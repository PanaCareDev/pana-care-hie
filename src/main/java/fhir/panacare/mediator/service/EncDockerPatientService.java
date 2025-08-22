package fhir.panacare.mediator.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import fhir.panacare.mediator.helpers.FHIRClientProvider;
import fhir.panacare.mediator.model.EncDoctorPatientAssignment;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EncDockerPatientService {

    private final IGenericClient client;

    public EncDockerPatientService(FHIRClientProvider fhirClientProvider) {
        this.client = fhirClientProvider.getClient();
    }

    public MethodOutcome assignDoctorToPatient(EncDoctorPatientAssignment model) {

        // --- Verify Patient exists ---
        try {
            client.read()
                    .resource(org.hl7.fhir.r4.model.Patient.class)
                    .withId(model.getPatientId())
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException("Patient with ID " + model.getPatientId() + " does not exist.");
        }

        // --- Verify Practitioner exists ---
        try {
            client.read()
                    .resource(org.hl7.fhir.r4.model.Practitioner.class)
                    .withId(model.getPractitionerId())
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException("Practitioner with ID " + model.getPractitionerId() + " does not exist.");
        }

        // --- Create Encounter ---
        Encounter encounter = new Encounter();
        encounter.setStatus(Encounter.EncounterStatus.INPROGRESS);

        // Class = Ambulatory
        encounter.setClass_(new Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                .setCode("AMB")
                .setDisplay("ambulatory"));

        // Subject = Patient
        encounter.setSubject(new Reference("Patient/" + model.getPatientId()));

        // Period
        Period period = new Period();
        period.setStartElement(new org.hl7.fhir.r4.model.DateTimeType(model.getStart()));
        period.setEndElement(new org.hl7.fhir.r4.model.DateTimeType(model.getEnd()));
        encounter.setPeriod(period);

        // Participant = Practitioner (attender)
        Encounter.EncounterParticipantComponent participant = new Encounter.EncounterParticipantComponent();
        participant.addType(new CodeableConcept().addCoding(
                new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/participant-type")
                        .setCode("ATND")
                        .setDisplay("attender")
        ));
        participant.setIndividual(new Reference("Practitioner/" + model.getPractitionerId()));
        encounter.addParticipant(participant);

        // --- Post to HAPI FHIR ---
        return client.create()
                .resource(encounter)
                .execute();
    }


}

package fhir.panacare.mediator.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import fhir.panacare.mediator.config.FhirProperties;
import fhir.panacare.mediator.model.PanaPatient;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PatientService {

    private final FhirContext fhirContext = FhirContext.forR4();
    private final IGenericClient client;

    public PatientService(FhirProperties fhirProperties) {
        String serverUrl = fhirProperties.getServerUrl();
        if (serverUrl == null || serverUrl.isEmpty()) {
            throw new IllegalArgumentException("FHIR server URL must not be null or empty");
        }
        this.client = fhirContext.newRestfulGenericClient(serverUrl);
    }

    public Bundle convertPatient(PanaPatient nonFhir) {

        // Create Patient
        Patient patient = new Patient();
        patient.addName(new HumanName()
                .setFamily(nonFhir.getLastName())
                .addGiven(nonFhir.getFirstName()));
        patient.setGender(Enumerations.AdministrativeGender.fromCode(nonFhir.getGender().toLowerCase()));
        patient.setBirthDateElement(new DateType(nonFhir.getDateOfBirth()));

        // Marital Status
        if (nonFhir.getMaritalStatus() != null) {
            patient.setMaritalStatus(new CodeableConcept().addCoding(
                    new Coding("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus",
                            nonFhir.getMaritalStatus(), null)
            ));
        }

        // Language
        if (nonFhir.getLanguage() != null) {
            patient.addCommunication()
                    .setLanguage(new CodeableConcept().addCoding(
                            new Coding("urn:ietf:bcp:47", nonFhir.getLanguage(), nonFhir.getLanguage())
                    ));
        }

        // Emergency Contact
        if (nonFhir.getEmergencyContactName() != null) {
            Patient.ContactComponent contact = new Patient.ContactComponent();
            contact.setName(new HumanName().setText(nonFhir.getEmergencyContactName()));
            contact.addTelecom(new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE)
                    .setValue(nonFhir.getEmergencyContactPhone()));
            contact.setRelationship(Arrays.asList(new CodeableConcept().setText(nonFhir.getEmergencyContactRelationship())));
            patient.addContact(contact);
        }

        // Create Observations
        Observation heightObs = createObservation(nonFhir.getHeightCm(), "cm", "Body Height", "8302-2");
        Observation weightObs = createObservation(nonFhir.getWeightKg(), "kg", "Body Weight", "29463-7");
        Observation bloodTypeObs = createStringObservation(nonFhir.getBloodType(), "Blood group", "883-9");

        //AllergyIntolerance
        AllergyIntolerance allergyIntolerance = new AllergyIntolerance();
        allergyIntolerance.setClinicalStatus(new CodeableConcept().addCoding(new Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical")
                .setCode("active")));
        allergyIntolerance.setCode(new CodeableConcept().addCoding(new Coding()
                .setCode("allergyCode")
                .setSystem("http://loinc.org")
                .setDisplay(nonFhir.getAllergies())));

        // Condition
        Condition condition = new Condition();
        condition.setSubject(new Reference(patient.getIdElement()));
        condition.setCode(new CodeableConcept().setText(nonFhir.getMedicalConditions()));

        // MedicationStatement
        MedicationStatement medication = new MedicationStatement();
        medication.setSubject(new Reference(patient.getIdElement()));
        medication.setMedication(new CodeableConcept().setText(nonFhir.getMedications()));
        medication.setStatus(MedicationStatement.MedicationStatementStatus.ACTIVE);
        //
        Coverage coverage = new Coverage();
        coverage.setIdentifier(Arrays.asList(new Identifier()
                .setSystem("http://www.acme.com/coverage/identifiers")
                .setValue(nonFhir.getInsurancePolicyNumber())));
       coverage.setStatus(Coverage.CoverageStatus.ACTIVE);
       coverage.setBeneficiary(new Reference(patient.getIdElement()));
        
        // Bundle
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.TRANSACTION);
        bundle.addEntry().setResource(patient).getRequest()
                .setMethod(Bundle.HTTPVerb.POST)
                .setUrl("Patient");
        bundle.addEntry().setResource(heightObs).getRequest()
                .setMethod(Bundle.HTTPVerb.POST)
                .setUrl("Observation");
        bundle.addEntry().setResource(weightObs).getRequest()
                .setMethod(Bundle.HTTPVerb.POST)
                .setUrl("Observation");
        bundle.addEntry().setResource(bloodTypeObs).getRequest()
                .setMethod(Bundle.HTTPVerb.POST)
                .setUrl("Observation");
        bundle.addEntry().setResource(allergyIntolerance).getRequest()
                .setMethod(Bundle.HTTPVerb.POST)
                .setUrl("AllergyIntolerance");
        bundle.addEntry().setResource(condition).getRequest()
                .setMethod(Bundle.HTTPVerb.POST)
                .setUrl("Condition");
        bundle.addEntry().setResource(medication).getRequest()
                .setMethod(Bundle.HTTPVerb.POST)
                .setUrl("MedicationStatement");
        bundle.addEntry().setResource(coverage).getRequest()
                .setMethod(Bundle.HTTPVerb.POST)
                .setUrl("Coverage");
        return client.transaction().withBundle(bundle).execute();
    }

    private Observation createObservation(double value, String unit, String display, String loincCode) {
        Observation obs = new Observation();
        obs.setStatus(Observation.ObservationStatus.FINAL);
        obs.getCode().addCoding(new Coding("http://loinc.org", loincCode, display));
        obs.setValue(new Quantity().setValue(value).setUnit(unit));
        return obs;
    }

    private Observation createStringObservation(String value, String display, String loincCode) {
        Observation obs = new Observation();
        obs.setStatus(Observation.ObservationStatus.FINAL);
        if (loincCode != null) {
            obs.getCode().addCoding(new Coding("http://loinc.org", loincCode, display));
        } else {
            obs.getCode().setText(display);
        }
        obs.setValue(new StringType(value));
        return obs;
    }
}

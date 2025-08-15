package fhir.panacare.mediator.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import fhir.panacare.mediator.config.FhirProperties;
import fhir.panacare.mediator.model.PanaPatient;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

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

    public MethodOutcome convertPatient(PanaPatient nonFhir) {
        org.hl7.fhir.r4.model.Patient patient = new Patient();

        HumanName name = new HumanName()
                .setFamily(nonFhir.getLastName())
                .addGiven(nonFhir.getFirstName());
        patient.addName(name);
        patient.setGender(Enumerations.AdministrativeGender.fromCode(nonFhir.getGender().toLowerCase()));
        patient.setBirthDateElement(new DateType(nonFhir.getBirthDate()));

        return client.create()
                .resource(patient)
                .execute();
    }
}

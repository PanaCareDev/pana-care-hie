package fhir.panacare.mediator.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import fhir.panacare.mediator.helpers.FHIRClientProvider;
import fhir.panacare.mediator.model.Practitioner;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PractitionerService {

    private final IGenericClient client;

    public PractitionerService(FHIRClientProvider fhirClientProvider) {
        this.client = fhirClientProvider.getClient();
    }

    public MethodOutcome createPractitioner(Practitioner model) {
        org.hl7.fhir.r4.model.Practitioner practitioner = new org.hl7.fhir.r4.model.Practitioner();

        // Identifier = license_number
        practitioner.addIdentifier ()
                .setSystem("http://hospital.example.org/practitioners")
                .setValue(model.getLicense_number());

        // Active status
        practitioner.setActive(model.isIs_available());

        // Qualification (specialty, education)
        org.hl7.fhir.r4.model.Practitioner.PractitionerQualificationComponent qualification = new org.hl7.fhir.r4.model.Practitioner().addQualification();
        qualification.addIdentifier()
                .setSystem("http://example.org/qualification-ids")
                .setValue(model.getSpecialty().toUpperCase());

        CodeableConcept specialtyConcept = new CodeableConcept();
        specialtyConcept.addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/practitioner-specialty")
                .setCode("394579002") // SNOMED for Cardiology
                .setDisplay(model.getSpecialty());
        specialtyConcept.setText(model.getSpecialty());
        qualification.setCode(specialtyConcept);

        Period period = new Period();
        period.setStartElement(new DateTimeType(model.getEducation().getStart_date()));
        period.setEndElement(new DateTimeType(model.getEducation().getEnd_date()));
        qualification.setPeriod(period);

        qualification.setIssuer(new Reference().setDisplay(model.getEducation().getInstitution()));
        practitioner.addQualification(qualification);

        // Communication languages
        Arrays.stream(model.getCommunication_languages().split(","))
                .map(String::trim)
                .forEach(lang -> {
                    CodeableConcept language = new CodeableConcept();
                    if (lang.equalsIgnoreCase("English")) {
                        language.addCoding().setSystem("urn:ietf:bcp:47").setCode("en").setDisplay("English");
                    } else if (lang.equalsIgnoreCase("Spanish")) {
                        language.addCoding().setSystem("urn:ietf:bcp:47").setCode("es").setDisplay("Spanish");
                    } else {
                        language.setText(lang);
                    }
                    practitioner.addCommunication(language);
                });
        // POST to FHIR server
        return client.create().resource(practitioner).execute();
    }
}
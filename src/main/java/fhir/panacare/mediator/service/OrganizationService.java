package fhir.panacare.mediator.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import fhir.panacare.mediator.helpers.FHIRClientProvider;
import fhir.panacare.mediator.model.Organization;
import fhir.panacare.mediator.model.Practitioner;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

    private final IGenericClient client;

    public OrganizationService(FHIRClientProvider fhirClientProvider) {
        this.client = fhirClientProvider.getClient();
    }

    public org.hl7.fhir.r4.model.Organization convertOrganizationToFHIR(Organization model) {
        org.hl7.fhir.r4.model.Organization organization = new org.hl7.fhir.r4.model.Organization();

        organization.setName(model.getName());
        organization.setActive(model.isActive());

        // Telecom: Phone
        if (model.getPhoneNumber() != null) {
            organization.addTelecom(new ContactPoint()
                    .setSystem(ContactPoint.ContactPointSystem.PHONE)
                    .setValue(model.getPhoneNumber()));
        }

        // Telecom: Email
        if (model.getEmail() != null) {
            organization.addTelecom(new ContactPoint()
                    .setSystem(ContactPoint.ContactPointSystem.EMAIL)
                    .setValue(model.getEmail()));
        }

        // Telecom: Website
        if (model.getWebsite() != null) {
            organization.addTelecom(new ContactPoint()
                    .setSystem(ContactPoint.ContactPointSystem.URL)
                    .setValue(model.getWebsite()));
        }

        // Hospital Category
        if (model.getCategory() != null) {
            organization.addType(new CodeableConcept().addCoding(
                            new Coding()
                                    .setSystem("http://terminology.hl7.org/CodeSystem/organization-type")
                                    .setCode("prov")
                                    .setDisplay(model.getCategory())
                    )
            );
        }

        // Address
        Address address = new Address();
        if (model.getAddress() != null) {
            address.addLine(model.getAddress());
        }
        address.setCity(model.getCity());
        address.setState(model.getState());
        address.setPostalCode(model.getPostalCode());
        address.setCountry(model.getCountry());
        organization.addAddress(address);

        return organization;
    }
    public MethodOutcome postToServer(Organization hospital) {
        org.hl7.fhir.r4.model.Organization org = convertOrganizationToFHIR(hospital);

        return client
                .create()
                .resource(org)
                .execute();
    }

}

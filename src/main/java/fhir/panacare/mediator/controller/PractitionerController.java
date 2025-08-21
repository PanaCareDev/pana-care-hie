package fhir.panacare.mediator.controller;

import ca.uhn.fhir.rest.api.MethodOutcome;
import fhir.panacare.mediator.service.PractitionerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/practitioner")
public class PractitionerController {

    @Autowired
    PractitionerService service;

    @PostMapping("/create")
    public String convertPractioner(@RequestBody fhir.panacare.mediator.model.Practitioner practitioner){
        MethodOutcome outcome = service.createPractitioner(practitioner);

        // Extract created Practitioner ID
        String resourceId = outcome.getId() != null ? outcome.getId().getValue() : "Unknown ID";

        return "FHIR Practitioner resource created successfully with ID: " + resourceId;
    }
}

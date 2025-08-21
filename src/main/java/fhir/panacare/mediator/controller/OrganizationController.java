package fhir.panacare.mediator.controller;

import ca.uhn.fhir.rest.api.MethodOutcome;
import fhir.panacare.mediator.model.Organization;
import fhir.panacare.mediator.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService service;

    @PostMapping("/create")
    public String createHospital(@RequestBody Organization hospital) {
        MethodOutcome outcome = service.postToServer(hospital);
        return "FHIR Organization created with ID: " +
                outcome.getId().getIdPart();
    }

}

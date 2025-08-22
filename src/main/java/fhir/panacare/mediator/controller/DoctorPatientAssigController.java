package fhir.panacare.mediator.controller;

import ca.uhn.fhir.rest.api.MethodOutcome;
import fhir.panacare.mediator.model.EncDoctorPatientAssignment;
import fhir.panacare.mediator.service.EncDockerPatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/encounter")
public class DoctorPatientAssigController {

    @Autowired
    private EncDockerPatientService dockerPatientService;

    @PostMapping("/create")
    public String createDoctorToPatient(@RequestBody EncDoctorPatientAssignment encDoctorPatientAssignment){
        MethodOutcome outcome = dockerPatientService.assignDoctorToPatient(encDoctorPatientAssignment);
        return "FHIR Encounter created with ID: " + outcome.getResource();
    }
}

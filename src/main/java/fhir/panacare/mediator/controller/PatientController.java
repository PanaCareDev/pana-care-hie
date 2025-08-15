package fhir.panacare.mediator.controller;

import fhir.panacare.mediator.model.PanaPatient;
import fhir.panacare.mediator.service.PatientService;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private PatientService service;

    @PostMapping("/post")
    public String convertPatient(@RequestBody PanaPatient patient){
        var outcome = service.convertPatient(patient);
        IdType id = (IdType) outcome.getId();
        return "FHIR Patient Created with ID: " + id.getIdPart();
    }

}

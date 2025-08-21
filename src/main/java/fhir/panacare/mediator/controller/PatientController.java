package fhir.panacare.mediator.controller;

import fhir.panacare.mediator.model.PanaPatient;
import fhir.panacare.mediator.service.PatientService;
import org.hl7.fhir.r4.model.Bundle;
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

    @PostMapping("/create")
    public String convertPatient(@RequestBody PanaPatient patient){
        Bundle outcomeBundle = service.convertPatient(patient);
        return "FHIR Bundle Created with " + outcomeBundle.getEntry().size() + " resources.";
    }

}

package fhir.panacare.mediator.model;

public class EncDoctorPatientAssignment {
    private String patientId;
    private String practitionerId;
    private String start;
    private String end;

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPractitionerId() { return practitionerId; }
    public void setPractitionerId(String practitionerId) { this.practitionerId = practitionerId; }

    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }

    public String getEnd() { return end; }
    public void setEnd(String end) { this.end = end; }
}

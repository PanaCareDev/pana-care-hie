package fhir.panacare.mediator.model;

public class Practitioner {

    private String specialty;

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getLicense_number() {
        return license_number;
    }

    public void setLicense_number(String license_number) {
        this.license_number = license_number;
    }

    public int getExperience_years() {
        return experience_years;
    }

    public void setExperience_years(int experience_years) {
        this.experience_years = experience_years;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Education getEducation() {
        return education;
    }

    public void setEducation(Education education) {
        this.education = education;
    }

    public boolean isIs_available() {
        return is_available;
    }

    public void setIs_available(boolean is_available) {
        this.is_available = is_available;
    }

    public String getCommunication_languages() {
        return communication_languages;
    }

    public void setCommunication_languages(String communication_languages) {
        this.communication_languages = communication_languages;
    }

    private String license_number;
    private int experience_years;
    private String bio;
    private Education education;
    private boolean is_available;
    private String communication_languages;

    public static class Education {
        private String level_of_education;
        private String field;

        public String getLevel_of_education() {
            return level_of_education;
        }

        public void setLevel_of_education(String level_of_education) {
            this.level_of_education = level_of_education;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getInstitution() {
            return institution;
        }

        public void setInstitution(String institution) {
            this.institution = institution;
        }

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        private String institution;
        private String start_date;
        private String end_date;
    }

}

class Student {
    private String academicId;
    private String name;
    private String birthDate;
    private String address;
    private String contactDetails;
    private String course;

    public Student(String academicId, String name, String birthDate, String address, String contactDetails, String course) {
        this.academicId = academicId;
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
        this.contactDetails = contactDetails;
        this.course = course;
    }

    public String getAcademicId() {
        return academicId;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public String getCourse() {
        return course;
    }

    public String getName() {
        return name;
    }

    public void setAcademicId(String academicId) {
        this.academicId = academicId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setName(String name) {
        this.name = name;
    }
}

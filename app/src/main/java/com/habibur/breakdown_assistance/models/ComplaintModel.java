package com.habibur.breakdown_assistance.models;

public class ComplaintModel {

    private String id;
    private UserModel submittedBy;
    private String contactInfo;
    private String description;

    public ComplaintModel() {
    }

    public ComplaintModel(String id, UserModel submittedBy, String contactInfo, String description) {
        this.id = id;
        this.submittedBy = submittedBy;
        this.contactInfo = contactInfo;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserModel getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(UserModel submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

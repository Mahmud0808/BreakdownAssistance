package com.habibur.breakdown_assistance.models;

public class RequestServiceModel {

    private String id;
    private UserModel requestedBy;
    private ServiceModel service;
    private String currentLocation;
    private GarageModel garage;
    private String additionalInfo;
    private RequestStatus status;
    private UserModel assignedTo;
    private String feedback;
    private String review;
    private Long submitTime;

    public RequestServiceModel() {
    }

    public RequestServiceModel(String id, UserModel requestedBy, ServiceModel service, String currentLocation, GarageModel garage, String additionalInfo, RequestStatus status, UserModel assignedTo, String feedback, String review) {
        this.id = id;
        this.requestedBy = requestedBy;
        this.service = service;
        this.currentLocation = currentLocation;
        this.garage = garage;
        this.additionalInfo = additionalInfo;
        this.status = status;
        this.assignedTo = assignedTo;
        this.feedback = feedback;
        this.review = review;
        this.submitTime = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserModel getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(UserModel requestedBy) {
        this.requestedBy = requestedBy;
    }

    public ServiceModel getService() {
        return service;
    }

    public void setService(ServiceModel service) {
        this.service = service;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public GarageModel getGarage() {
        return garage;
    }

    public void setGarage(GarageModel garage) {
        this.garage = garage;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public UserModel getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserModel assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Long getSubmitTime() {
        return submitTime;
    }
}

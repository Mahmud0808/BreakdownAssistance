package com.habibur.breakdown_assistance.models;

public class RequestModel {

    private String id;
    private UserModel user;
    private ServiceModel service;
    private GarageModel garage;
    private String additionalInfo;
    private RequestStatus status;
    private String feedback;

    public RequestModel() {
    }

    public RequestModel(String id, UserModel user, ServiceModel service, GarageModel garage, String additionalInfo, RequestStatus status, String feedback) {
        this.id = id;
        this.user = user;
        this.service = service;
        this.garage = garage;
        this.additionalInfo = additionalInfo;
        this.status = status;
        this.feedback = feedback;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public ServiceModel getService() {
        return service;
    }

    public void setService(ServiceModel service) {
        this.service = service;
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

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}

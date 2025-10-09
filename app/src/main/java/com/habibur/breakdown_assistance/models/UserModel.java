package com.habibur.breakdown_assistance.models;

public class UserModel {

    private String id;
    private String name;
    private String phone;
    private String vehicleCompany;
    private String vehicleModel;
    private final AccountType accountType;

    public UserModel() {
        this.accountType = AccountType.USER;
    }

    public UserModel(String id, String name, String phone, String vehicleCompany, String vehicleModel) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.vehicleCompany = vehicleCompany;
        this.vehicleModel = vehicleModel;
        this.accountType = AccountType.USER;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVehicleCompany() {
        return vehicleCompany;
    }

    public void setVehicleCompany(String vehicleCompany) {
        this.vehicleCompany = vehicleCompany;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public AccountType getAccountType() {
        return accountType;
    }
}

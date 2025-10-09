package com.habibur.breakdown_assistance.models;

import android.os.Parcel;

import java.io.Serializable;

public class ServiceModel implements Serializable {

    private String id;
    private String image;
    private String title;
    private String description;
    private int minimumPrice;
    private int serviceCharge;
    private int durationHours;

    public ServiceModel() {
    }

    public ServiceModel(String id, String image, String title, String description, int minimumPrice, int serviceCharge, int durationHours) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.minimumPrice = minimumPrice;
        this.serviceCharge = serviceCharge;
        this.durationHours = durationHours;
    }

    protected ServiceModel(Parcel in) {
        id = in.readString();
        image = in.readString();
        title = in.readString();
        description = in.readString();
        minimumPrice = in.readInt();
        serviceCharge = in.readInt();
        durationHours = in.readInt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(int minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public int getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(int serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }
}

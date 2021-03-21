package com.secretdevbd.emergencyambulance.models;

public class Doctor {
    long id;
    String name, designation, details;
    long hospital_id;

    public Doctor() {
    }

    public Doctor(long id, String name, String designation, String details, long hospital_id) {
        this.id = id;
        this.name = name;
        this.designation = designation;
        this.details = details;
        this.hospital_id = hospital_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getHospital_id() {
        return hospital_id;
    }

    public void setHospital_id(long hospital_id) {
        this.hospital_id = hospital_id;
    }
}

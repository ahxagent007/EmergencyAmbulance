package com.secretdevbd.emergencyambulance.models;

public class Hospital {

    int id;
    String category, name, title;
    double latitude, Longitude;

    public Hospital() {
    }

    public Hospital(int id, String category, String name, String title, double latitude, double longitude) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.title = title;
        this.latitude = latitude;
        Longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}

package com.secretdevbd.emergencyambulance.models;

public class Hospital {

    long id;
    String category, name, title;
    double latitude, Longitude;
    public double distance_from_user;

    public Hospital() {
    }

    public double getDistance_from_user() {
        return distance_from_user;
    }

    public void setDistance_from_user(double distance_from_user) {
        this.distance_from_user = distance_from_user;
    }

    public Hospital(long id, String category, String name, String title, double latitude, double longitude) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.title = title;
        this.latitude = latitude;
        Longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

package com.secretdevbd.emergencyambulance.models;

import java.io.Serializable;

public class User implements Serializable {

    String id;
    String email;
    String pw;
    String role;

    public User() {
    }

    public User(String id, String email, String pw, String role) {
        this.id = id;
        this.email = email;
        this.pw = pw;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

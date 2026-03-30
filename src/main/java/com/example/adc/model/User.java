package com.example.adc.model;

public class User {
    private String username;
    private String password;
    private String phone;
    private String address;
    private String role;

    public User() {}

    public User(String username, String password, String phone, String address, String role) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getAddress() {
        return this.address;
    }

    public String getRole() {
        return this.role;
    }

}

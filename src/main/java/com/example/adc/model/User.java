package com.example.adc.model;

public class User {
    private String userId;
    private String username;
    private String passwordHash;
    private String email;
    private String phone;
    private String address;
    private String role;

public User(){}

public User(String userId, String username, String password, String email, String phone, String address, String role){
    this.userId = userId;
    this.username = username;
    this.passwordHash = password;
    this.email = email;
    this.phone = phone;
    this.address = address;
    this.role = role;
}


    
    public String getUserId(){return this.userId;}

    public String getUsername(){return this.username;}

    public String getPasswordHash(){return this.passwordHash;}

    public String getEmail(){return this.email;}

    public String getPhone(){return this.phone;}

    public String getAddress(){return this.address;}

    public String getRole(){return this.role;}

}

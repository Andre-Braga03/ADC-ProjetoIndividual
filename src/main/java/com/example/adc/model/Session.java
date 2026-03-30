package com.example.adc.model;

public class Session {

    private String tokenId;
    private String username;
    private String role;
    private long issuedAt;
    private long expiresAt;

    public Session(){}

    public Session(String tokenId, String username, String role, long issuedAt, long expiresAt){
        this.tokenId = tokenId;
        this.username = username;
        this.role = role;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public String getTokenId(){return this.tokenId;}

    public String getUsername(){return this.username;}

    public String getRole(){return this.role;}

    public long getIssuedAt(){return this.issuedAt;}

    public long getExpiresAt(){return this.expiresAt;}

    public void setRole(String role){this.role = role;}
    
}

package com.example.adc.model;

public class Session {

    private String tokenId;
    private String userId;
    private String role;
    private String issuedAt;
    private String expiresAt;

    public Session(){}

    public Session(String tokenId, String userId, String role, String issuedAt, String expiresAt){
        this.tokenId = tokenId;
        this.userId = userId;
        this.role = role;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public String getTokenId(){return this.tokenId;}

    public String getUserId(){return this.userId;}

    public String getRole(){return this.role;}

    public String getIssuedAt(){return this.issuedAt;}

    public String getExpiresAt(){return this.expiresAt;}
    
}

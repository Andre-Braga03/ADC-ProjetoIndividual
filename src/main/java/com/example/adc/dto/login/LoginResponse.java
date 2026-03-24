package com.example.adc.dto.login;

public class LoginResponse {

    public String status;

    public LoginData data;

    public static class LoginData{
        public TokenData token;
    }

    public static class TokenData{
        public String tokenId;
        public String userId;
        public String role;
        public String issuedAt;
        public String expiresAt;
    }
}

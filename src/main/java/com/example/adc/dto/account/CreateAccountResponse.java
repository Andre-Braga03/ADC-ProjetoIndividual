package com.example.adc.dto.account;

public class CreateAccountResponse {

    public String status;

    public AccountData data;

    public static class AccountData{
        public String username;
        public String role;

    }
    
}

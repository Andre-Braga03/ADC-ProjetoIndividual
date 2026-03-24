package com.example.adc.dto.account;

public class CreateAccountRequest {


    /**
     * Input data for the create account request
     */
    public CreateAccountInput input;

    /**
     * 
     * Input data for the create account request
     * */
    public static class CreateAccountInput {
       public String username;
       public String password;
       public String confirmation;
       public String email;
       public String phone;
       public String address;
       public String role;

    }
    
}

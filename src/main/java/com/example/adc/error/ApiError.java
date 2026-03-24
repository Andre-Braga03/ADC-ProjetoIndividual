package com.example.adc.error;

public class ApiError {

    private final String code;
    private final String message;


    public ApiError(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode(){return this.code;}

    public String getMessage(){return this.message;}
    
}

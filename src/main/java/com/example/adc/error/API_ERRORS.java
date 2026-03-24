package com.example.adc.error;

public class API_ERRORS {

    public static final ApiError INVALID_CREDENTIALS =
    new ApiError("9900", "The username-password pair is not valid");
    
    public static final ApiError USER_ALREADY_EXISTS =
    new ApiError("9901","Error in creating an account because the username already exists");
    
    public static final ApiError USER_NOT_FOUND = 
    new ApiError("9902","The username referred in the operation doesn’t exist in registered accounts");

    public static final ApiError INVALID_TOKEN =
    new ApiError("9903","The operation is called with an invalid token (wrong format for example)");

    public static final ApiError TOKEN_EXPIRED =
    new ApiError("9904","The operation is called with a token that is expired");

    public static final ApiError UNAUTHORIZED =
    new ApiError("9905", "The operation is not allowed for the user role");

    public static final ApiError INVALID_INPUT =
    new ApiError("9906","The call is using input data not following the correct specification");

    public static final ApiError FORBIDDEN = 
    new ApiError("9907", "The operation generated a forbidden error by other reason");

    private API_ERRORS(){}
}

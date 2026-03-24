package com.example.adc.resource;


import com.example.adc.dto.account.CreateAccountRequest;
import com.example.adc.dto.login.LoginRequest;
import com.example.adc.service.AccountAuthService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;



@Path("/")
public class AccountResource {

    private final AccountAuthService accountService = new AccountAuthService();
    @POST
    @Path("/createaccount")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Object createAccount(CreateAccountRequest request){
          return accountService.createAccount(request);  
    }


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Object login(LoginRequest request){
       return accountService.login(request);
    }
}

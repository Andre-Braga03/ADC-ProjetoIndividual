package com.example.adc.resource;


import com.example.adc.service.AccountAuthService;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;



//*
// This class is the resource for the account authentication service.
// It handles the requests for the account authentication service.
// It is used to create a new account, login, show users, delete an account, modify an account, show authentication sessions, show user role, change user role, change user password, and logout.
// */
@Path("/")
public class AccountResource {
   

    private final AccountAuthService accountService = new AccountAuthService();
    @POST
    @Path("/createaccount")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Response createAccount(JsonObject request){
          return accountService.createAccount(request);  
    }


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(JsonObject request){
       return accountService.login(request);
    }

    @POST
    @Path("/showusers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response showUsers(JsonObject request){
       return accountService.showUsers(request);
    }

    @POST
    @Path("/deleteaccount")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(JsonObject request){
       return accountService.delete(request);
    }

    @POST
    @Path("/modaccount")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyAttribute(JsonObject request){
       return accountService.modifyAttribute(request);
    }
    @POST
    @Path("/showauthsessions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response showAuthSessions(JsonObject request){
       return accountService.showAuthSessions(request);
    }

    @POST
    @Path("/showuserrole")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response showUserRole(JsonObject request){
       return accountService.showUserRole(request);
    }

    @POST
    @Path("/changeuserrole")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeUserRole(JsonObject request){
       return accountService.changeUserRole(request);
    }

    @POST
    @Path("/changeuserpwd")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeUserPassword(JsonObject request){
       return accountService.changeUserPassword(request);
    }

    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(JsonObject request){
       return accountService.logout(request);
    }
}

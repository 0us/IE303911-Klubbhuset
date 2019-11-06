package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.service.AuthenticationService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

    @Inject
    AuthenticationService authenticationService;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("email") String email,
                          @FormParam("password") String password,
                          @Context HttpServletRequest context) {
        return authenticationService.login(email, password, context);
    }

    @DELETE
    public Response logout() {
        return authenticationService.logout();
    }

    @GET
    @RolesAllowed(value = {Group.USER})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser() {
        return authenticationService.getCurrentUser();
    }
}


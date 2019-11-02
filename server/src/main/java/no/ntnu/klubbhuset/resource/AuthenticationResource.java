package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.AuthenticationService;
import no.ntnu.klubbhuset.domain.Group;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(value = {Group.USER})
public class AuthenticationResource {

    @Inject
    AuthenticationService authenticationService;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("username") String username,
                          @FormParam("password") String password,
                          @Context HttpServletRequest context) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "LOGIN @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        return authenticationService.login(username, password, context);
    }

    @DELETE
    public Response logout() {
        return authenticationService.logout();
    }
}


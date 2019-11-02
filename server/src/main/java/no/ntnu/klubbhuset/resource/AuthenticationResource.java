package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.AuthenticationService;
import no.ntnu.klubbhuset.domain.Group;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(value = {Group.USER})
public class AuthenticationResource {

    @Inject
    AuthenticationService authenticationService;

//    @POST
//    public Response login(@FormParam("username") String username,
//                          @FormParam("password") String password) {
//        System.out.println("AuthenticationResource.login");
//        return authenticationService.login(username, password);
//    }

//    @DELETE
//    public Response logout() {
//        return authenticationService.logout();
//    }
}


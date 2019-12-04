package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.domain.User;
import no.ntnu.klubbhuset.service.UserService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Stateless
@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    JsonWebToken principal;


    @GET
    @RolesAllowed(value = {Group.USER})
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        return userService.getCurrentUser(securityContext);
    }

    @POST
    public Response createNewUserProfile(User user) {
        return userService.createNewUser(user);
    }

    @PUT
    @RolesAllowed(value = {Group.USER})
    public Response updateUser(User user) {
        return userService.updateUser(user);
    }

    @DELETE
    @RolesAllowed(value = {Group.USER})
    public Response deleteUser() {
        return userService.deleteUser();
    }
}

package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.service.UserService;
import org.eclipse.microprofile.openapi.models.headers.Header;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPart;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;

@Stateless
@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @GET
    @RolesAllowed(value = {Group.USER})
    public Response getCurrentUser() {
        return userService.getCurrentUser();
    }

    @POST
    public Response createNewUserProfile(@FormDataParam("firstname") String firstname,
                                         @FormDataParam("lastname") String lastname,
                                         @FormDataParam("email") String email,
                                         @FormDataParam("password") String password,
                                         @FormDataParam("phonenumber") String phonenumber,
                                         MultiPart multiPart) {
        return userService.createNewUser(firstname, lastname, email, password, phonenumber, multiPart);
    }

    // todo the user id should be sent somehow
    @DELETE
    @RolesAllowed(value = {Group.USER})
    public Response deleteUser() {
        return userService.deleteUser();
    }
}

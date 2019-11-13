package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.domain.User;
import no.ntnu.klubbhuset.service.UserService;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    public Response createNewUserProfile(User user) {
        return userService.createNewUser(user);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createNewUserProfile(@FormDataParam("firstname") String firstname,
                                         @FormDataParam("lastname") String lastname,
                                         @FormDataParam("email") String email,
                                         @FormDataParam("password") String password,
                                         @FormDataParam("phonenumber") String phonenumber,
                                         FormDataMultiPart multiPart) {
        return userService.createNewUser(firstname, lastname, email, password, phonenumber, multiPart);
    }

    // todo the user id should be sent somehow
    @DELETE
    @RolesAllowed(value = {Group.USER})
    public Response deleteUser() {
        return userService.deleteUser();
    }
}

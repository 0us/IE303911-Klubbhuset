package no.ntnu.klubbhuset.service;

import no.ntnu.klubbhuset.SaveImages;
import no.ntnu.klubbhuset.domain.Image;
import no.ntnu.klubbhuset.domain.User;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;

public class UserService {

    public static final String PROFILE_PICTURE = "profilePicture";
    @Inject
    EntityManager entityManager;

    @Inject
    SaveImages saveImages;

    @Inject
    JsonWebToken principal;

    public Response getCurrentUser() {
        int uid = principal.getClaim("userId"); // todo this, or similar value, should be part of the token

        User user = entityManager.find(User.class, uid);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Jsonb jsonb = JsonbBuilder.create();
        jsonb.toJson(user);

        return Response.ok(user).build();
    }

    public Response createNewUser(String firstname, String lastname, String email, String password, String phonenumber, FormDataMultiPart multiPart) {

        // Check if user with same email exist
        if (!entityManager.createQuery("select u from User u where u.email = :email").getResultList().isEmpty()) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("User with same email address allready exist").build();
        }

        User user = new User();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhonenumber(phonenumber);

        // todo save image
        InputStream inputStream = multiPart.getField(PROFILE_PICTURE).getValueAs(InputStream.class);
        FormDataContentDisposition fileDetails = multiPart.getField(PROFILE_PICTURE).getValueAs(FormDataContentDisposition.class);
        String filename = fileDetails.getFileName();
        String target = user.getUid() + File.separator + PROFILE_PICTURE + File.separator + filename; // todo save location directory uid?

        Image avatar = saveImages.saveImage(inputStream, target);

        user.setAvatar(avatar);

        entityManager.persist(user);

        return Response.status(Response.Status.CREATED).build();


    }

    public Response deleteUser() {
        User user = entityManager.find(User.class, principal.getName());

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }

        entityManager.remove(user);
        return Response.ok("User removed from system").build();
    }
}

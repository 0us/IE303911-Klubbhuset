package no.ntnu.klubbhuset.service;

import no.ntnu.klubbhuset.DatasourceProducer;
import no.ntnu.klubbhuset.SaveImages;
import no.ntnu.klubbhuset.domain.Image;
import no.ntnu.klubbhuset.domain.User;
import no.ntnu.klubbhuset.resource.UserResource;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {

    public static final String PROFILE_PICTURE = "profilePicture";

    @PersistenceContext
    EntityManager entityManager;

    @Resource(lookup = DatasourceProducer.JNDI_NAME)
    DataSource dataSource;

    @Inject
    SaveImages saveImages;

    @Inject
    JsonWebToken principal;

    public Response getCurrentUser() {
        int uid = principal.getClaim("userId"); // todo this, or similar value, should be part of the token

        User user = entityManager.find(User.class, uid);

        if ( user == null ) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Jsonb jsonb = JsonbBuilder.create();
        jsonb.toJson(user);

        return Response.ok(user).build();
    }

    public Response createNewUser(String firstname, String lastname, String email, String password, String phonenumber, FormDataMultiPart multiPart) {
        Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "UserService.createNewUser: was called");


        // Check if user with same email exist
//        if (!entityManager.createQuery("select u from User u where u.email = :email").getResultList().isEmpty()) {
//            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("User with same email address allready exist").build();
//        }

        User user = new User();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhonenumber(phonenumber);
        entityManager.persist(user);

        // todo save image
        if ( multiPart.getField(PROFILE_PICTURE) != null ) {
            InputStream inputStream = multiPart.getField(PROFILE_PICTURE).getValueAs(InputStream.class);
            System.out.println("UserService.createNewUser: saving image");

            ContentDisposition fileDetails = multiPart.getField(PROFILE_PICTURE).getContentDisposition();
            String filename = fileDetails.getFileName();
            String target = user.getUid() + File.separator + PROFILE_PICTURE; // todo save location directory uid?

            Image avatar = saveImages.saveImage(inputStream, target, filename);
            avatar.setUser(user);
//            user.setAvatar(avatar);
//            entityManager.merge(user);
            entityManager.persist(avatar);
        }

        return Response.status(Response.Status.CREATED).entity(user).build();


    }

    public Response deleteUser() {
        User user = entityManager.find(User.class, principal.getName());

        if ( user == null ) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }

        entityManager.remove(user);
        return Response.ok("User removed from system").build();
    }
}

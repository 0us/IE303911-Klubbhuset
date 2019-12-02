package no.ntnu.klubbhuset.service;

import lombok.extern.java.Log;
import no.ntnu.klubbhuset.DatasourceProducer;
import no.ntnu.klubbhuset.SaveImages;
import no.ntnu.klubbhuset.domain.Image;
import no.ntnu.klubbhuset.domain.SecurityGroup;
import no.ntnu.klubbhuset.domain.User;
import no.ntnu.klubbhuset.resource.UserResource;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log
@Stateless
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

    @Inject
    PasswordHash hasher;

    public Response getCurrentUser(SecurityContext securityContext) {
        String email = principal.getClaim("sub");

        if ( email == null ) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        User user = entityManager.find(User.class, email);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Jsonb jsonb = JsonbBuilder.create();
        jsonb.toJson(user);

        return Response.ok(user).build();
    }

    /**
     * Create new user from json.
     *
     * @param user the user
     * @return the response Returns  Created if created. Forbidden if user is null
     */
    public Response createNewUser(User user) {
        log.log(Level.INFO, "UserService.CreateUser: was called. Json edition");
        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        SecurityGroup securityGroup = entityManager.find(SecurityGroup.class, SecurityGroup.USER);
        user.addSecurityGroup(securityGroup);
        String hashedpw = hashPassword(user.getPassword());
        user.setPassword(hashedpw);
        entityManager.persist(user);

        return Response.status(Response.Status.CREATED).build();
    }

    private String hashPassword(String password) {
        return hasher.generate(password.toCharArray());
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
        user.setPassword(hasher.generate(password.toCharArray()));
        user.setPhonenumber(phonenumber);
        SecurityGroup securityGroup = entityManager.find(SecurityGroup.class, SecurityGroup.USER);
        user.addSecurityGroup(securityGroup);
        entityManager.persist(user);

        FormDataBodyPart bodyPart = multiPart.getField(PROFILE_PICTURE);
        if ( bodyPart != null ) {
            if ( !saveImages.checkBodyPartIsImage(bodyPart) ) {
                entityManager.remove(user);  // the user is already persisted to the database. Since the file uploaded is not an image, removing the user is done
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                        .entity("File must be image, no image was uploaded")
                        .build();
            }

            InputStream inputStream = bodyPart.getValueAs(InputStream.class);
            System.out.println("UserService.createNewUser: saving image");

            ContentDisposition fileDetails = bodyPart.getContentDisposition();
            String filename = fileDetails.getFileName();
            String target = user.getEmail() + File.separator + PROFILE_PICTURE;

            Image avatar = saveImages.saveImage(inputStream, target, filename);
            coupleImageAndOrganization(user, avatar);
        }

        return Response.status(Response.Status.CREATED)
                .entity(user)
                .build();
    }

    public Response deleteUser() {
        User user = entityManager.find(User.class, principal.getName());

        if ( user == null ) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }

        entityManager.remove(user);
        return Response.ok("User removed from system").build();
    }


    // --- Private methods below --- //
    private void coupleImageAndOrganization(User user, Image profilePicture) {
        String email = user.getEmail();
        long iid = profilePicture.getIid();
        String query = "update auser set iid = " + iid + " where email = " + email;
        System.out.println("query = " + query);
        entityManager.createNativeQuery(query).executeUpdate();
    }

    public Response updateUser(User newUser) {
        User oldUser = entityManager.find(User.class, principal.getName());
        if(oldUser == null ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No user found").build();
        }

        if(newUser.getFirstName() != null) {
            oldUser.setFirstName(newUser.getFirstName());
        }

        if(newUser.getLastName() != null) {
            oldUser.setLastName(newUser.getLastName());
        }

        if(newUser.getPassword() != null) {
            String hashedPassword = hashPassword(newUser.getPassword());
            oldUser.setPassword(hashedPassword);
        }

        if(newUser.getPhonenumber() != null) {
            oldUser.setPhonenumber(newUser.getPhonenumber());
        }

        return Response.ok(oldUser).build();
    }
}

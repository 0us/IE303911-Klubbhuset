package no.ntnu.klubbhuset.service;

import lombok.extern.java.Log;
import no.ntnu.klubbhuset.DatasourceProducer;
import no.ntnu.klubbhuset.SaveImages;
import no.ntnu.klubbhuset.domain.Image;
import no.ntnu.klubbhuset.domain.Member;
import no.ntnu.klubbhuset.domain.SecurityGroup;
import no.ntnu.klubbhuset.domain.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.logging.Level;

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

        if (email == null) {
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
            return Response.status(Response.Status.FORBIDDEN).entity("User can not be null").build();
        }

        if (doesUserExist(user)) {
            return Response.status(Response.Status.FORBIDDEN).entity("User with email " + user.getEmail() + " already exist!").build();
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

    public Response deleteUser() {
        User user = entityManager.find(User.class, principal.getName());

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }

        TypedQuery<Member> query = entityManager.createQuery("delete from Member m where m.user = :user", Member.class);
        query.setParameter("user", user);
        query.executeUpdate();

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
        if (oldUser == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No user found").build();
        }

        if (newUser.getFirstName() != null) {
            oldUser.setFirstName(newUser.getFirstName());
        }

        if (newUser.getLastName() != null) {
            oldUser.setLastName(newUser.getLastName());
        }

        if (newUser.getPassword() != null) {
            String hashedPassword = hashPassword(newUser.getPassword());
            oldUser.setPassword(hashedPassword);
        }

        if (newUser.getPhonenumber() != null) {
            oldUser.setPhonenumber(newUser.getPhonenumber());
        }

        return Response.ok(oldUser).build();
    }

    private boolean doesUserExist(User user) {
        User exist = entityManager.find(User.class, user.getEmail());

        return exist != null;
    }
}

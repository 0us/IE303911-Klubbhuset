package no.ntnu.klubbhuset;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.extern.java.Log;
import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.domain.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

@Path("auth")
@Stateless
@Log
public class AuthenticationService {

    private static final String INSERT_USERGROUP = "INSERT INTO AUSERGROUP(NAME,uid) VALUES (?,?)";
    private static final String DELETE_USERGROUP = "DELETE FROM AUSERGROUP WHERE NAME = ? AND uid = ?";

    @Inject
    KeyService keyService;

    @Inject
    IdentityStoreHandler identityStoreHandler;

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
    String issuer;

    @PersistenceContext
    EntityManager em;

    @Inject
    PasswordHash hasher;

    @Inject
    JsonWebToken principal;

    @Resource(lookup = DatasourceProducer.JNDI_NAME)
    DataSource dataSource;

    /**
     * @param email
     * @param pwd
     * @param request
     * @return
     */
    @GET
    @Path("login")
    public Response login(
            @QueryParam("email") @NotBlank String email,
            @QueryParam("pwd") @NotBlank String pwd,
            @Context HttpServletRequest request) {
        CredentialValidationResult result = identityStoreHandler.validate(
                new UsernamePasswordCredential(email, pwd));

        if (result.getStatus() == CredentialValidationResult.Status.VALID) {
            long uid = (long) em.createNativeQuery(
                    "select uid from auser where EMAIL = #email").
                    setParameter("email", result.getCallerPrincipal().getName()).
                    getResultList().get(0);
            List resultList = em.createNativeQuery(
                    "select name from ausergroup where uid = #uid").
                    setParameter("uid", uid).getResultList();
            Set<String> group = new HashSet<>(resultList);
            String token = issueToken(result.getCallerPrincipal().getName(),
                    group, request);
            return Response
                    .ok(token)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     * @param name
     * @param groups
     * @param request
     * @return
     */
    private String issueToken(String name, Set<String> groups, HttpServletRequest request) {
        try {
            Date now = new Date();
            Date expiration = Date.from(LocalDateTime.now().plusDays(1L).atZone(ZoneId.systemDefault()).toInstant());
            JwtBuilder jb = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setHeaderParam("kid", "abc-1234567890")
                    .setSubject(name)
                    .setId("a-123")
                    //.setIssuer(issuer)
                    .claim("iss", issuer)
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .claim("upn", name)
                    .claim("groups", groups)
                    .claim("aud", "aud")
                    .claim("auth_time", now)
                    .signWith(keyService.getPrivate());
            return jb.compact();
        } catch (Exception t) {
            log.log(Level.SEVERE, "Failed to create token", t);
            throw new RuntimeException("Failed to create token", t);
        }
    }

    /**
     * Does an insert into the AUSER and AUSERGROUP tables. It creates a SHA-256
     * hash of the password and Base64 encodes it before the user is created in
     * the database. The authentication system will read the AUSER table when
     * doing an authentication.
     *
     * @return
     */
    @POST
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createUser(
            @FormDataParam("pwd") String pwd,
            @FormDataParam("firstname") String firstname,
            @FormDataParam("lastname") String lastname,
            @FormDataParam("email") String email
    ) {
        // Checks if the user exists in the database
        Query query = em.createNativeQuery("select uid from auser where email = #email");
        query.setParameter("email", email);
        User user;
        if (!query.getResultList().isEmpty()) {
            log.log(Level.INFO, "User already exists {0}", email);

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("User already exists")
                    .build();
        } else {
            user = new User();
            user.setPassword(hasher.generate(pwd.toCharArray()));
            user.setFirstName(firstname);
            user.setLastName(lastname);
            user.setEmail(email);
            long gid = (long) em.createNativeQuery("select gid from agroup where agroup.NAME ='" + Group.USER + "'").getResultList().get(0);
            Group userGroup = em.find(Group.class, gid);
            user.getGroups().add(userGroup);
            Response.status(Response.Status.OK).build();
            return Response.ok(em.merge(user)).build();
        }
    }

    /**
     * @return
     */
    @GET
    @Path("currentuser")
    @RolesAllowed(value = {Group.USER})
    @Produces(MediaType.APPLICATION_JSON)
    public User getCurrentUser() {
        return em.find(User.class, principal.getName());
    }


    /**
     * @param uid
     * @param role
     * @return
     */
    @PUT
    @Path("addrole")
    @RolesAllowed(value = {Group.ADMIN})
    public Response addRole(@QueryParam("uid") String uid, @QueryParam("role") String role) {
        if (!roleExists(role)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        try (Connection c = dataSource.getConnection();
             PreparedStatement psg = c.prepareStatement(INSERT_USERGROUP)) {
            psg.setString(1, role);
            psg.setString(2, uid);
            psg.executeUpdate();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok().build();
    }

    /**
     * @param role
     * @return
     */
    private boolean roleExists(String role) {
        boolean result = false;

        if (role != null) {
            switch (role) {
                case Group.ADMIN:
                case Group.USER:
                    result = true;
                    break;
            }
        }

        return result;
    }

    /**
     * @param uid
     * @param role
     * @return
     */
    @PUT
    @Path("removerole")
    @RolesAllowed(value = {Group.ADMIN})
    public Response removeRole(@QueryParam("uid") String uid, @QueryParam("role") String role) {
        if (!roleExists(role)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        try (Connection c = dataSource.getConnection();
             PreparedStatement psg = c.prepareStatement(DELETE_USERGROUP)) {
            psg.setString(1, role);
            psg.setString(2, uid);
            psg.executeUpdate();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok().build();
    }

    /**
     * @param uid
     * @param password
     * @param sc
     * @return
     */
    @PUT
    @Path("changepassword")
    @RolesAllowed(value = {Group.USER})
    public Response changePassword(@QueryParam("uid") String uid,
                                   @QueryParam("pwd") String password,
                                   @Context SecurityContext sc) {
        String authuser = sc.getUserPrincipal() != null ? sc.getUserPrincipal().getName() : null;
        if (authuser == null || uid == null || (password == null || password.length() < 3)) {
            log.log(Level.SEVERE, "Failed to change password on user {0}", uid);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (authuser.compareToIgnoreCase(uid) != 0 && !sc.isUserInRole(Group.ADMIN)) {
            log.log(Level.SEVERE,
                    "No admin access for {0}. Failed to change password on user {1}",
                    new Object[]{authuser, uid});
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            User user = em.find(User.class, uid);
            user.setPassword(hasher.generate(password.toCharArray()));
            em.merge(user);
            return Response.ok().build();
        }
    }
}

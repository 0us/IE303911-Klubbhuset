package no.ntnu.klubbhuset.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.extern.java.Log;
import no.ntnu.klubbhuset.DatasourceProducer;
import no.ntnu.klubbhuset.KeyService;
import no.ntnu.klubbhuset.domain.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    private static final String INSERT_USERGROUP = "INSERT INTO AUSERGROUP(NAME,email) VALUES (?,?)";
    private static final String DELETE_USERGROUP = "DELETE FROM AUSERGROUP WHERE NAME = ? AND email = ?";

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
     * @param password
     * @param context
     * @return
     */
    @PermitAll
    public Response login(String email, String password, HttpServletRequest context) {
        CredentialValidationResult result = identityStoreHandler.validate(
                new UsernamePasswordCredential(email, password));

        if (result.getStatus() == CredentialValidationResult.Status.VALID) {
            List resultList = em.createNativeQuery(
                    "select name from usersecurityroles where email = #email").
                    setParameter("email", email).getResultList();
            Set<String> group = new HashSet<>(resultList);
            String token = issueToken(result.getCallerPrincipal().getName(),
                    group, context);
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
            Date expiration = Date.from(LocalDateTime.now().plusDays(1L).atZone(ZoneId.systemDefault()).toInstant()); // the token is valid for one day
            JwtBuilder jb = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setHeaderParam("kid", "THEONEANDONLY") // this is a hint to which signature is been used. we only have one
                    .setHeaderParam("alg", "RS256")
                    .setIssuer(issuer)
                    .claim("iss", issuer)
                    .claim(Claims.ID, "test123")
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .setSubject(name)
                    .setAudience(issuer)
                    .claim("upn", name)
                    .claim("groups", groups)
                    .claim("auth_time", now)
                    .signWith(keyService.getPrivate());
            return jb.compact();
        } catch (Exception t) {
            log.log(Level.SEVERE, "Failed to create token", t);
            throw new RuntimeException("Failed to create token", t);
        }
    }

    /**
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
        User user = em.find(User.class, email);

         if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("User already exists")
                    .build();
        } else {
            user = new User();
            user.setPassword(hasher.generate(pwd.toCharArray()));
            user.setFirstName(firstname);
            user.setLastName(lastname);
            user.setEmail(email);
            Response.status(Response.Status.OK).build();
            return Response.ok(em.merge(user)).build();
        }
    }

//    @PUT
//    @Path("addrole")
//    @RolesAllowed(value = {Group.ADMIN})
//    public Response addRole(@QueryParam("uid") String uid, @QueryParam("role") String role) {
//        if (!roleExists(role)) {
//            return Response.status(Response.Status.FORBIDDEN).build();
//        }
//
//        try (Connection c = dataSource.getConnection();
//             PreparedStatement psg = c.prepareStatement(INSERT_USERGROUP)) {
//            psg.setString(1, role);
//            psg.setString(2, uid);
//            psg.executeUpdate();
//        } catch (SQLException ex) {
//            log.log(Level.SEVERE, null, ex);
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//
//        return Response.ok().build();
//    }
//
//    /**
//     * @param role
//     * @return
//     */
//    private boolean roleExists(String role) {
//        boolean result = false;
//
//        if (role != null) {
//            switch (role) {
//                case Group.ADMIN:
//                case Group.USER:
//                    result = true;
//                    break;
//            }
//        }
//
//        return result;
//    }
//
//    /**
//     * @param uid
//     * @param role
//     * @return
//     */
//    @PUT
//    @Path("removerole")
//    @RolesAllowed(value = {Group.ADMIN})
//    public Response removeRole(@QueryParam("uid") String uid, @QueryParam("role") String role) {
//        if (!roleExists(role)) {
//            return Response.status(Response.Status.FORBIDDEN).build();
//        }
//
//        try (Connection c = dataSource.getConnection();
//             PreparedStatement psg = c.prepareStatement(DELETE_USERGROUP)) {
//            psg.setString(1, role);
//            psg.setString(2, uid);
//            psg.executeUpdate();
//        } catch (SQLException ex) {
//            log.log(Level.SEVERE, null, ex);
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//
//        return Response.ok().build();
//    }

//    @PUT
//    @Path("changepassword")
//    @RolesAllowed(value = {Group.USER})
//    public Response changePassword(@FormDataParam("uid") String uid,
//                                   @FormDataParam("pwd") String password,
//                                   @Context SecurityContext sc) {
//        String authuser = sc.getUserPrincipal() != null ? sc.getUserPrincipal().getName() : null;
//        if (authuser == null || email == null || (password == null || password.length() < 3)) {
//            log.log(Level.SEVERE, "Failed to change password on user {0}", email);
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//
//        if (authuser.compareToIgnoreCase(email) != 0 && !sc.isUserInRole(Group.ADMIN)) {
//            log.log(Level.SEVERE,
//                    "No admin access for {0}. Failed to change password on user {1}",
//                    new Object[]{authuser, email});
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        } else {
//            User user = em.find(User.class, email);
//            user.setPassword(hasher.generate(password.toCharArray()));
//            em.merge(user);
//            return Response.ok().build();
//        }
//    }

    public Response logout() {
        return null; //todo implement
    }
}

package no.ntnu.klubbhuset.service;

import no.ntnu.klubbhuset.DatasourceProducer;
import no.ntnu.klubbhuset.SaveImages;
import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.domain.Image;
import no.ntnu.klubbhuset.domain.Member;
import no.ntnu.klubbhuset.domain.Organization;
import no.ntnu.klubbhuset.domain.SecurityGroup;
import no.ntnu.klubbhuset.domain.User;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Stateless
public class OrganizationService {

    public static final String IMAGES = "images";
    public static final String IMAGE = "image";

    @Inject
    JsonWebToken principal;

    @Inject
    SaveImages saveImages;

    @PersistenceContext
    EntityManager entityManager;

    @Resource(lookup = DatasourceProducer.JNDI_NAME)
    DataSource dataSource;

    public Response getAllOrganizations() {
        System.out.println("Fetching all organizations");
        List<Organization> organizations = entityManager.createQuery("Select o From Organization o", Organization.class).getResultList();

        if ( organizations.isEmpty() ) {
            return Response.status(Response.Status.NOT_FOUND).entity("No organizations registered").build();
        }
        System.out.println("organizations = " + organizations);

//        Jsonb jsonb = JsonbBuilder.create();
//        String organizationsJson = jsonb.toJson(organizations);
//        System.out.println("organizationsJson = " + organizationsJson);

//        String json = null;
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
//            json = objectMapper.writeValueAsString(organizations);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return Response.ok(organizations).build();
    }


    // todo implement security
    //@RolesAllowed(value = {Group.USER})
    public Response createNewOrganization(String name, String price, String description, FormDataMultiPart multiPart) {
        Organization organization = new Organization();
        FormDataBodyPart imageBodyPart = multiPart.getField(IMAGE);

        organization.setName(name);
        organization.setDescription(description);
        organization.setPriceOfMembership(BigDecimal.valueOf(Long.parseLong(price))); // todo go through during code review. a bit cumbersome but should work. Maybe change?
        entityManager.persist(organization);

        if ( imageBodyPart != null ) {
            if (!saveImages.checkBodyPartIsImage(imageBodyPart)) {
                entityManager.remove(organization);  // the organization is already persisted to the database. Since the file uploaded is not an image, removing the organization is done
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                        .entity("File must be image, no image was uploaded")
                        .build();
            }

            InputStream inputStream = imageBodyPart.getValueAs(InputStream.class);
            ContentDisposition fileDetails = imageBodyPart.getContentDisposition();
            String filename = fileDetails.getFileName();
            System.out.println("filename = " + filename);
            String target = organization.getOid() + File.separator + IMAGES; // todo directory should be organization name or id?

            Image organizationImage = saveImages.saveImage(inputStream, target, filename);

            //entityManager.persist(organizationImage);
            coupleImageAndOrganization(organization, organizationImage);
        }

        return Response.status(Response.Status.CREATED).entity(organization).build();
    }

    @RolesAllowed(value = {Group.USER})
    public Response deleteOrganization(int organizationId) {
        Organization organization = entityManager.find(Organization.class, organizationId);


        if ( organization == null ) {
            return Response.status(Response.Status.NOT_FOUND).entity("No organization with id: " + organizationId).build();
        }

        entityManager.remove(organization);
        return Response.ok("Organization removed from system").build();
    }

    /**
     * A user can join a organization based on the organization ID. The user has to be logged in and is retrived trough
     * the JWT principal.
     * @param organizationId
     * @return
     */
    public Response joinOrganization(Long organizationId) {
        //Getting organization
        Organization organization = entityManager.find(Organization.class, organizationId);

        // Getting user
        User user = getUserFromPrincipal();
        // Getting default group (user)
        Group group = getDefaultGroup();

        Member member = new Member();
        member.setUser(user);
        member.setOrganization(organization);
        member.setGroup(group);
        member.setOrganization(organization);
        entityManager.persist(member);

        return Response.status(Response.Status.CREATED).entity("User was added to organization").build(); // todo return better feedback
    }

    /**
     * Getting an organization based on id
     * @param organizationId Id must be long since database value is BIGINT
     * @return Organization with id = organizationID
     */
    public Response getOrganizationById(Long organizationId) {
        Organization organization = entityManager.find(Organization.class, organizationId);

        if ( organization == null ) {
            return Response.status(Response.Status.NOT_FOUND).entity("Organization not found").build();
        }
        return Response.ok(organization).build();
    }

    public Response getMembers(String organizationId) {
        Long oid;
        try {
            oid = Long.parseLong(organizationId);
        } catch (NumberFormatException ne) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Please only use numbers for id.").build();
        }
        Organization org = entityManager.find(Organization.class, oid);
        Set<Member> members = org.getMembers();
        String json = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
            json = objectMapper.writeValueAsString(members);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.ok(json).build();
    }

    public Response createNewOrganization(Organization organization) {
        if ( organization == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Organization can not be null").build();
        }
        entityManager.persist(organization);

        return Response.status(Response.Status.CREATED).entity(organization).build();
    }

    // --- Private methods below --- //
    private void coupleImageAndOrganization(Organization organization, Image organizationImage) {
        long oid = organization.getOid();
        long iid = organizationImage.getIid();
        String query = "insert into orgimages (oid, iid) values (" + oid + ", " + iid + ")";
        System.out.println("query = " + query);
        entityManager.createNativeQuery(query).executeUpdate();
    }

    /**
     * Getting the default groupe which is `user`
     * @return
     */
    private Group getDefaultGroup() {
        return entityManager.createQuery("select g from Group g where g.name = :name", Group.class).setParameter("name", Group.USER).getSingleResult();
    }

    /**
     * Getting the user based on JWT principal
     * @return the user that is logged in
     */
    private User getUserFromPrincipal() {
        String userId = principal.getName();
        return entityManager.find(User.class, userId);
    }
}

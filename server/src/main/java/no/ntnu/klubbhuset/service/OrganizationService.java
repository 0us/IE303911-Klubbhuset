package no.ntnu.klubbhuset.service;

import no.ntnu.klubbhuset.DatasourceProducer;
import no.ntnu.klubbhuset.SaveImages;
import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.domain.Image;
import no.ntnu.klubbhuset.domain.Member;
import no.ntnu.klubbhuset.domain.Organization;
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
import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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

        Jsonb jsonb = JsonbBuilder.create();
        String organizationsJson = jsonb.toJson(organizations);
        System.out.println("organizationsJson = " + organizationsJson);

//        String json = null;
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
//            json = objectMapper.writeValueAsString(organizations);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return Response.ok(organizationsJson).build();
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

    @RolesAllowed(value = {Group.USER})
    public Response joinOrganization(int organizationId) {
        Organization organization = entityManager.find(Organization.class, organizationId);
        int userId = Integer.parseInt(principal.getName());
        User user = entityManager.find(User.class, userId);
        Member member = new Member(); // todo connect user to member
        member.setUser(user);
        organization.getMembers().add(member); // todo is this even gonna work?
        entityManager.persist(organization); // todo is this redundant

        return Response.status(Response.Status.CREATED).entity("User was added to organization").build(); // todo return better feedback
    }

    public Response getOrganizationById(int organizationId) {
        Organization organization = entityManager.find(Organization.class, organizationId);

        if ( organization == null ) {
            return Response.status(Response.Status.NOT_FOUND).entity("Organization not found").build();
        }

        Jsonb jsonb = JsonbBuilder.create();
        jsonb.toJson(organization);

        return Response.ok(jsonb).build();
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

    // --- Private methods below --- //
    private void coupleImageAndOrganization(Organization organization, Image organizationImage) {
        long oid = organization.getOid();
        long iid = organizationImage.getIid();
        String query = "insert into orgimages (oid, iid) values (" + oid + ", " + iid + ")";
        System.out.println("query = " + query);
        entityManager.createNativeQuery(query).executeUpdate();
    }

    public Response createNewOrganization(Organization organization) {
        if ( organization == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("Organization can not be null").build();
        }
        entityManager.persist(organization);

        return Response.status(Response.Status.CREATED).entity(organization).build();
    }
}

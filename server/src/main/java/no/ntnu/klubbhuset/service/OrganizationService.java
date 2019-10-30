package no.ntnu.klubbhuset.service;

import no.ntnu.klubbhuset.DatasourceProducer;
import no.ntnu.klubbhuset.SaveImages;
import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.domain.Member;
import no.ntnu.klubbhuset.domain.Organization;
import no.ntnu.klubbhuset.domain.User;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
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
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class OrganizationService {

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
        List<Organization> organizations = entityManager.createQuery("Select org From Organization org", Organization.class).getResultList();

        if (organizations.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No organizations registered").build();
        }

        Jsonb jsonb = JsonbBuilder.create();
        jsonb.toJson(organizations);
        return Response.ok(jsonb).build();
    }


    // todo implement security
    @RolesAllowed(value = {Group.USER}
    )
    public Response createNewOrganization(String name, String price, String description, FormDataMultiPart multiPart) {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setDescription(description);
        organization.setPriceOfMembership(BigDecimal.valueOf(Long.parseLong(price))); // todo go through during code review. a bit cumbersome but should work. Maybe change?

        InputStream inputStream = multiPart.getField("image").getValueAs(InputStream.class);
        FormDataContentDisposition fileDetails = multiPart.getField("image").getValueAs(FormDataContentDisposition.class);
        String filename = fileDetails.getFileName();
        String target = organization.getName() + File.separator + "images" + File.separator + filename; // todo directory should be organization name or id?

        saveImages.saveImage(inputStream, target, filename);

        entityManager.persist(organization);

        return Response.status(Response.Status.CREATED).entity("Organization was created").build();
    }

    @RolesAllowed(value = {Group.USER})
    public Response deleteOrganization(int organizationId) {
        Organization organization = entityManager.find(Organization.class, organizationId);


        if (organization == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No organization with id: " + organizationId).build();
        }

        entityManager.remove(organization);
        return Response.ok("Organization removed from system").build();
    }

    @RolesAllowed(value = {Group.USER})
    public Response joinOrganization(int organizationId, String userId) {
        Organization organization = entityManager.find(Organization.class, organizationId);
        User user = entityManager.find(User.class, userId);
        Member member = new Member(); // todo connect user to member
        organization.getMembers().add(member); // todo is this even gonna work?

        return Response.status(Response.Status.CREATED).entity("User was added to organization").build(); // todo return better feedback
    }

    public Response getOrganizationById(int organizationId) {
        Organization organization = entityManager.find(Organization.class, organizationId);

        if (organization == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Organization not found").build();
        }

        Jsonb jsonb = JsonbBuilder.create();
        jsonb.toJson(organization);

        return Response.ok(jsonb).build();
    }
}

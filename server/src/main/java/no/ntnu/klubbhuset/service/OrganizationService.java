package no.ntnu.klubbhuset.service;

import no.ntnu.klubbhuset.DatasourceProducer;
import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.domain.Member;
import no.ntnu.klubbhuset.domain.Organization;
import no.ntnu.klubbhuset.domain.User;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.microprofile.jwt.JsonWebToken;
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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Stateless
public class OrganizationService {

    @Inject
    JsonWebToken principal;

    @PersistenceContext
    EntityManager entityManager;

    public Response getAllOrganizations() {
        List<Organization> organizations = entityManager.createQuery("Select o From Organization o", Organization.class).getResultList();

        if (organizations.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No organizations registered").build();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        String json = null;
        try {
            json = objectMapper.writeValueAsString(organizations);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.ok(json).build();
    }


    // todo implement security
    @RolesAllowed(value = {Group.USER}
    )
    public Response createNewOrganization(String name, String price, String description, FormDataMultiPart multiPart) {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setDescription(description);
        organization.setPriceOfMembership(BigDecimal.valueOf(Long.parseLong(price))); // todo go through during code review. a bit cumbersome but should work. Maybe change?

        //todo save image
        //saveImage(multiPart);

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
}

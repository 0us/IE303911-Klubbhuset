package no.ntnu.klubbhuset.service;

import no.ntnu.klubbhuset.domain.Organization;
import no.ntnu.klubbhuset.domain.User;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrganizationService {

    @PersistenceContext
    EntityManager entityManager;

    public Response getAllOrganizations() {
        System.out.println("Fetching all organizations");
        List<Organization> organizations = entityManager.createQuery("Select org From Organization o", Organization.class).getResultList();

        if ( organizations.isEmpty() ) {
            return Response.noContent().entity("No organizations registered").build();
        }

        Jsonb jsonObject = JsonbBuilder.create();
        jsonObject.toJson(organizations);
        return Response.ok(jsonObject).build();
    }


    // todo implement security
    public Response createNewOrganization(String name, String price, String description, FormDataMultiPart multiPart) {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setDescription(description);
        organization.setPriceOfMembership(BigDecimal.valueOf(Long.parseLong(price))); // todo go through during code review. a bit cumbersome but should work. Maybe change?

        //todo save image
        saveImage(multiPart);

        entityManager.persist(organization);

        return Response.status(Response.Status.CREATED).entity("Organization was created").build();
    }
}

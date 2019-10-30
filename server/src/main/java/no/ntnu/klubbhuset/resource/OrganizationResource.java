package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.service.OrganizationService;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("organization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrganizationResource {
    public static final String ORGANIZATION = "organization";

    @Inject
    OrganizationService organizationService;

    @GET
    public Response getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }

    @GET
    @Path("/{organizationId}")
    public Response getOrganizationById(@PathParam("organizationId") int organizationId) {
        return organizationService.getOrganizationById(organizationId);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createNewOrganization(@FormDataParam("name") String name,
                                          @FormDataParam("priceOfMembership") String price,
                                          @FormDataParam("description") String description,
                                          FormDataMultiPart multiPart) {
        return organizationService.createNewOrganization(name, price, description, multiPart);
    }

//    @PUT
//    @Path("/{organizationId}")
//    public Response updateOrganization(@PathParam("organizationId") int id, Organization organization) {
//        return organizationService.updateOrganization(id, organization);
//    }

    @DELETE
    @Path("/{organizationId}")
    public Response deleteOrganization(@PathParam("organizationId") int id) {
        return organizationService.deleteOrganization(id);
    }

    @GET
    @Path("/{organizationId}/join")
    public Response joinOrganization(@PathParam("organizationId") int id, String userId) {
        return organizationService.joinOrganization(id, userId);
    }

    @GET
    @Path("/{organizationId}/members")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembers(@PathParam("organizationId") String organizationId) {
        return organizationService.getMembers(organizationId);
    }
}

package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.domain.Organization;
import no.ntnu.klubbhuset.service.OrganizationService;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("organization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrganizationResource {
    public static final String ORGANIZATION = "organization";

    OrganizationService organizationService = new OrganizationService();

    @GET
    public Response getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createNewOrganization(@FormDataParam("name") String name,
                                          @FormDataParam("priceOfMembership") String price,
                                          @FormDataParam("description") String description,
                                          FormDataMultiPart multiPart) {
        return organizationService.createNewOrganization(name, price, description, multiPart);
    }

    @PUT
    @Path("/{organizationId}")
    public Response updateOrganization(@PathParam("organizationId") int id, Organization organization) {
        return organizationService.updateOrganization(id, organization);
    }

    @DELETE
    @Path("/{organizationId}")
    public Response deleteOrganization(@PathParam("organizationId") int id) {
        return organizationService.deleteOrganization(id);
    }

    @GET
    @Path("/{organizationId}/join")
    public Response joinOrganization(@PathParam("organizationId") int id) {
        return organizationService.joinOrganization(id);
    }
}

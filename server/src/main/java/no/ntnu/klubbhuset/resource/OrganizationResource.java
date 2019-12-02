package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.domain.Organization;
import no.ntnu.klubbhuset.domain.SecurityGroup;
import no.ntnu.klubbhuset.service.AdminOrganizationService;
import no.ntnu.klubbhuset.service.OrganizationService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
import java.util.Map;

@Stateless
@Path("organization")
@RolesAllowed({SecurityGroup.USER, SecurityGroup.ADMIN})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrganizationResource {
    public static final String ORGANIZATION = "organization";

    @Inject
    OrganizationService organizationService;

    @Inject
    AdminOrganizationService adminOrganizationService;

    @Inject
    JsonWebToken principal;

    @GET
    public Response getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }

    @GET
    @Path("/{organizationId}")
    public Response getOrganizationById(@PathParam("organizationId") Long organizationId) {
        return organizationService.getOrganizationById(organizationId);
    }

//    @POST
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public Response createNewOrganization(@FormDataParam("name") String name,
//                                          @FormDataParam("priceOfMembership") String price,
//                                          @FormDataParam("description") String description,
//                                          FormDataMultiPart multiPart
////                                          @FormDataParam("image") InputStream uploadedInputStream,
////                                          @FormDataParam("image")FormDataContentDisposition fileDetails
//    ) {
//        return organizationService.createNewOrganization(name, price, description, multiPart);
//    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createNewOrganization(Map<String, String> map) {
        return organizationService.createNewOrganization(map);
    }


    @PUT
    @Path("/{organizationId}")
    public Response updateOrganization(@PathParam("organizationId") Long organizationId, Organization organization) {
        if(!adminOrganizationService.isAdminOfOrganization(organizationId)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this call").build();
        }
        return organizationService.updateOrganization(organizationId, organization);
    }

    @GET
    @Path("/{organizationId}/membership")
    public Response getMembership(@PathParam("organizationId") long id) {
        return organizationService.getMembership(id);
    }

    @DELETE
    @Path("/{organizationId}")
    public Response deleteOrganization(@PathParam("organizationId") int id) {
        return organizationService.deleteOrganization(id);
    }

    @POST
    @Path("/{organizationId}/join")
    public Response joinOrganization(@PathParam("organizationId") long id) {
        return organizationService.joinOrganization(id);
    }

    @GET
    @Path("/{organizationId}/members")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembers(@PathParam("organizationId") String organizationId) {
        return organizationService.getMembers(organizationId);
    }

    @GET
    @Path("/managed")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnedOrganizationsForUser() {
        return organizationService.getOwnedOrganizationsForUser();
    }
}

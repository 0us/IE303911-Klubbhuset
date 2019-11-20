package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.domain.SecurityGroup;
import no.ntnu.klubbhuset.domain.User;
import no.ntnu.klubbhuset.service.AdminOrganizationService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Endpoint for all task related to administrators of organizations
 */
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({SecurityGroup.USER})
@Path("organization/{organizationId}/admin")
public class AdminOrganizationResource {
    @PathParam("organizationId")
    private Long organizationId;

    @Inject
    AdminOrganizationService adminOrganizationService;

    @Inject
    JsonWebToken principal;

    @GET
    public Response getAllMembers() {
        if(!adminOrganizationService.isAdminOfOrganization(organizationId)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this call").build();
        }
        return  adminOrganizationService.getAllMembers(organizationId);
    }

    @POST
    public Response hasMemberPaid(User user) {
        return adminOrganizationService.harMemberPaid(organizationId, user);
    }
}

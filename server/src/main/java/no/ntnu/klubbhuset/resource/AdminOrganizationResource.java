package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.domain.Group;
import no.ntnu.klubbhuset.service.AdminOrganizationService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Endpoint for all task related to administrators of organizations
 */
@Stateless
//@RolesAllowed({Group.ADMIN})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("organizaton/{organizationId}/admin")
public class AdminOrganizationResource {
    @PathParam("organizationId")
    private Long organizationId;

    @Inject
    AdminOrganizationService adminOrganizationService;

    @GET
    public Response getAllMembers() {
        return  adminOrganizationService.getAllMembers(organizationId);
    }
}

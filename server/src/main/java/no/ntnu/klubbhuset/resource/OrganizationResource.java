package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.domain.Organization;
import no.ntnu.klubbhuset.domain.SecurityGroup;
import no.ntnu.klubbhuset.service.OrganizationService;
import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPart;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.json.Json;
import javax.print.attribute.standard.Media;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Stateless
@Path("organization")
@RolesAllowed({SecurityGroup.USER})
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

    @POST
    @Path("/{organizationId}/join")
    public Response joinOrganization(@PathParam("organizationId") Long id) {
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

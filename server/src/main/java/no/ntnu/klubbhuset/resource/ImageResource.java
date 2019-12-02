package no.ntnu.klubbhuset.resource;

import no.ntnu.klubbhuset.domain.SecurityGroup;
import no.ntnu.klubbhuset.service.ImageService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("image")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ImageResource {

    @Inject
    private ImageService imageService;


    @GET
    @Path("{oid}/{iid}")
    @Produces("image/jpeg")
    public Response getImage(@PathParam("oid") long oid, @PathParam("iid") long iid) {
        return imageService.getImages(oid, iid);
    }
}

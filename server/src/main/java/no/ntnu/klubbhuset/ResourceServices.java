//package no.ntnu.randa;
//
//import lombok.extern.java.Log;
//import net.coobird.thumbnailator.Thumbnails;
//import no.ntnu.randa.domain.Group;
//import no.ntnu.randa.entities.Image;
//import no.ntnu.randa.entities.Post;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.eclipse.microprofile.config.inject.ConfigProperty;
//import org.eclipse.microprofile.jwt.JsonWebToken;
//import org.glassfish.jersey.media.multipart.BodyPartEntity;
//import org.glassfish.jersey.media.multipart.FormDataBodyPart;
//import org.glassfish.jersey.media.multipart.FormDataParam;
//
//import javax.annotation.Resource;
//import javax.annotation.security.RolesAllowed;
//import javax.ejb.Stateless;
//import javax.inject.Inject;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.Query;
//import javax.security.enterprise.identitystore.IdentityStoreHandler;
//import javax.security.enterprise.identitystore.PasswordHash;
//import javax.sql.DataSource;
//import javax.ws.rs.*;
//import javax.ws.rs.core.CacheControl;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.StreamingOutput;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.file.FileSystems;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//
//import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
//import static no.ntnu.randa.entities.Post.GET_ALL_POSTS;
//
//
//@Path("resource")
//@Stateless
//@Log
//public class ResourceServices {
//
//    @Inject
//    KeyService keyService;
//
//    @Inject
//    IdentityStoreHandler identityStoreHandler;
//
//    @Inject
//    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
//    String issuer;
//
//    @PersistenceContext
//    EntityManager em;
//
//    @Inject
//    PasswordHash hasher;
//
//    @Inject
//    JsonWebToken principal;
//
//    @Resource(lookup = DatasourceProducer.JNDI_NAME)
//    DataSource dataSource;
//
//    private static final String IMAGE_SAVE_LOCATION = "C:/filestorage/images";
//
//
//    @POST
//    @Path("createpost")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @RolesAllowed(value = {Group.USER})
//    public Response createpost(
//            @FormDataParam("title") String title,
//            @FormDataParam("description") String description,
//            @FormDataParam("price") String formPrice,
//            @FormDataParam("image") List<FormDataBodyPart> images
//    ) {
//        // Parse price from form data
//        float price = 0;
//        try {
//            price = Float.parseFloat(formPrice);
//        } catch (NumberFormatException ne) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .entity("Price can only include numbers")
//                    .build();
//        }
//
//        // Create a new Post
//        Post post = new Post();
//
//
//        StringBuilder fileDetails = new StringBuilder("");
//        String user = principal.getName();
//
//        if (images == null) {
//            return Response
//                    .status(Response.Status.BAD_REQUEST)
//                    .entity("No images were included")
//                    .build();
//        }
//
//        // Save multiple files
//        for (FormDataBodyPart formDataBodyPart : images) {
//
//            // Create a new Image for the database and set this post as FK
//            Image image = new Image();
//            image.setPost(post);
//
//            // Casting FormDataBodyPart to BodyPartEntity, which can give us
//            BodyPartEntity bodyPartEntity = (BodyPartEntity) formDataBodyPart.getEntity();
//            // Getting the filename
//            String fileName = formDataBodyPart.getContentDisposition().getFileName();
//
//            // Get the absolute url to the image and sets the url for the image
//            String imagePath = "/" + user + "/" + fileName;
//            String fullpath = FileSystems.getDefault().getPath(
//                    IMAGE_SAVE_LOCATION + imagePath).toString();
//            image.setUrl(imagePath);
//
//            // Saving the file to the filesystem
//            saveFile(bodyPartEntity.getInputStream(), fileName, user);
//
//            // Adding filename to the post that's being created
//            post.addImage(image);
//        }
//
//        post.setTitle(title);
//        post.setDescription(description);
//        post.setPrice(price);
//        em.persist(post);
//
//        return Response.ok(fileDetails.toString())
//                // TODO: 25.09.2019 Add useful response
//                .entity("")
//                .build();
//    }
//
//    /**
//     * Method used to save files in the filesystem. This method will overwrite file without warning if
//     * a file already exists with the same name
//     *
//     * @param file the file to save
//     * @param name the name of the file
//     */
//    private void saveFile(InputStream file, String name, String user) {
//        try {
//            // Set the path destination for the file
//            java.nio.file.Path newFile = FileSystems.getDefault().getPath(
//                    IMAGE_SAVE_LOCATION + "/" + user + "/" + name);
//
//            // Create directory for specific user if it doesn't exist
//            boolean isCreated = false;
//
//            if (!isCreated) {
//                isCreated = (new File(IMAGE_SAVE_LOCATION + "/" + user).mkdir());
//            }
//
//            // Save InputStream as file
//            Files.copy(file, newFile, REPLACE_EXISTING);
//        } catch (IOException ie) {
//            ie.printStackTrace();
//        }
//    }
//
//    @GET
//    @Path("getposts")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getPosts() {
//        List<Post> postList = em.createNamedQuery(GET_ALL_POSTS, Post.class).getResultList();
//
//        if (postList.isEmpty()) {
//            return Response.status(Response.Status.NO_CONTENT).build();
//        }
//
////        Jsonb jsonb = JsonbBuilder.create();
////        String json = jsonb.toJson(postList);
//
//        String json = null;
//        try {
//            json = new ObjectMapper().writeValueAsString(postList);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return Response.ok()
//                .entity(json)
//                .build();
//    }
//
//    @GET
//    @Path("photo/{userFolder}/{name}")
//    @Produces("image/jpeg")
//    public Response getPhoto(@PathParam("name") String name,
//                             @PathParam("userFolder") String userFolder,
//                             @QueryParam("width") int width) {
//        java.nio.file.Path imageSaveLocation = Paths.get(IMAGE_SAVE_LOCATION);
//        java.nio.file.Path image = imageSaveLocation.resolve(userFolder + "\\" + name);
//        if(Files.exists(image)) {
//            StreamingOutput result = (OutputStream os) -> {
//
//                if(width == 0) {
//                    Files.copy(image, os);
//                    os.flush();
//                } else {
//                    Thumbnails.of(image.toFile())
//                            .size(width, width)
//                            .outputFormat("jpeg")
//                            .toOutputStream(os);
//                }
//            };
//
//            // Ask the browser to cache the image for 24 hours
//            CacheControl cc = new CacheControl();
//            cc.setMaxAge(86400);
//            cc.setPrivate(true);
//
//            return Response.ok(result).cacheControl(cc).build();
//        } else {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//    }
//
//    // TODO: 23.09.2019 This should not be here in production
//    @GET
//    @Path("killposts")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response killPosts() {
//        Query query = em.createNativeQuery("DELETE FROM Post");
//        query.executeUpdate();
//        return Response.ok()
//                .build();
//    }
//
//}

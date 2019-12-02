package no.ntnu.klubbhuset.service;

import net.coobird.thumbnailator.Thumbnails;

import javax.naming.OperationNotSupportedException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageService {

    private final String LOCAL_STORAGE_DIR = System.getProperty("user.home") + File.separator + "files";


    public Response getImages(long oid, long iid) {
        final String PATH = LOCAL_STORAGE_DIR + File.separator + oid + File.separator + iid + ".png";

        Path path = Paths.get(PATH);

        if (Files.exists(path)) {

            StreamingOutput result = (OutputStream os) -> {
                Thumbnails.of(path.toFile()).outputFormat("jpeg").size(128, 128).toOutputStream(os);
            };
            // Ask the browser to cache the image for 24 hours
            CacheControl cc = new CacheControl();
            cc.setMaxAge(86400);
            cc.setPrivate(true);
            return Response.ok(result).cacheControl(cc).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}

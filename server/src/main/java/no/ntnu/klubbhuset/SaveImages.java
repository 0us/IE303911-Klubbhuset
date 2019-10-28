package no.ntnu.klubbhuset;

import no.ntnu.klubbhuset.domain.Image;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Saves an image to the system. The image is first uploaded to the disk, then it is saved to the database.
 * If any of the processes fails, the image is deleted from the disk again.
 * Images should be saved under specific destinations like /organizations/images or /user/images
 */

//Todo check that all files is valide file types
public class SaveImages {

    @Inject
    EntityManager entityManager;

    @Inject
    JsonWebToken principal;

    private final String LOCAL_STORAGE_DIR = System.getProperty("user.home") + File.separator + "files";
    private Image image;


    public void saveImage(InputStream inputStream, String path) {
        final String END_PATH = LOCAL_STORAGE_DIR + File.separator + path;


        try {
            saveImageToDisk(inputStream, path);
            Image image = new Image();
            image.setUrl(path);
            entityManager.persist(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveImageToDisk(InputStream inputStream, String target) throws IOException {

        createFolderIfNotExists(target);

        OutputStream out = null;
        int read = 0;
        byte[] bytes = new byte[1024];

        out = new FileOutputStream(new File(target));
        while ((read = inputStream.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
        out.flush();
        out.close();
    }

    private void createFolderIfNotExists(String dirName) throws SecurityException {
        File theDir = new File(dirName);
        if (!theDir.exists()) {
            theDir.mkdir();
        }
    }
}

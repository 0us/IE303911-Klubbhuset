package no.ntnu.klubbhuset;

import no.ntnu.klubbhuset.domain.Image;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;


/**
 * Saves an image to the system. The image is first uploaded to the disk, then it is saved to the database.
 * If any of the processes fails, the image is deleted from the disk again.
 * Images should be saved under specific destinations like /organizations/images or /user/images
 */

//Todo check that all files is valide file types
public class SaveImages {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    JsonWebToken principal;


    private final String LOCAL_STORAGE_DIR = System.getProperty("user.home") + File.separator + "files";


    public Image saveImage(InputStream inputStream, String path, String filename) {
        final String END_PATH = LOCAL_STORAGE_DIR + File.separator + path;
        final String FULL_PATH = END_PATH + File.separator + filename;
        final String RELATIVE_URL = path + File.separator + filename;
        Image image = null;

        try {
            createFolderIfNotExists(END_PATH);
            saveImageToDisk(inputStream, FULL_PATH); // todo should this be END_PATH or path?
            image = new Image();
            image.setUrl(RELATIVE_URL); // todo should this be END_PATH or path?
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    private void saveImageToDisk(InputStream inputStream, String target) throws IOException {
        System.out.println("SaveImages.saveImageToDisk");
        System.out.println("target = " + target);
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
        System.out.println("SaveImages.createFolderIfNotExists");
        System.out.println("dirName = " + dirName);
        File theDir = new File(dirName);
        if (!theDir.exists()) {
            System.out.println("SaveImages.createFolderIfNotExists: File path does not exist. Trying to create");
            boolean created = theDir.mkdirs();
            System.out.println("created = " + created);
        }
    }
}

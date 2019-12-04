package no.ntnu.klubbhuset;

import no.ntnu.klubbhuset.domain.Image;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.glassfish.jersey.media.multipart.BodyPart;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Saves an image to the filesystem.
 * Creates paths for the destination if they don't exist
 */

//Todo check that all files is valide file types
public class SaveImages {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    JsonWebToken principal;


    private final String LOCAL_STORAGE_DIR = System.getProperty("user.home") + File.separator + "files";


    /**
     * Save an image to the filesystem
     *
     * @param inputStream the input stream
     * @param path        the path The path where you want the file saved to. Eg. {userid}/profilePicture/
     * @param filename    the filename The name of the file you want to save.
     * @return the image The image that was saved.
     */
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
            persistImage(image);
            System.out.println("image id = " + image.getIid());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    /**
     * Save an image to the filesystem. This method does almost the same as the one
     * above, but doesn't require a filename, it will instead create a unique filename for
     * given directory.
     *
     * @param inputStream the input stream
     * @param path        the path The path where you want the file saved to. Eg. {userid}/profilePicture/
     * @return the image The image that was saved.
     */
    public Image saveImage(InputStream inputStream, String path) {
        String filename = getUniqueFileName(path);
        return saveImage(inputStream, path, filename);
    }

    /**
     * Returns a unique filename in the directory specified
     * by the parameter
     * @param path directory to check
     * @return A unique filename
     */
    private String getUniqueFileName(String path) {
        String filename = "";
        final String END_PATH = LOCAL_STORAGE_DIR + File.separator + path;
        createFolderIfNotExists(END_PATH);
        File testTile = new File(END_PATH);
        File fileExists = null;
        for (int i = 0; i < 255; i++) {
            try {
                fileExists = File.createTempFile("file", String.valueOf(i), testTile);
                filename = String.valueOf(i);
            } catch (IOException ie) {
                continue;
            }
            break;
        }
        if (fileExists != null) {
            System.out.println(("Deletion of testfile: " + fileExists.delete()));
        }

        return filename;
    }

    private Image persistImage(Image image) {
        entityManager.persist(image);
        return image;
    }

    /**
     * Checks that file from inputstrem is acctually an image
     *
     * @param bodyPart
     * @return true if mime matches "image/*" false if not.
     */
    public boolean checkBodyPartIsImage(BodyPart bodyPart) {
        boolean result = false;
        String mimeType = bodyPart.getMediaType().toString();
        System.out.println("SaveImages.isImage");
        System.out.println("mimeType = " + mimeType);
        if ( mimeType.startsWith("image/") ) {
            result = true;
        }
        return result;
    }


    private void saveImageToDisk(InputStream inputStream, String target) throws IOException {
        // If the filename doesn't contain a file extension, then it will be set to .png
        if (!target.contains(".")) {
            target = target + ".png";
        }

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
        if ( !theDir.exists() ) {
            System.out.println("SaveImages.createFolderIfNotExists: File path does not exist. Trying to create");
            boolean created = theDir.mkdirs();
            System.out.println("created = " + created);
        }
    }
}

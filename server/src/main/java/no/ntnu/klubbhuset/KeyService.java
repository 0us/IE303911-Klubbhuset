package no.ntnu.klubbhuset;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@ApplicationScoped
@Path("publickey.pem")
public class KeyService {
    private static final String KEYPAIR_FILENAME = "jwtkeys.ser";
    
    KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);


    private boolean hasKeyFile() {
        return Files.exists(Paths.get(KEYPAIR_FILENAME));
    }
    
    @PostConstruct
    protected void postConstruct() {
        if(hasKeyFile()) {
            keyPair = readKeyPair();
        } else {
            keyPair = createKeyPair();
            writeKeyPair(keyPair);
        }
    }

    private KeyPair readKeyPair() {
        KeyPair result = null;
        
        try(
            FileInputStream fis = new FileInputStream(KEYPAIR_FILENAME);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bis)
        ) {
            result = (KeyPair) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(KeyService.class.getName()).log(Level.SEVERE, "Failed to read keyfile", ex);
        }
        
        return result != null ? result : createKeyPair();
    }
    
    private void writeKeyPair(KeyPair keyPair) {
        try(
            FileOutputStream fos = new FileOutputStream(KEYPAIR_FILENAME);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(keyPair);
        } catch (IOException ex) {
            Logger.getLogger(KeyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private KeyPair createKeyPair() {
        return Keys.keyPairFor(SignatureAlgorithm.RS256);
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response publicKey() {
        String key = Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPublic().getEncoded());
        StringBuilder result = new StringBuilder();
        result.append("-----BEGIN PUBLIC KEY-----\n");
        result.append(key);
        result.append("\n-----END PUBLIC KEY-----");

        return Response.ok(result.toString()).build();
    }
    
    public KeyPair getKeyPair() {
        return keyPair;
    }
    
    public PrivateKey getPrivate() {
        return getKeyPair().getPrivate();
    }
}
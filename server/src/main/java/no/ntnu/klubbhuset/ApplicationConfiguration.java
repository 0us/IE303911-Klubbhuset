package no.ntnu.klubbhuset;

import javax.annotation.security.DeclareRoles;
import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import no.ntnu.klubbhuset.domain.Group;
import org.eclipse.microprofile.auth.LoginConfig;

import java.util.HashMap;
import java.util.Map;


@ApplicationScoped
@ApplicationPath("api")
@DatabaseIdentityStoreDefinition(
    dataSourceLookup=DatasourceProducer.JNDI_NAME,
    callerQuery="select password from auser where email = ?",
    groupsQuery="select name from ausergroup where uid  = ?",
    hashAlgorithm = PasswordHash.class,
    priority = 80)
@DeclareRoles({Group.ADMIN,Group.USER})
@LoginConfig(authMethod = "MP-JWT",realmName = "klubbhuset")
public class ApplicationConfiguration extends Application {

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("jersey.config.server.provider.classnames",
                "org.glassfish.jersey.media.multipart.MultiPartFeature");
        return props;
    }

}

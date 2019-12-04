package no.ntnu.klubbhuset;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JAXRSConfiguration extends ResourceConfig {
    public JAXRSConfiguration() {
        packages("no.ntnu.klubbhuset")
            // Now you can expect validation errors to be sent to the client.
            .property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
            // @ValidateOnExecution annotations on subclasses won't cause errors.
            .property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true)
            .register(MultiPartFeature.class);
    }
}
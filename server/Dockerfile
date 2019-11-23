FROM payara/server-full
MAINTAINER Klubbhuset App
COPY ./mysql-connector-java-5.1.48.jar $PAYARA_DIR
RUN cp /opt/payara/appserver/mysql-connector-java-5.1.48.jar /opt/payara/appserver/glassfish/domains/production/lib/
RUN cp /opt/payara/appserver/mysql-connector-java-5.1.48.jar /opt/payara/appserver/glassfish/lib

COPY ./target/Klubbhuset.war $DEPLOY_DIR
ARG DEPLOY_PROPS='--contextroot /'
#CMD [$DEPLOY_PROPS, "/opt/payara/deployments", "--contextroot", "my"]
#COPY mycommands.asadmin .
#USER root
#RUN chown payara:payara mycommands.asadmin
#USER payara
#ENTRYPOINT cat ${DEPLOY_COMMANDS} >> mycommands.asadmin && ${PAYARA_PATH}/bin/asadmin start-domain -v --postbootcommandfile mycommands.asadmin ${PAYARA_DOMAIN}

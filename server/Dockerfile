FROM payara/server-full
MAINTAINER Klubbhuset App
COPY ./mysql-connector-java-5.1.48.jar $PAYARA_DIR
RUN cp /opt/payara/appserver/mysql-connector-java-5.1.48.jar /opt/payara/appserver/glassfish/domains/production/lib/
RUN cp /opt/payara/appserver/mysql-connector-java-5.1.48.jar /opt/payara/appserver/glassfish/lib

ENV DEPLOY_PROPS "--contextroot /"
COPY ./target/Klubbhuset.war $DEPLOY_DIR
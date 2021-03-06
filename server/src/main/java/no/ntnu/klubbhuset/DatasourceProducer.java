package no.ntnu.klubbhuset;

import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

import static no.ntnu.klubbhuset.DatasourceProducer.JNDI_NAME;


@Singleton
@DataSourceDefinition(
        name = JNDI_NAME,
        className = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource",
//        serverName = "klubbhusetdb",
//        databaseName = "klubbhuset",
//        portNumber = 3306,
//        url = "jdbc:mysql://klubbhusetdb:3306/klubbhuset?useSSL=false",
        url = "jdbc:mysql://klubbhuset-db:3306/klubbhuset?serverTimezone=Europe/Oslo&useSSL=false&allowPublicKeyRetrieval=true",
        user = "user",
        password = "password"
)
public class DatasourceProducer {
    public static final String JNDI_NAME = "java:app/jdbc/klubbhuset";

    @Resource(lookup = JNDI_NAME)
    DataSource ds;

    @Produces
    public DataSource getDatasource() {
        return ds;
    }
}

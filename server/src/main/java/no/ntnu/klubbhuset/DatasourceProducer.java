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
        className = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"
//        user = "markus",
//        password = "knugen",
//        url = "jdbc:mysql://localhost:3306/klubbhuset?useSSL=false"
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

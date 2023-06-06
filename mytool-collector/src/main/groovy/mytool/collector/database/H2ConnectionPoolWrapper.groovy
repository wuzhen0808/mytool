package mytool.collector.database


import groovy.transform.CompileStatic
import mytool.util.jdbc.ConnectionProvider
import org.h2.jdbcx.JdbcConnectionPool
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.SQLException

@CompileStatic
public class H2ConnectionPoolWrapper implements ConnectionProvider {
    private static final Logger LOG = LoggerFactory.getLogger(H2ConnectionPoolWrapper.class);

    private JdbcConnectionPool pool;

    public H2ConnectionPoolWrapper(JdbcConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public Connection openConnection() throws SQLException {

        Connection c = this.pool.getConnection();
        if (c.getAutoCommit()) {
            c.setAutoCommit(false);//
            if (LOG.isTraceEnabled()) {
                //LOG.trace("autocommit disabled for connection:" + c);
            }
        }
        //LOG.trace("connection opened,it may be closed later without notified.");
        return c;
    }

    public static ConnectionProvider newInstance(String dbUrl, String string, String string2) {
        JdbcConnectionPool pool = JdbcConnectionPool.create(dbUrl, "sa", "sa");
        LOG.info("connection pool created");
        return new H2ConnectionPoolWrapper(pool);
    }

    @Override
    public void dispose() {
        this.pool.dispose();
        LOG.info("connection pool closed");//
    }

}

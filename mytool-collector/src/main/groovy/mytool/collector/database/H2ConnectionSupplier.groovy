package mytool.collector.database


import groovy.transform.CompileStatic
import org.h2.jdbcx.JdbcConnectionPool
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.util.function.Supplier

@CompileStatic
class H2ConnectionSupplier implements Supplier<Connection> {
    private static final Logger LOG = LoggerFactory.getLogger(H2ConnectionSupplier.class);

    private JdbcConnectionPool pool;

    public H2ConnectionSupplier(JdbcConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    Connection get() {

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

    static Supplier<Connection> newInstance(String dbUrl, String string, String string2) {
        JdbcConnectionPool pool = JdbcConnectionPool.create(dbUrl, "sa", "sa");
        LOG.info("connection pool created");
        return new H2ConnectionSupplier(pool);
    }

}

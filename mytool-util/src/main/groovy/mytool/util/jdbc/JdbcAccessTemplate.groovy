package mytool.util.jdbc

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

@CompileStatic
class JdbcAccessTemplate {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcAccessTemplate.class);

    private static ParameterProvider EMPTY = new ParameterProvider() {

        @Override
        int size() {
            return 0;
        }

        @Override
        Object get(int idx) {
            throw new RuntimeException("no data");
        }
    };

    private static PreparedStatementExecutor<Long> UPDATE = new PreparedStatementExecutor<Long>() {

        @Override
        Long execute(PreparedStatement ps) throws SQLException {
            int rt = ps.executeUpdate();
            return Long.valueOf(rt);//
        }

    };

    static interface JdbcOperation<T> {

        T execute(Connection con, JdbcAccessTemplate t);

    }

    JdbcAccessTemplate() {
    }

    long executeUpdate(Connection con, String sql) {
        Long rt = (Long) execute(con, sql, EMPTY, UPDATE);
        return rt.longValue();
    }

    long executeUpdate(Connection con, String sql, List<Object> pp) {
        return this.executeUpdate(con, sql, pp.toArray(new Object[pp.size()]));
    }

    long executeUpdate(Connection con, String sql, Object[] pp) {
        return (Long) execute(con, sql, new ArrayParameterProvider(pp), UPDATE);
    }

    public <T> T execute(Connection con, String sql, ParameterProvider pp, PreparedStatementExecutor<T> pse) {
        Object[] args = pp.getAsArray();
        if (LOG.isTraceEnabled()) {
            LOG.trace("execute,sql:" + sql + ",parameters:" + Arrays.asList(args) + "");
        }
        try {

            PreparedStatement ps = con.prepareStatement(sql);
            int size = args.length;
            for (int i = 0; i < size; i++) {
                ps.setObject(i + 1, args[i]);
            }
            try {
                return pse.execute(ps);//
            } finally {
                ps.close();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    List<Object[]> executeQuery(Connection con, String sql) {
        return this.executeQuery(con, sql, new ObjectArrayListResultSetProcessor());
    }

    List<Object[]> executeQuery(Connection con, String sql, Object arg) {
        return this.executeQuery(con, sql, new Object[]{arg});
    }

    public <T> T executeQuery(Connection con, String sql, Object arg, ResultSetProcessor<T> rsp) {
        return this.executeQuery(con, sql, new Object[]{arg}, rsp);
    }

    List<Object[]> executeQuery(Connection con, String sql, Object[] pp) {
        return executeQuery(con, sql, pp, new ObjectArrayListResultSetProcessor());
    }

    public <T> T executeQuery(Connection con, String sql, ResultSetProcessor<T> rsp) {
        return executeQuery(con, sql, new Object[]{}, rsp);
    }

    public <T> T executeQuery(Connection con, String sql, List<Object> objects, ResultSetProcessor<T> rsp) {
        return this.executeQuery(con, sql, objects.toArray(), rsp);
    }

    public <T> T executeQuery(Connection con, String sql, Object[] objects, ResultSetProcessor<T> rsp) {
        return execute(con, sql, new ArrayParameterProvider(objects),
                new ResultSetProcessorPreparedStatementExecutor<T>(rsp));
    }

    Long counter(Connection con, String sql, Object[] args) {
        return this.executeQuery(con, sql, args, new CounterResultSetProcessor());
    }

}

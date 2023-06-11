package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.service.ConfigService
import mytool.backend.service.DataService
import mytool.collector.RtException
import mytool.collector.database.DBUpgrader
import mytool.collector.database.DBUpgrader_001
import mytool.collector.database.DBUpgrader_002
import mytool.collector.database.DBUpgrader_003
import mytool.collector.database.DataVersion
import mytool.collector.database.ReportDataAccessor
import mytool.collector.database.ReportTypeAccessor
import mytool.collector.database.Tables
import mytool.util.jdbc.ConnectionSupplier
import mytool.util.jdbc.JdbcAccessTemplate
import mytool.util.jdbc.ResultSetProcessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.util.function.Supplier

@CompileStatic
@Component
class DataServiceImpl implements DataService {

    private static Logger LOG = LoggerFactory.getLogger(DataServiceImpl)

    @Autowired
    ConfigService configService

    @Autowired
    ReportTypeAccessor reportTypeAccessor

    @Autowired
    ReportDataAccessor reportDataAccessor

    private static List<DBUpgrader> upgraderList = new ArrayList<DBUpgrader>();

    static {
        upgraderList.add(new DBUpgrader_001());
        upgraderList.add(new DBUpgrader_002());
        upgraderList.add(new DBUpgrader_003());
    }
    //the target data version to be upgraded to.
    private DataVersion targetDataVersion = DataVersion.V_0_0_3;

    private DataVersion dataVersion;

    @Autowired
    private JdbcAccessTemplate template

    @Autowired
    private ConnectionSupplier pool

    @PostConstruct
    void init() {
        doInit()
    }


    void doInit() {

        final String schema = "test";

        template.execute(pool, new JdbcAccessTemplate.JdbcOperation<Object>() {

            @Override
            Object execute(Connection con, JdbcAccessTemplate t) {
                final List<String> schemaList = new ArrayList<String>();
                t.executeQuery(con, "show schemas", new ResultSetProcessor<Object>() {

                    @Override
                    Object process(ResultSet rs) throws SQLException {
                        while (rs.next()) {
                            String name = rs.getString(1);
                            schemaList.add(name);
                            // System.out.println(rs.getString(1));
                        }
                        return null;
                    }
                });

                if (!schemaList.contains(schema.toUpperCase())) {
                    t.executeUpdate(con, "create schema " + schema);
                }
                if (!reportDataAccessor.isTableExists(con, t, Tables.TN_PROPERTY)) {
                    // create property table
                    {

                        String sql = "create table " + Tables.TN_PROPERTY + "(category varchar,key varchar,value varchar,";
                        sql += "primary key(category,key))";
                        t.executeUpdate(con, sql);

                    }
                    {
                        String sql = "insert into " + Tables.TN_PROPERTY + "(category,key,value)values(?,?,?)";
                        t.executeUpdate(con, sql,
                                new Object[]{"core", "data-version", DataVersion.V_UNKNOW.toString()});
                    }

                }

                upgrade(con, t);

                return null;
            }
        }, true);

    }

    void upgrade(Connection con, JdbcAccessTemplate t) {
        this.dataVersion = resolveDataVersion(con, t);

        LOG.info("dataVersion:" + dataVersion + ",targetVersion:" + this.targetDataVersion);
        while (true) {
            if (this.dataVersion == this.targetDataVersion) {
                // upgrade complete
                break;
            }

            DataVersion pre = this.dataVersion;
            DataVersion dv = this.tryUpgrade(con, t);
            if (dv == null) {
                LOG.warn("cannot upgrade from:" + pre + " to target:" + this.targetDataVersion);
                break;
            }
            LOG.info("successfuly upgrade from:" + pre + " to target:" + dv);
        }

    }

    private DataVersion tryUpgrade(Connection con, JdbcAccessTemplate t) {
        DataVersion rt = null;
        for (DBUpgrader up : this.upgraderList) {
            if (this.dataVersion == up.getSourceVersion()) {
                up.upgrade(con, t);//
                rt = up.getTargetVersion();
            }
        }
        if (rt != null) {
            this.dataVersion = rt;
        }
        return rt;
    }

    private DataVersion resolveDataVersion(Connection con, JdbcAccessTemplate t) {
        String sql = "select category,key,value from " + Tables.TN_PROPERTY + " t where t.category=? and t.key=?";
        List<Object[]> ll = t.executeQuery(con, sql, new Object[]{"core", "data-version"});
        if (ll.isEmpty()) {
            throw new RtException("bad data base.");
        } else {
            Object[] row = ll.get(0);
            return DataVersion.valueOf((String) row[2]);
        }
    }

    @Override
    ReportDataAccessor getReportDataAccessor() {
        return reportDataAccessor
    }

    @Override
    List<List> executeQueryBySqlId(String sqlId) {
        String sql = "select * from " + Tables.TN_ALIAS_INFO;
        return executeQuery(sql)
    }

    @Override
    List<List> executeQuery(String sql) {
        return template.executeQuery(pool, sql, false) as List<List>
    }
}

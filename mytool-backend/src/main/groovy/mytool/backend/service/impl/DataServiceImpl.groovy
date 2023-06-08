package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.service.ConfigService
import mytool.backend.service.DataService
import mytool.collector.database.H2ConnectionPoolWrapper
import mytool.collector.database.ReportDataAccessor
import mytool.collector.database.Tables
import mytool.collector.util.EnvUtil
import mytool.util.jdbc.ConnectionProvider
import mytool.util.jdbc.JdbcAccessTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.sql.Connection

@CompileStatic
@Component
class DataServiceImpl implements DataService {

    private static Logger LOG = LoggerFactory.getLogger(DataServiceImpl)

    @Autowired
    ConfigService configService

    ReportDataAccessor reportDataAccessor

    @PostConstruct
    void init() {
        File dbHome = configService.getDataFolder()
        String dbName = EnvUtil.getDbName()
        String dbUrl = "jdbc:h2:" + dbHome.getAbsolutePath().replace('\\', '/') + "/" + dbName;
        LOG.info("dbUrl:" + dbUrl);
        ConnectionProvider pool = H2ConnectionPoolWrapper.newInstance(dbUrl, "sa", "sa");
        reportDataAccessor = new ReportDataAccessor(pool);
        reportDataAccessor.initialize();
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
        return reportDataAccessor.execute(new JdbcAccessTemplate.JdbcOperation<Object>() {
            @Override
            Object execute(Connection con, JdbcAccessTemplate t) {
                return t.executeQuery(con, sql)
            }
        }, false) as List<List>
    }
}

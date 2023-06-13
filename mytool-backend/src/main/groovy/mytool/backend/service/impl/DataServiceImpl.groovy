package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.CorpInfo
import mytool.backend.service.ConfigService
import mytool.backend.service.DataService
import mytool.collector.database.ReportDataAccessor
import mytool.collector.database.ReportTypeAccessor
import mytool.collector.database.Tables
import mytool.util.jdbc.ResultSetUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Component

import java.sql.ResultSet

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

    @Autowired
    private JdbcTemplate template

    @Autowired
    private JdbcTemplate jdbcTemplate


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
    List<CorpInfo> getRecentCorps(String userId) {

    }

    @Override
    void touchRecent(String userId, List<String> corpIds) {

    }

    @Override
    List<List> executeQuery(String sql) {

        return jdbcTemplate.query(sql, { ResultSet rs ->
            ResultSetUtil.extractAsArrayList(rs)
        } as ResultSetExtractor<List<List>>)
    }

}

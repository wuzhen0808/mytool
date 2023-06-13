package mytool.backend.service

import groovy.transform.CompileStatic
import mytool.backend.CorpInfo
import mytool.collector.database.ReportDataAccessor
import mytool.collector.database.ReportTypeAccessor

@CompileStatic
interface DataService {
    List<List> executeQuery(String sql)

    ReportTypeAccessor getReportTypeAccessor()

    ReportDataAccessor getReportDataAccessor()

    List<List> executeQueryBySqlId(String sqlId)

    List<CorpInfo> getRecentCorps(String userId)

    void touchRecent(String userId, List<String> corpIds)
}
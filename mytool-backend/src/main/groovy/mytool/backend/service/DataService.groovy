package mytool.backend.service

import groovy.transform.CompileStatic
import mytool.collector.database.ReportDataAccessor
import mytool.collector.database.ReportTypeAccessor

@CompileStatic
interface DataService {
    List<List> executeQuery(String sql)

    ReportTypeAccessor getReportTypeAccessor()

    ReportDataAccessor getReportDataAccessor()

    List<List> executeQueryBySqlId(String sqlId)
}
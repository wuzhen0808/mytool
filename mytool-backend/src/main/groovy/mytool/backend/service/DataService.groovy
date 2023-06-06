package mytool.backend.service

import groovy.transform.CompileStatic
import mytool.collector.database.ReportDataAccessor

@CompileStatic
interface DataService {
    List<List> executeQuery(String sql)

    ReportDataAccessor getReportDataAccessor()

    List<List> executeQueryBySqlId(String sqlId)
}
package mytool.collector.database

import groovy.transform.CompileStatic
import mytool.collector.ReportType

import java.sql.ResultSet
import java.sql.SQLException

@CompileStatic
interface ReportResultProcessor<T> {
    public T process(ReportType reportType, List<String> aliasList, ResultSet rs) throws SQLException;
}

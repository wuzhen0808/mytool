package mytool.collector.database

import groovy.transform.CompileStatic

import java.sql.ResultSet
import java.sql.SQLException

@CompileStatic
public interface ReportResultProcessor<T> {
    public T process(int reportType, List<String> aliasList, ResultSet rs) throws SQLException;
}

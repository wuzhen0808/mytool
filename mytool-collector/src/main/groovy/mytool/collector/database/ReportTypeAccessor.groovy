package mytool.collector.database

import groovy.transform.CompileStatic
import mytool.collector.ReportType

@CompileStatic
interface ReportTypeAccessor {

    List<String> getMetricNames(ReportType reportType)

    Map<Integer, Map<String, Integer>> getAll()

    List<Integer> getColumnIndexByAliasList(final ReportType reportType, List<String> aliasList)

    List<Integer> getOrCreateColumnIndexByAliasList(final ReportType reportType, List<String> aliasList)

}

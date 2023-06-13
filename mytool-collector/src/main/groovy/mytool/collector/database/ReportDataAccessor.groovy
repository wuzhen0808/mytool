package mytool.collector.database

import groovy.transform.CompileStatic
import mytool.collector.ReportType

@CompileStatic
interface ReportDataAccessor {

    void mergeReport(final ReportType reportType, final String corpId, final Date reportDate, List<String> aliasList,
                     final List<BigDecimal> valueList)

    List<MetricRecord> queryReport(final ReportType reportType, final String corpId, final Date[] reportDateList)

    List<MetricRecord> queryReport(final ReportType reportType, final String corpId, final Date[] reportDateList, final List<String> aliasList)

    BigDecimal[] getReportValues(ReportType reportType, String corpId, Date[] dates, String alias)

    List<MetricRecord> getReport(ReportType reportType, String corpId, Date[] dates, String... metrics)

    List<MetricRecord> getReport(ReportType reportType, String corpId, Date[] dates, List<String> metrics)

}

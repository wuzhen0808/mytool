package mytool.backend.metrics

import groovy.transform.CompileStatic
import mytool.collector.MetricType
import mytool.collector.database.ReportDataAccessor
import mytool.collector.database.ReportRecord

@CompileStatic
class DefaultMetricsContext extends MetricsContext {

    private ReportDataAccessor reportDataAccessor
    private Map<MetricType, MetricProvider> metricProviderMap = [:]

    DefaultMetricsContext() {
        metricProviderMap.put(MetricTypes.ROE, new ROEMetricProvider())
    }

    @Override
    ReportRecord[] resolveMetrics(MetricType metricType, String corpId, Date[] dates) {
        MetricProvider provider = metricProviderMap.get(metricType)
        if (provider) {
            BigDecimal[] values = provider.calculate(this, corpId, dates)
            return ReportRecord.asList(MetricTypes.ROE, corpId, dates, values) as ReportRecord[]
        } else {
            return reportDataAccessor.getReport(metricType.reportType, corpId, dates, metricType.name) as ReportRecord[]
        }
    }


}

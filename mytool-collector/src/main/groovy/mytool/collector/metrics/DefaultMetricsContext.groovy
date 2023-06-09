package mytool.collector.metrics

import groovy.transform.CompileStatic
import mytool.collector.MetricProvider
import mytool.collector.MetricsContext
import mytool.collector.database.ReportDataAccessor
import mytool.collector.database.MetricRecord

@CompileStatic
class DefaultMetricsContext extends MetricsContext {

    private Map<String, MetricProvider> providerMap = [:]
    private MetricProvider defaultProvider

    DefaultMetricsContext(ReportDataAccessor reportDataAccessor) {
        this.defaultProvider = new DefaultMetricProvider(reportDataAccessor: reportDataAccessor)
    }

    @Override
    MetricRecord[] resolveMetrics(String metric, String corpId, Date[] dates) {
        MetricProvider provider = providerMap.get(metric)
        if (!provider) {
            provider = defaultProvider
        }

        BigDecimal[] values = provider.calculate(this, metric, corpId, dates)
        return MetricRecord.asList(metric, corpId, dates, values) as MetricRecord[]


    }


}

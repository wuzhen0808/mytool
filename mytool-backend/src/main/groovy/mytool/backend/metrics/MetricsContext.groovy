package mytool.backend.metrics

import groovy.transform.CompileStatic
import mytool.collector.MetricType
import mytool.collector.database.ReportRecord

@CompileStatic
abstract class MetricsContext {
    abstract ReportRecord[] resolveMetrics(MetricType metricType, String corpId, Date[] dates)
}

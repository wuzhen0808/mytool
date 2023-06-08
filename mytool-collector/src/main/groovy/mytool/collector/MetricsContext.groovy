package mytool.collector

import groovy.transform.CompileStatic
import mytool.collector.database.ReportRecord

@CompileStatic
abstract class MetricsContext {
    abstract ReportRecord[] resolveMetrics(MetricType metricType, String corpId, Date[] dates)
}

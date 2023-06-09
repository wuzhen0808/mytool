package mytool.collector

import groovy.transform.CompileStatic

@CompileStatic
abstract class MetricProvider {

    abstract BigDecimal[] calculate(MetricsContext metricsContext, String metric, String corpId, Date[] dates)
}

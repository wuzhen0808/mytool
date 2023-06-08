package mytool.backend.metrics

import groovy.transform.CompileStatic

@CompileStatic
abstract class MetricProvider {

    abstract BigDecimal[] calculate(MetricsContext metricsContext, String corpId, Date[] dates)
}

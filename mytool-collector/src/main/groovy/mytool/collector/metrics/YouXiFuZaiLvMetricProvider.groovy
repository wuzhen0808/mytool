package mytool.collector.metrics

import groovy.transform.CompileStatic
import mytool.collector.MetricProvider
import mytool.collector.MetricsContext

@CompileStatic
//有息负债率
class YouXiFuZaiLvMetricProvider extends MetricProvider {

    @Override
    BigDecimal[] calculate(MetricsContext mcc, String corpId, Date[] dates) {

    }
}

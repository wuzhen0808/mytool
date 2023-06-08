package mytool.collector.metrics

import groovy.transform.CompileStatic
import mytool.collector.MetricProvider
import mytool.collector.MetricTypes
import mytool.collector.MetricsContext

import java.math.RoundingMode

@CompileStatic
class ROEMetricProvider extends MetricProvider {

    @Override
    BigDecimal[] calculate(MetricsContext mcc, String corpId, Date[] dates) {
        BigDecimal[] equityArray = mcc.resolveMetrics(MetricTypes.BALANCE_TOTAL_HOLDERS_EQUITY, corpId, dates).collect { it.value } as BigDecimal[]
        BigDecimal[] netProfitArray = mcc.resolveMetrics(MetricTypes.INCOME_NET_PROFIT, corpId, dates).collect { it.value } as BigDecimal[]
        BigDecimal[] rt = new BigDecimal[dates.size()]

        for (int i = 0; i < rt.length; i++) {
            if (equityArray[i] && netProfitArray[i]) {
                rt[i] = netProfitArray[i].divide(equityArray[i], 4, RoundingMode.HALF_UP)
            }
        }
        return rt
    }
}

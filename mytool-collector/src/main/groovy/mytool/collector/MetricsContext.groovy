package mytool.collector

import groovy.transform.CompileStatic
import mytool.collector.database.MetricRecord

@CompileStatic
abstract class MetricsContext {

    abstract MetricRecord[] resolveMetrics(String metric, String corpId, Date[] dates)

    BigDecimal[] resolveMetricsValue(String metric, String corpId, Date[] dates) {
        resolveMetrics(metric, corpId, dates).collect({
            it.value
        }) as BigDecimal[]
    }

    Map<String, BigDecimal[]> resolveMetricsValue(String[] metrics, String corpId, Date[] dates) {
        Map<String, MetricRecord[]> map = resolveMetrics(metrics, corpId, dates)
        Map<String, BigDecimal[]> rtMap = [:]
        map.each {
            rtMap.put(it.key, it.value.collect {
                it.value
            } as BigDecimal[])
        }
        return rtMap

    }

    Map<String, MetricRecord[]> resolveMetrics(String[] metricType, String corpId, Date[] dates) {
        Map<String, MetricRecord[]> rtMap = [:]
        metricType.each {
            MetricRecord[] records = resolveMetrics(it, corpId, dates)
            rtMap.put(it, records)
        }
        return rtMap
    }
}

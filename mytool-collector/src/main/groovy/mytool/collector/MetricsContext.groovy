package mytool.collector

import groovy.transform.CompileStatic
import mytool.collector.database.ReportRecord

@CompileStatic
abstract class MetricsContext {
    abstract ReportRecord[] resolveMetrics(MetricType metricType, String corpId, Date[] dates)

    BigDecimal[] resolveMetricsValue(MetricType metricType, String corpId, Date[] dates) {
        resolveMetrics(metricType, corpId, dates).collect({
            it.value
        }) as BigDecimal[]
    }

    Map<MetricType, BigDecimal[]> resolveMetricsValue(MetricType[] metricType, String corpId, Date[] dates) {
        Map<MetricType, ReportRecord[]> map = resolveMetrics(metricType, corpId, dates)
        Map<MetricType, BigDecimal[]> rtMap = [:]
        map.each {
            rtMap.put(it.key, it.value.collect {
                it.value
            } as BigDecimal[])
        }
        return rtMap

    }

    Map<MetricType, ReportRecord[]> resolveMetrics(MetricType[] metricType, String corpId, Date[] dates) {
        Map<MetricType, ReportRecord[]> rtMap = [:]
        metricType.each {
            ReportRecord[] records = resolveMetrics(it, corpId, dates)
            rtMap.put(it, records)
        }
        return rtMap
    }
}

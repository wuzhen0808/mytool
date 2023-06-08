package mytool.backend.service

import groovy.transform.CompileStatic
import mytool.backend.ChartData
import mytool.collector.MetricType

@CompileStatic
interface ChartService {
    ChartData getChartData(String corpId, MetricType metricType)
}
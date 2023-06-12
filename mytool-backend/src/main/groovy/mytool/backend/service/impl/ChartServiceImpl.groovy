package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.ChartData
import mytool.backend.ChartModel
import mytool.backend.service.ChartModels
import mytool.backend.service.ChartService
import mytool.backend.service.DataService
import mytool.collector.MetricSettings
import mytool.collector.MetricType
import mytool.collector.MetricsContext
import mytool.collector.ReportType
import mytool.collector.RtException
import mytool.collector.database.MetricRecord
import mytool.collector.metrics.DefaultMetricsContext
import mytool.collector.util.EnvUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.math.RoundingMode
import java.text.SimpleDateFormat

@CompileStatic
@Component
class ChartServiceImpl implements ChartService {

    @Autowired
    DataService dataService

    @Autowired
    MetricSettings metricSettings

    @Autowired
    ChartModels chartModels
    MetricsContext metricsContext

    Date[] dates = EnvUtil.newDateOfYearsLastDay(2022..2013);

    @PostConstruct
    void init() {
        metricsContext = new DefaultMetricsContext(dataService.getReportDataAccessor())
    }

    @Override
    ChartData getChartDataById(String corpId, String chartId) {

        ChartModel chartModel = chartModels.getChartModelMap().get(chartId)
        if (!chartModel) {
            throw new RtException("no such chart:${chartId}")
        }
        return getChartData(corpId, chartModel)
    }

    @Override
    ChartData getChartData(String corpId, ChartModel chartModel) {
        ChartData chartData
        switch (chartModel.provider) {
            case "metric":
                chartData = getChartDataByMetric(corpId, chartModel)
                break
            case "report":
                chartData = getChartDataByReport(corpId, chartModel)
                break
            default:
                throw new RuntimeException("not supported:${chartModel.provider}")
        }
        return chartData
    }

    /**
     * 通过指定一个指标名称，构造一个柱状图
     * @param corpId
     * @param chartModel
     * @return
     */
    ChartData getChartDataByMetric(String corpId, ChartModel chartModel) {

        MetricRecord[] records = metricsContext.resolveMetrics(chartModel.metric, corpId, dates)
        Date[] dates = MetricRecord.collectDates(records)

        List<String> lablels = (dates.collect({ new SimpleDateFormat("yyyyMMdd").format(it) }))
        Map<String, BigDecimal[]> map = buildData(corpId, dates, records)
        map = translateMetricNameToAlias(map)
        return new ChartData.Builder()
                .type("bar")
                .labels(lablels)
                .data(map)
                .build()
    }

    /**
     * 通过查询一个报表中某些指标，构造一个包含多个指标序列的线图
     * @param corpId
     * @param chartModel
     * @return
     */
    ChartData getChartDataByReport(String corpId, ChartModel chartModel) {
        ReportType reportType = ReportType.get(chartModel.report)
        List<MetricRecord> records = dataService.getReportDataAccessor().queryReport(reportType, corpId, dates)
        Date[] dates = MetricRecord.collectDates(records)

        List<String> lablels = (dates.collect({ new SimpleDateFormat("yyyyMMdd").format(it) }))
        Map<String, BigDecimal[]> map = buildData(corpId, dates, records as MetricRecord[])

        if (chartModel.percentage) {
            def percentageBy = MetricType.valueOf(reportType, chartModel.percentageBy)
            Map<String, BigDecimal[]> mapPercentage = buildPercentage(map, percentageBy)
            //显示百分比，而不是原始数据
            map = mapPercentage
        }

        if (chartModel.removeLowLines != null) {
            //移除数值太低的某些序列，保留较大的那些序列
            Set<String> remainKeySet = getMetricsAnyHigherThan(map, chartModel.removeLowLines)
            map.removeAll { k, v ->
                !remainKeySet.contains(k)
            }
        }

        //根据标签过滤
        map.removeAll { k, v ->
            return !isTagMatch(k, chartModel.metrics?.tags, chartModel.metrics?.notTags)
        }

        map = translateMetricNameToAlias(map)
        return new ChartData.Builder()
                .type("line")
                .labels(lablels)
                .stacked(chartModel.stacked)
                .fill(chartModel.fill)
                .data(map)
                .build()
    }

    Map<String, BigDecimal[]> translateMetricNameToAlias(Map<String, BigDecimal[]> map) {
        return map.collectEntries {
            String alias = metricSettings.getFirstAlias(it.key)
            [alias ?: it.key, it.value]
        }
    }

    boolean isTagMatch(String metric, Set<String> chartTags, Set<String> chartNotTags) {
        MetricSettings.Attributes attributes = metricSettings.getAttributes(metric)
        Set<String> metricsTags = attributes?.tags

        if (chartTags != null) {
            if (metricsTags == null || !metricsTags.containsAll(chartTags)) {
                return false
            }
        }
        if (chartNotTags != null) {
            if (metricsTags != null) {
                for (String tag : metricsTags) {
                    if (chartNotTags.contains(tag)) {
                        return false
                    }
                }
            }
        }
        return true
    }

    Map<String, BigDecimal[]> buildData(String corpId, Date[] dates, MetricRecord[] records) {
        Map<String, Map<String, BigDecimal[]>> map = MetricRecord.groupValueByCorpIdAndKey(records, dates)
        return map.get(corpId) as Map<String, BigDecimal[]>
    }

    Map<String, BigDecimal[]> buildPercentage(Map<String, BigDecimal[]> map, MetricType totalMetric) {
        BigDecimal[] baseValue = map.get(totalMetric as String)
        if (baseValue == null) {
            throw new RtException("no base value found with total metric:${totalMetric}")
        }

        map.collectEntries {
            BigDecimal[] values = new BigDecimal[it.value.length]
            System.arraycopy(it.value, 0, values, 0, values.length)
            for (int i = 0; i < values.length; i++) {
                BigDecimal value = values[i]
                if (value == null) {
                    value = BigDecimal.ZERO
                }
                value = value.divide(baseValue[i], 2, RoundingMode.HALF_EVEN)
                values[i] = value
            }
            [
                    it.key,
                    values
            ]
        }
    }

    Set<String> getMetricsAnyHigherThan(Map<String, BigDecimal[]> map, BigDecimal anyHigherThan) {
        Set<String> set = []
        map.each {
            boolean isOk = false
            for (int i = 0; i < it.value.length; i++) {
                if (it.value[i].compareTo(anyHigherThan) > 0) {
                    isOk = true
                    break
                }
            }
            if (isOk) {
                set.add(it.key)
            }
        }

        return set
    }


}

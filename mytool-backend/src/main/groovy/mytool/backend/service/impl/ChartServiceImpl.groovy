package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.ChartData
import mytool.backend.ChartModel
import mytool.backend.service.ChartService
import mytool.backend.service.DataService
import mytool.collector.MetricType
import mytool.collector.MetricTypes
import mytool.collector.MetricsContext
import mytool.collector.ReportType
import mytool.collector.RtException
import mytool.collector.database.MetricRecord
import mytool.collector.metrics.DefaultMetricsContext
import mytool.collector.util.EnvUtil
import mytool.parser.formula.BigDecimalCalculator
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

    MetricsContext metricsContext

    Date[] dates = EnvUtil.newDateOfYearsLastDay(2022..2013);

    @PostConstruct
    void init() {
        metricsContext = new DefaultMetricsContext(dataService.getReportDataAccessor())
    }

    @Override
    ChartData getChartDataById(String corpId, String chartId) {

        ChartModel chartModel = ChartModel.getChartModelMap().get(chartId)
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
                return getChartDataByMetric(corpId, chartModel)
            case "report":
                return getChartDataByReport(corpId, chartModel)
            default:
                throw new RuntimeException("not supported:${chartModel.provider}")
        }
        return chartData
    }

    ChartData getChartDataByMetric(String corpId, ChartModel chartModel) {

        MetricRecord[] records = metricsContext.resolveMetrics(chartModel.metric, corpId, dates)
        Date[] dates = MetricRecord.collectDates(records)

        List<String> lablels = (dates.collect({ new SimpleDateFormat("yyyyMMdd").format(it) }))
        Map<String, BigDecimal[]> map = buildData(corpId, dates, records)

        return new ChartData.Builder()
                .type("bar")
                .labels(lablels)
                .data(map)
                .build()
    }

    ChartData getChartDataByReport(String corpId, ChartModel chartModel) {
        ReportType reportType = ReportType.get("1")
        List<MetricRecord> records = dataService.getReportDataAccessor().queryReport(reportType, corpId, dates)
        Date[] dates = MetricRecord.collectDates(records)

        List<String> lablels = (dates.collect({ new SimpleDateFormat("yyyyMMdd").format(it) }))
        Map<String, BigDecimal[]> map = buildData(corpId, dates, records as MetricRecord[])


        Map<String, BigDecimal[]> mapPercentage = buildPercentage(map)
        Set<String> remainKeySet = getMetricsAnyHigherThan(mapPercentage, 0.01 as BigDecimal)

        if (chartModel.percentage) {
            map = mapPercentage
        }

        map.removeAll { k, v ->
            !remainKeySet.contains(k)
        }

        //remove non-leaf
        map.removeAll { k, v ->
            MetricTypes.Options options = MetricTypes.getOptions(k)
            if (options && !options.isLeaf) {
                return true
            }
            return false
        }


        return new ChartData.Builder()
                .type("line")
                .labels(lablels)
                .stacked(true)
                .fill(true)
                .data(map)
                .build()
    }

    Map<String, BigDecimal[]> buildData(String corpId, Date[] dates, MetricRecord[] records) {
        Map<String, Map<String, BigDecimal[]>> map = MetricRecord.groupValueByCorpIdAndKey(records, dates)
        return map.get(corpId) as Map<String, BigDecimal[]>
    }

    Map<String, BigDecimal[]> buildPercentage(Map<String, BigDecimal[]> map) {
        BigDecimal[] baseValue = map.get(MetricType.valueOf(ReportType.ZCFZB, "total_assets") as String)
        if (baseValue == null) {
            throw new RtException("no baseValue found")
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

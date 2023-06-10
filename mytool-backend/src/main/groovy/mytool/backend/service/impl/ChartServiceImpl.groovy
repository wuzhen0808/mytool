package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.ChartData
import mytool.backend.ChartModel
import mytool.backend.service.ChartService
import mytool.backend.service.DataService
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
    ChartData getChartData(String corpId, String metric) {

        ChartModel chartModel = ChartModel.getChartModelMap().get(metric)
        if (!chartModel) {
            throw new RtException("no such chart:${metric}")
        }
        MetricRecord[] report = metricsContext.resolveMetrics(metric, corpId, dates)

        return new ChartData.Builder()
                .type(chartModel.type)
                .data(report)
                .build()
    }

    @Override
    ChartData getChartDataByReport(String corpId, String report) {
        ReportType reportType = ReportType.get(report)
        List<MetricRecord> records = dataService.getReportDataAccessor().queryReport(reportType, corpId, dates)

        return new ChartData.Builder()
                .type("line")
                .data(records)
                .build()
    }
}

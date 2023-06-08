package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.ChartData
import mytool.backend.metrics.DefaultMetricsContext
import mytool.backend.metrics.MetricTypes
import mytool.backend.metrics.MetricsContext
import mytool.backend.service.ChartService
import mytool.backend.service.DataService
import mytool.collector.ReportType
import mytool.collector.database.ReportDataAccessor
import mytool.collector.database.ReportRecord
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

    @PostConstruct
    void init() {
        metricsContext = new DefaultMetricsContext(reportDataAccessor: dataService.getReportDataAccessor())
    }

    @Override
    ChartData getChartData() {
        String corpId = "000001"
        Date[] dates = EnvUtil.newDateOfYearsLastDay(2022..2013);
        ReportRecord[] report = metricsContext.resolveMetrics(MetricTypes.ROE, corpId, dates)

        return new ChartData.Builder()
                .type("bar")
                .data(report)
                .build()
    }
}

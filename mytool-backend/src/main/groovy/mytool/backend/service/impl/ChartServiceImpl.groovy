package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.ChartData
import mytool.backend.service.ChartService
import mytool.backend.service.DataService
import mytool.collector.MetricsContext
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

    @PostConstruct
    void init() {
        metricsContext = new DefaultMetricsContext(dataService.getReportDataAccessor())
    }

    @Override
    ChartData getChartData(String corpId, String metric) {

        Date[] dates = EnvUtil.newDateOfYearsLastDay(2022..2013);
        MetricRecord[] report = metricsContext.resolveMetrics(metric, corpId, dates)

        return new ChartData.Builder()
                .type("bar")
                .data(report)
                .build()
    }
}

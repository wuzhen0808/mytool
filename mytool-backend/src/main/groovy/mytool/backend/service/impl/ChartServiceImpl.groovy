package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.ChartData
import mytool.backend.service.ChartService
import mytool.backend.service.DataService
import mytool.collector.ReportType
import mytool.collector.database.ReportDataAccessor
import mytool.collector.database.ReportRecord
import mytool.collector.util.EnvUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@CompileStatic
@Component
class ChartServiceImpl implements ChartService {

    @Autowired
    DataService dataService

    @Override
    ChartData getChartData() {
        ReportDataAccessor reportDataAccessor = dataService.getReportDataAccessor()
        String corpId = "000001"
        Date[] reportDate = EnvUtil.newDateOfYearsLastDay(2022..2013);
        List<ReportRecord> report = reportDataAccessor.getReport(ReportType.ZCFZB, corpId, reportDate, "total_assets")

        return new ChartData.Builder()
                .type("bar")
                .data(report)
                .build()
    }
}

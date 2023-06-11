package mytool.backend.service.impl

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import mytool.backend.ChartModel
import mytool.backend.service.ChartModels
import mytool.collector.RtException
import mytool.util.IoUtil
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.Mapping

import javax.annotation.PostConstruct

@CompileStatic
@Component
class ChartModelsImpl implements ChartModels {

    List<ChartModel> chartModels
    Map<String, ChartModel> chartModelMap = [:]

    @PostConstruct
    void init() {
        Map json = (new JsonSlurper().parse(IoUtil.getResourceAsReader(ChartModel, "conf/chart-models.json"))) as Map

        Set<String> enabledIds = json.enabled as Set<String>
        Map defaultChart = json.default as Map

        chartModels = (json.charts as List<Map>)
                .collect({Map it ->
                    Map it2 = [:]
                    it2.putAll(defaultChart)
                    it2.putAll(it)

                    ChartModel chart = new ChartModel()
                    chart.id = it2.id as String
                    chart.name = it2.name as String
                    chart.provider = it2.provider as String
                    chart.metric = it2.metric as String
                    chart.report = it2.report as String
                    Map metrics = it2.metrics as Map
                    if (metrics != null) {
                        chart.metrics = new ChartModel.Metrics()
                        chart.metrics.tags = metrics.tags as Set<String>
                        chart.metrics.notTags = metrics.notTags as Set<String>
                    }

                    chart.enabled = enabledIds.contains(chart.id)
                    if (it2.stacked as Boolean) {
                        chart.stacked = true
                    }

                    chart.fill = chart.stacked

                    chart.removeLowLines = it2.removeLowLines as BigDecimal

                    if (it2.percentage as Boolean) {
                        chart.percentage = true
                    }

                    chart.type = it2.type as String
                    chart.style = it2.style as String

                    return chart
                })
        chartModels.each {
            if (chartModelMap.put(it.id, it)) {
                throw new RtException("duplicated id:${it.id}")
            }

        }

    }

    List<ChartModel> getChartModels() {
        return this.chartModels
    }

    Map<String, ChartModel> getChartModelMap() {
        return this.chartModelMap
    }
}

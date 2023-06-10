package mytool.backend

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import mytool.util.IoUtil

@CompileStatic
class ChartModel {
    String id
    String type
    String provider
    String metric
    String report
    boolean enabled
    String style


    static List<ChartModel> chartModels
    static Map<String, ChartModel> chartModelMap

    static {
        Map json = (new JsonSlurper().parse(IoUtil.getResourceAsReader(ChartModel, "conf/corp-charts.json"))) as Map
        def enabled = json["enabled"] as Map
        Set<String> enabledMetrics = enabled["metrics"] as Set<String>
        Set<String> enabledReports = enabled["reports"] as Set<String>


        chartModels = (json["charts"] as List<Map>)
                .collect({
                    ChartModel chart = new ChartModel(id: UUID.randomUUID().toString())

                    chart.provider = it['provider'] as String
                    chart.metric = it["metric"] as String
                    chart.report = it["report"] as String
                    if (chart.provider == "metric") {
                        chart.enabled = enabledMetrics.contains(chart.metric)
                    } else if (chart.provider == "report") {
                        chart.enabled = enabledReports.contains(chart.report)
                    }

                    chart.type = it['type'] as String
                    chart.style = it['style'] as String

                    return chart
                })
        chartModelMap = chartModels.collectEntries {
            [it.metric, it]
        }
    }

}

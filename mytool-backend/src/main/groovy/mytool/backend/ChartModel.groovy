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
    boolean stacked
    boolean removeLowLines
    boolean percentage
    String style


    static List<ChartModel> chartModels
    static Map<String, ChartModel> chartModelMap

    static {
        Map json = (new JsonSlurper().parse(IoUtil.getResourceAsReader(ChartModel, "conf/chart-models.json"))) as Map

        Set<String> enabledIds = json["enabled"] as Set<String>

        chartModels = (json["charts"] as List<Map>)
                .collect({

                    ChartModel chart = new ChartModel()
                    chart.id = it["id"] as String
                    chart.provider = it['provider'] as String
                    chart.metric = it["metric"] as String
                    chart.report = it["report"] as String
                    chart.enabled = enabledIds.contains(chart.id)
                    if (it["stacked"] as Boolean) {
                        chart.stacked = true
                    }
                    if (it["removeLowLines"] as Boolean) {
                        chart.removeLowLines = true
                    }
                    if (it["percentage"] as Boolean) {
                        chart.percentage = true
                    }

                    chart.type = it['type'] as String
                    chart.style = it['style'] as String

                    return chart
                })
        chartModelMap = chartModels.collectEntries {
            [it.id, it]
        }
    }

}

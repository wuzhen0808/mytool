package mytool.backend

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import mytool.util.IoUtil

@CompileStatic
class ChartModel {

    String type
    String metric
    boolean enabled


    static List<ChartModel> chartModels
    static Map<String, ChartModel> chartModelMap

    static {
        Map json = (new JsonSlurper().parse(IoUtil.getResourceAsReader(ChartModel, "conf/corp-charts.json"))) as Map
        Set<String> show = json["enabled"] as Set<String>
        chartModels = (json["charts"] as List<Map>)
                .collect({
                    ChartModel chart = new ChartModel(metric: it["metric"] as String)
                    chart.enabled = show.contains(chart.metric)
                    chart.type = it['type'] as String
                    return chart
                })
        chartModelMap = chartModels.collectEntries {
            [it.metric, it]
        }
    }

}

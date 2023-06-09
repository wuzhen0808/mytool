package mytool.backend.controller

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import mytool.collector.MetricType
import mytool.collector.MetricTypes
import mytool.util.IoUtil
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@CompileStatic
@Controller
@RequestMapping("/ui/corp")
class CorpUiController {
    static class ChartModel {
        String metric
        boolean enabled

        static ChartModel valueOf(String metric) {
            return new ChartModel(metric: metric)
        }
    }

    @GetMapping("chart")
    String chart(Model model) {
        model.addAttribute("msg", "Hello")
        model.addAttribute("time", new Date())
        return null
    }

    @GetMapping("list")
    String corpList(Model model) {
        model.addAttribute("corpCode", "")
        return null
    }

    @GetMapping("detail")
    String corpDetail(@RequestParam(name = "corpId") String corpId, Model model) {
        model.addAttribute("title", "Corp Detail")
        model.addAttribute("corpId", corpId)
        List<ChartModel> charts = loadChart()
        model.addAttribute("charts", charts)
        return null
    }

    private List<ChartModel> loadChart() {
        return ((new JsonSlurper().parse(IoUtil.getResourceAsReader(CorpUiController, "conf/corp-charts.json"))
                as Map)["charts"] as List<Map>)
                .collect {
                    ChartModel chart = ChartModel.valueOf(it["metric"] as String)
                    chart.enabled = it["enabled"] as boolean
                    return chart
                }
    }


}

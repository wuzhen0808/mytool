package mytool.backend.controller

import groovy.transform.CompileStatic
import mytool.collector.MetricType
import mytool.collector.MetricTypes
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
        MetricType metricType

        static ChartModel valueOf(MetricType metricType) {
            return new ChartModel(metricType: metricType)
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
        model.addAttribute("title","Corp Detail")
        model.addAttribute("corpId", corpId)
        List<ChartModel> charts = []
        charts.add(ChartModel.valueOf(MetricTypes.ROE))
        charts.add(ChartModel.valueOf(MetricTypes.INCOME_NET_PROFIT))
        model.addAttribute("charts", charts)
        return null
    }


}

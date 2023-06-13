package mytool.backend.controller


import groovy.transform.CompileStatic
import mytool.backend.service.ChartModels
import mytool.backend.service.CorpListService
import mytool.backend.service.RecentCorpService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@CompileStatic
@Controller
@RequestMapping("/ui/corp")
class CorpUiController {

    @Autowired
    ChartModels chartModels

    @Autowired
    CorpListService corpListService

    @Autowired
    RecentCorpService recentCorpService

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
        String corpName = corpListService.getCorpName(corpId)
        if (corpName) {
            recentCorpService.addRecent(corpId, corpName)
        }
        model.addAttribute("title", "Corp Detail")
        model.addAttribute("corpId", corpId)
        model.addAttribute("corpName", corpName)
        model.addAttribute("charts", chartModels.getChartModels())
        return null
    }


}

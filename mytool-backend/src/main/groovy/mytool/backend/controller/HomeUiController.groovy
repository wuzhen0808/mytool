package mytool.backend.controller

import groovy.transform.CompileStatic
import mytool.backend.CorpInfo
import mytool.backend.service.RecentCorpService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@CompileStatic
@Controller
@RequestMapping("/ui")
class HomeUiController {

    @Autowired
    RecentCorpService recentCorpService

    @GetMapping("home")
    String home(Model model) {
        List<CorpInfo> recentCorps = recentCorpService.getRecentCorps()
        model.addAttribute("title", "My-Tool")
        model.addAttribute("time", new Date())
        model.addAttribute("recentCorps", recentCorps)

        return null
    }
}

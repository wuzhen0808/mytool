package mytool.backend.controller

import groovy.transform.CompileStatic
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@CompileStatic
@Controller
@RequestMapping("/ui/corp")
class CorpUiController {

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
    String corpDetail(Model model) {
        model.addAttribute("corpCode", "")
        return null
    }


}

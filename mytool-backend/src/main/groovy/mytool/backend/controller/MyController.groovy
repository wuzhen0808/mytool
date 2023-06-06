package mytool.backend.controller

import groovy.transform.CompileStatic
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@CompileStatic
@Controller
class MyController {

    @GetMapping("home")
    String home(Model model) {
        model.addAttribute("msg", "Hello")
        model.addAttribute("time", new Date())
        return "home"
    }
}

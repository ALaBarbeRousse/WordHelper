package helper.web;

import helper.api.service.StudentService;
import helper.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final StudentService studentService;
    @GetMapping
    public String getStartPage(Model model) {
        setPersonalAttributes(model);
        return "start";
    }

    @GetMapping("/main")
    public String getMainPage(Model model) {
        setPersonalAttributes(model);
        return "main";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "register";
    }

    @GetMapping("/auth/login")
    public String getLoginPage() {
        return "login";
    }

    private void setPersonalAttributes(Model model) {
        studentService.setPersonalAttributes(model);
    }
}

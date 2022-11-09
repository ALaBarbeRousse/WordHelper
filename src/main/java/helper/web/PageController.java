package helper.web;

import helper.model.Student;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping
    public String getStartPage(Model model) {
        model.addAttribute("student_name", getStudentName());
        return "start";
    }

    @GetMapping("/main")
    public String getMainPage(Model model) {
        model.addAttribute("student_name", getStudentName());
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

    private String getStudentName() {
        Object pr = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (pr instanceof Student) {
            return ((Student) pr).getName();
        } else {
            return null;
        }
    }
}

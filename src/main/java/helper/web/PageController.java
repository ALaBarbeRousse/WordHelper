package helper.web;

import helper.api.service.StudentService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/setroles")
    public String getRolesUpdatePage(Model model) {
        setPersonalAttributes(model);
        return "roles";
    }

    @GetMapping("/newdict")
    public String getNewDictionaryPage(Model model) {
        setPersonalAttributes(model);
        return "newdict";
    }

    @GetMapping("/word")
    public String getWordEditPage(Model model) {
        setPersonalAttributes(model);
        return "editword";
    }

    private void setPersonalAttributes(Model model) {
        studentService.setPersonalAttributes(model);
    }
}

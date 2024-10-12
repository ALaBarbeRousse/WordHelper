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
        return "word";
    }

    @GetMapping("/sounding")
    public String getSoundPage(Model model) {
        setPersonalAttributes(model);
        return "sounding";
    }

    @GetMapping("/training")
    public String getTrainWords(Model model) {
        setPersonalAttributes(model);
        return "training";
    }

    @GetMapping("/collection")
    public String getEditCollection(Model model) {
        setPersonalAttributes(model);
        return "collection";
    }

    @GetMapping("/export")
    public String getExportPage(Model model) {
        setPersonalAttributes(model);
        return "editor/export";
    }

    private void setPersonalAttributes(Model model) {
        studentService.setPersonalAttributes(model);
    }
}

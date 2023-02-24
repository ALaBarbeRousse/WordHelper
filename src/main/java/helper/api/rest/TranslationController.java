package helper.api.rest;

import helper.api.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/translation")
@RequiredArgsConstructor
public class TranslationController {
    private final TranslationService translationService;

    @GetMapping("/languages")
    public List<String[]> getExportLanguages() {
        return translationService.getAvailableLanguages().stream()
                .map(lp -> new String[] {lp.getL1().getName(), lp.getL2().getName()})
                .collect(Collectors.toList());
    }
}

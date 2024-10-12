package helper.api.rest;

import helper.api.service.LanguageService;
import helper.model.Language;
import helper.model.dto.LanguageCreateDTO;
import helper.model.dto.LanguageDTO;
import helper.model.dto.LanguageListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/language")
@RequiredArgsConstructor
public class LanguageController {
    private final LanguageService languageService;

    /**
     * @return List of all found languages
     */
    @GetMapping
    public LanguageListDTO getAllLanguages() {
        List<LanguageDTO> langs = languageService.getAllLanguages().stream()
                .map(Language::toDTO)
                .collect(Collectors.toList());
        List<Long> usedLanguages = languageService.findUsedLanguages();
        return new LanguageListDTO(langs, usedLanguages);
    }

    @PostMapping
    public LanguageDTO createLanguage(LanguageCreateDTO dto) {
        return languageService.createLanguage(dto).toDTO();
    }
}

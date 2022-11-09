package helper.api.service;

import helper.api.jpa.LanguageRepository;
import helper.model.Language;
import helper.model.dto.LanguageCreateDTO;
import helper.model.dto.LanguageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageService {
    private final LanguageRepository languageRepository;
    /**
     * TODO
     * @return List of all found languages
     */
    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    /**
     * TODO
     * @param dto = creation DTO for new language
     * @return DTO of created language
     */
    public Language createLanguage(LanguageCreateDTO dto) {
        Language language = new Language();
        language.setName(dto.getName());
        return languageRepository.save(language);
    }
}
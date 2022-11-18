package helper.api.service;

import helper.api.jpa.LanguageRepository;
import helper.model.Language;
import helper.model.dto.LanguageCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LanguageService {
    private final LanguageRepository languageRepository;

    /**
     * Getting all existing languages
     * @return List of all found languages
     */
    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    public Optional<Language> findLanguageByName(String name) {
        return languageRepository.findLanguageByName(name);
    }

    /**
     * Creating a new language
     * @param dto = creation DTO for new language
     * @return DTO of created language
     */
    public Language createLanguage(LanguageCreateDTO dto) {
        Language language = new Language();
        language.setName(dto.getName());
        return languageRepository.save(language);
    }
}
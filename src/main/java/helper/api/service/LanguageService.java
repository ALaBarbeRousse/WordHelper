package helper.api.service;

import helper.api.jpa.LanguageRepository;
import helper.model.Language;
import helper.model.LanguageChoice;
import helper.model.Student;
import helper.model.dto.LanguageCreateDTO;
import helper.model.dto.LanguageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LanguageService {
    private final LanguageRepository languageRepository;
    private final LanguageChoiceService languageChoiceService;

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

    public List<Long> findUsedLanguages() {
        Student student = (Student) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return languageChoiceService.findLanguageChoice(student)
                .map(LanguageChoice::getLanguages)
                .orElseGet(List::of)
                .stream()
                .map(LanguageDTO::getId)
                .collect(Collectors.toList());
    }

    public List<List<Language>> getAvailableLanguages() {
        return languageRepository.getAvailableLanguages();
    }

    /*  Получаем язык по его имени. Если такого нет - создаём */
    public Language findOrCreateLanguageByName(String name) {
        return languageRepository.findLanguageByName(name)
            .orElseGet(() -> createLanguage(new LanguageCreateDTO(name)));
    }
}
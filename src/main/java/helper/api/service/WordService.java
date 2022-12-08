package helper.api.service;

import helper.api.jpa.WordRepository;
import helper.model.Language;
import helper.model.LanguageChoice;
import helper.model.Student;
import helper.model.Translation;
import helper.model.Word;
import helper.model.dto.LanguageCreateDTO;
import helper.model.dto.WordArticleEditDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordService {
    private final LanguageService languageService;
    private final WordRepository wordRepository;
    private final TranslationService translationService;
    private final LanguageChoiceService languageChoiceService;

    /**
     * Создаём новую словарную статью (для слова) или редактируем существующую.
     *
     * @return saved Translation
     */
    @Transactional
    public Translation saveWordPair(WordArticleEditDTO dto) {
        /* Проверяем для начала, все ли языки есть. Если нет - создаём */
        Language lang1 = languageService.findLanguageByName(dto.getLang1())
                .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(dto.getLang1())));
        Language lang2 = languageService.findLanguageByName(dto.getLang2())
                .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(dto.getLang2())));

        Student student = (Student)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<LanguageChoice> languageChoice = languageChoiceService.findLanguageChoice(student);
        if (languageChoice.isEmpty()) {
            languageChoiceService.saveLanguageChoice(new LanguageChoice(student, lang1, lang2));
        } else if (!languageChoice.get().equalsLanguages(dto.getLang1(), dto.getLang2())) {
            languageChoice.get().setLang1(lang1);
            languageChoice.get().setLang2(lang2);
        }

        /* Сохраняем оба слова */
        Word word1 = this.saveWord(dto.getWord1().trim().toLowerCase(), lang1);
        Word word2 = this.saveWord(dto.getWord2().trim().toLowerCase(), lang2);

        /* Сохраняем сущность перевода (Translation) */
        Translation translation = translationService.findTranslation(lang1, word1, lang2, word2)
            .orElseGet(() -> translationService.findTranslation(lang2, word2, lang1, word1).orElseGet(() ->
                    new Translation(lang1, word1, lang2, word2)
                )
            );
        return translationService.saveTranslation(translation);
    }

    private Word saveWord(String writing, Language language) {
        Word word = wordRepository.findWordByWriting(writing)
                .orElseGet(() -> new Word(writing, language));
        return wordRepository.save(word);
    }
}

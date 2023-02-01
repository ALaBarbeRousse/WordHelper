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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<Translation> saveWordPair(WordArticleEditDTO dto) {
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
        Word word1 = this.getOrSaveWord(dto.getWord1().trim().toLowerCase(), lang1);
        Word word2 = this.getOrSaveWord(dto.getWord2().trim().toLowerCase(), lang2);

        /* Сохраняем сущности перевода (Translation) */
        /* todo Переводов может быть не один */
        Translation fwdTranslation = translationService.findTranslation(lang1, word1, lang2, word2)
                .orElseGet(() -> new Translation(lang1, word1, lang2, word2));
        Translation backTranslation = translationService.findTranslation(lang2, word2, lang1, word1)
                .orElseGet(() -> new Translation(lang2, word2, lang1, word1));
        return translationService.saveTranslations(List.of(fwdTranslation, backTranslation));
    }

    private Word getOrSaveWord(String writing, Language language) {
        return wordRepository.findWordByWriting(writing)
                .orElseGet(() -> wordRepository.save(new Word(writing, language)));
    }

    public Optional<Word> findWord(String word) {
        return wordRepository.findWordByWriting(word);
    }

    public Optional<String> findTranslation(Language langFrom, String word, Language langTo) {
        return wordRepository.findWordByWriting(word.toLowerCase())
            .flatMap(foundWord -> translationService.findTranslation(langFrom, foundWord, langTo))
            .map(Translation::getTranslation)
            .map(Word::getWriting);
    }

    /**
     * Пытаемся найти подходящие слова. Подходящие - это совпадающие или начинающиеся с word
     * @param word - search context
     * @return List of found words
     */
    public List<Word> findMatchingWords(String word) {
        return wordRepository.findByWritingStartingWith(word);
    }

    public List<String> findSimilarWords(String word, Language language) {
        return wordRepository.findByWritingStartingWithAndLanguage(word, language).stream()
                .map(Word::getWriting).collect(Collectors.toList());
    }
}

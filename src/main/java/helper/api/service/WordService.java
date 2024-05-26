package helper.api.service;

import helper.api.jpa.WordRepository;
import helper.model.*;
import helper.model.dto.LanguageCreateDTO;
import helper.model.dto.WordArticleEditDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class WordService {
    private final String CANNOT_FIND_LANGUAGE = "Не удалось найти язык \"%s\".";
    private final String CANNOT_FIND_WORD = "Не удалось найти слово \"%s\".";

    private final LanguageService languageService;
    private final TranslationService translationService;
    private final LanguageChoiceService languageChoiceService;
    private final TrainingResultService trainingResultService;
    private final VoiceHandler voiceHandler;

    private final WordRepository wordRepository;

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

    public Word getOrSaveWord(String writing, Language language) {
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

    /* Удаляем перевод */
    @Transactional
    public void deleteTranslation(WordArticleEditDTO dto) {
        /* Итак, нужно через translationRepository вытащить перевод */
        Word word1 = this.findWord(dto.getWord1())
            .orElseThrow(() -> new RuntimeException(String.format(CANNOT_FIND_WORD, dto.getWord1())));
        Word word2 = this.findWord(dto.getWord2())
            .orElseThrow(() -> new RuntimeException(String.format(CANNOT_FIND_WORD, dto.getWord2())));
        Language language1 = languageService.findLanguageByName(dto.getLang1())
            .orElseThrow(() -> new RuntimeException(String.format(CANNOT_FIND_LANGUAGE, dto.getLang1())));
        Language language2 = languageService.findLanguageByName(dto.getLang2())
            .orElseThrow(() -> new RuntimeException(String.format(CANNOT_FIND_LANGUAGE, dto.getLang2())));

        Translation found1 = translationService.findTranslationByWordsAndLanguages(word1, language1, word2, language2);
        Translation found2 = translationService.findTranslationByWordsAndLanguages(word2, language2, word1, language1);

        /* Перед удалением перевода надо удалить также все результаты тренировок с этим переводом */
        trainingResultService.deleteTrainingResults(Stream.concat(
                trainingResultService.getTrainingResultsByTranslation(found1).stream(),
                trainingResultService.getTrainingResultsByTranslation(found2).stream()
            )
            .distinct()
            .collect(Collectors.toList()));

        List<Word> wordsToDelete = Stream.concat(
                translationService.deleteTranslation(found1).stream(),
                translationService.deleteTranslation(found2).stream()
            )
            .distinct()
            .collect(Collectors.toList());

        /* Перед удалением слов надо удалить все озвучки этих слов */
        voiceHandler.deleteVoicesByWord(word1);
        voiceHandler.deleteVoicesByWord(word2);

        wordRepository.deleteAll(wordsToDelete);
    }
}

package helper.api.service;

import helper.model.Language;
import helper.model.Training;
import helper.model.Translation;
import helper.model.Word;
import helper.api.jpa.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TranslationService {
    private final TranslationRepository translationRepository;

    /*
     * Try to find translation
     */
    public Optional<Translation> findTranslation(Language lang1, Word word1, Language lang2, Word word2) {
        return translationRepository.findTranslationByLanguage1AndWord1AndLanguage2AndWord2(
                lang1,
                word1,
                lang2,
                word2
        );
    }

    public Translation saveTranslation(Translation translation) {
        return translationRepository.save(translation);
    }

    public Translation getRandomTranslation(Training training) {
        List<Translation> translations = translationRepository.getTranslationsByLanguage1AndLanguage2(
                training.getLanguage1(),
                training.getLanguage2()
        );
        /* todo Надо будет в какое-то подобие кэша складывать результат, чтобы не лазить каждый раз в базу. */
        Random random = new Random(System.currentTimeMillis());

        /* TODO Сделать также развесовку */

        return translations.get(random.nextInt(translations.size()));
    }
}

package helper.api.service;

import helper.model.Language;
import helper.model.Translation;
import helper.model.Word;
import helper.api.jpa.TranslationRepository;
import helper.model.dto.FindTranslationDTO;
import helper.model.dto.FindTranslationResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TranslationService {
    private final TranslationRepository translationRepository;

    /*
     * TODO Try to find translation
     */
    public Optional<Translation> findTranslation(Language wordLanguage, Word word, Language translationLanguage, Word translation) {
        return translationRepository.findTranslationByWordLanguageAndWordAndTranslationLanguageAndTranslation(
                wordLanguage,
                word,
                translationLanguage,
                translation
        );
    }

    public Translation saveTranslation(Translation translation) {
        return translationRepository.save(translation);
    }

    /* TODO Сделано крайне неэффективно */
//    public Translation getRandomTranslation(Training training) {
//        List<Translation> translations = translationRepository.getTranslationsByLanguage1AndLanguage2(
//                training.getLanguage1(),
//                training.getLanguage2()
//        );
//        /* todo Надо будет в какое-то подобие кэша складывать результат, чтобы не лазить каждый раз в базу. */
//        Random random = new Random(System.currentTimeMillis());
//
//        /* TODO Сделать также развесовку */
//
//        return translations.get(random.nextInt(translations.size()));
//    }

    public Optional<Translation> findTranslation(Language langFrom, Word word, Language langTo) {
        return translationRepository.findTranslationByWordLanguageAndTranslationLanguageAndAndWord(langFrom, langTo, word);
    }

    /* TODO Сохраняем список переводов*/
    public List<Translation> saveTranslations(List<Translation> translations) {
        return translationRepository.saveAll(translations);
    }


    /**
     * TODO Берём все переводы из подборки по критериям из training - подборка и пара языков
     * @param training
     * @return
//     */
//    public List<Translation> getRandomTranslations(Training training, int amount) {
//        Collection collection = training.getCollection();
//        Language l1 = training.getLanguage1();
//        Language l2 = training.getLanguage2();
//
//        /* todo это без учёта подборки */
//        List<Translation> found = translationRepository.getTranslationsByLanguage1AndLanguage2(l1, l2);
//
//        return List.of();
//    }
}

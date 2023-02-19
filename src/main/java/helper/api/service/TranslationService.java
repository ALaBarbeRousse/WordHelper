package helper.api.service;

import helper.model.Language;
import helper.model.Translation;
import helper.model.Word;
import helper.api.jpa.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TranslationService {
    private final TranslationRepository translationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Translation> findTranslation(UUID uuid) {
        return translationRepository.findTranslationByPhysicalId(uuid);
    }

    /*
     * Try to find translation
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

    public Optional<Translation> findTranslation(Language langFrom, Word word, Language langTo) {
        return translationRepository.findTranslationByWordLanguageAndTranslationLanguageAndWord(langFrom, langTo, word);
    }

    /* Сохраняем список переводов*/
    public List<Translation> saveTranslations(List<Translation> translations) {
        return translationRepository.saveAll(translations);
    }

    /* Хватаем случайный перевод */
    public Translation getRandomTranslation(Language l1, Language l2, List<Translation> exclude, int restrictedCount) {
        Random random = new Random(System.currentTimeMillis());

        Query query;
        if (exclude.isEmpty()) {
            query = entityManager.createQuery("SELECT t FROM Translation t WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2");
        } else {
            query = entityManager
                    .createQuery("SELECT t FROM Translation t WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2 AND t NOT IN ?3");
            query.setParameter(3, exclude);
        }
        query.setParameter(1, l1);
        query.setParameter(2, l2);

        int restrictedRandom = random.nextInt(restrictedCount);
        query.setFirstResult(restrictedRandom);
        query.setMaxResults(1);
        try {
            return (Translation) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public int getRestrictedCount(Language l1, Language l2, List<Translation> exclude) {
        Integer ret;
        if (exclude.isEmpty()) {
            ret = translationRepository.countTranslationByWordLanguageAndTranslationLanguage(l1, l2);
        } else {
            ret = translationRepository.countRestrictedTranslations(l1, l2, exclude);
        }
        return ret;
    }

    public List<Translation> getRestrictedTranslations(Language l1, Language l2, List<Translation> exclude) {
        return translationRepository.getRestrictedTranslations(l1, l2, exclude);
    }
}

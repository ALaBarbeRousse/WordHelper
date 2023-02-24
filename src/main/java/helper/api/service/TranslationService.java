package helper.api.service;

import helper.api.jpa.TranslationRepository;
import helper.model.Language;
import helper.model.Translation;
import helper.model.Word;
import helper.model.dto.LanguagePair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TranslationService {
    private final TranslationRepository translationRepository;

    private final LanguageService languageService;

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
    public Translation getRandomTranslation(Language l1, Language l2, List<Translation> exclude, List<Translation> include, int restrictedCount) {
        Random random = new Random(System.currentTimeMillis());

        Query query;
        if (exclude.isEmpty()) {
            if (include.isEmpty()) {
                query = entityManager
                        .createQuery("SELECT t FROM Translation t WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2");
            } else {
                query = entityManager
                        .createQuery("SELECT t FROM Translation t WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2 AND t IN ?3");
                query.setParameter(3, include);
            }
        } else {
            if (include.isEmpty()) {
                query = entityManager
                        .createQuery("SELECT t FROM Translation t WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2 AND t NOT IN ?3");
            } else {
                query = entityManager
                        .createQuery("SELECT t FROM Translation t WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2 AND t NOT IN ?3 AND t in ?4");
                query.setParameter(4, include);
            }
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

    public int getRestrictedCount(Language l1, Language l2, List<Translation> exclude, List<Translation> include) {
        /* Если список include не пустой, искать только в нём */
        if (exclude.isEmpty()) {
            if (include.isEmpty()) {
                return translationRepository.countTranslationByWordLanguageAndTranslationLanguage(l1, l2);
            } else {
                /* Случай, когда в заданной подборке есть переводы */
                return translationRepository.countTranslation(l1, l2, include);
            }
        } else {
            if (include.isEmpty()) {
                return translationRepository.countRestrictedTranslationsExclude(l1, l2, exclude);
            } else {
                return translationRepository.countRestrictedTranslationsIncludeExclude(l1, l2, include, exclude);
            }
        }
    }

    public List<Translation> getRestrictedTranslations(Language l1, Language l2, List<Translation> exclude, List<Translation> include) {
        if (include.isEmpty()) {
            if (exclude.isEmpty()) {
                return translationRepository.getRestrictedTranslations(l1, l2);
            } else {
                return translationRepository.getRestrictedTranslationsExclude(l1, l2, exclude);
            }
        } else {
            if (exclude.isEmpty()) {
                return translationRepository.getRestrictedTranslationsInclude(l1, l2, include);
            } else {
                return translationRepository.getRestrictedTranslationsIncludeExclude(l1, l2, include, exclude);
            }
        }
    }

    /**
     * Получаем все языки, использованные в переводах.
     * То есть на выходе должны быть не все языки, а только те, для которых есть переводы.
     * @return Список найденных языков
     */
    public List<LanguagePair> getAvailableLanguages() {
        return removeDuplicates(languageService.getAvailableLanguages().stream()
                .map(languages -> new LanguagePair(languages.get(0), languages.get(1)))
                .collect(Collectors.toList()));
    }

    /* Убираем дубликаты по специальному правилу: suomi->русский равно русский->suomi */
    private List<LanguagePair> removeDuplicates(List<LanguagePair> lpList) {
        List<LanguagePair> newList = new ArrayList<>();
        lpList.forEach(languagePair -> {
            if(!newList.contains(languagePair)) {
                newList.add(languagePair);
            }
        });

        return newList;
    }
}

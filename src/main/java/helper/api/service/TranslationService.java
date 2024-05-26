package helper.api.service;

import helper.api.jpa.TranslationRepository;
import helper.model.Language;
import helper.model.Translation;
import helper.model.Word;
import helper.model.dto.DictionaryDTO;
import helper.model.dto.LanguagePair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public List<DictionaryDTO> findTranslations(List<List<String>> dictionaries) {
        return dictionaries.stream()
            .map(languages -> List.of(
                Objects.requireNonNull(languageService.findLanguageByName(languages.get(0)).orElse(null)),
                Objects.requireNonNull(languageService.findLanguageByName(languages.get(1)).orElse(null))
            ))
                .map(languages -> {  // languages - это пара языков
                    DictionaryDTO dictionaryDTO = new DictionaryDTO();
                    dictionaryDTO.setLanguages(List.of(languages.get(0).getName(), languages.get(1).getName()));
                    List<Translation> found = findTranslations(languages.get(0), languages.get(1));
                    List<List<String>> foundTranslations = found.stream()
                        .map(translation -> List.of(
                            translation.getWord().getWriting(),
                            translation.getTranslation().getWriting())
                        ).collect(Collectors.toList());
                    dictionaryDTO.setTranslations(foundTranslations);
                    return dictionaryDTO;
                })
                .collect(Collectors.toList());
    }

    private List<Translation> findTranslations(Language l1, Language l2) {
        List<Translation> ret = translationRepository.findTranslationsByWordLanguageAndTranslationLanguage(l1, l2);
        ret.sort(Comparator.comparing(o -> o.getWord().getWriting()));
        return ret;
    }

    /**
     * todo Получаем случайный (один) перевод, у которого хотя бы одно слово без озвучки.
     *  Сделано неправильно - не включает переводы, у которых без озвучки только одно слово.
     * @return
     */
    public List<Translation> getRandomDeafTranslation(String lang1, String lang2, int amnt) {
        Optional<Language> l1 = languageService.findLanguageByName(lang1);
        Optional<Language> l2 = languageService.findLanguageByName(lang2);
        if (l1.isPresent() && l2.isPresent()) {
            return translationRepository.getRandomDeafTranslation(l1.get(), l2.get(), PageRequest.of(0, amnt));
        }
        return Collections.emptyList();
    }

    public Translation findTranslationByWordsAndLanguages(Word word1, Language language1, Word word2, Language language2) {
        return translationRepository.findTranslationByWordLanguageAndWordAndTranslationLanguageAndTranslation(language1, word1, language2, word2)
            .orElse(null);
    }

    /* Удаляем указанный перевод */
    public List<Word> deleteTranslation(Translation translation) {
        List<Word> toDelete = new ArrayList<>();

        List<Translation> trw = translationRepository.findTranslationsByWord(translation.getWord());
        trw.remove(translation);
        if (trw.isEmpty()) {
            toDelete.add(translation.getWord());
        }

        List<Translation> trt = translationRepository.findTranslationsByWord(translation.getTranslation());
        trt.remove(translation);
        if (trt.isEmpty()) {
            toDelete.add(translation.getTranslation());
        }

        translationRepository.delete(translation);

        return toDelete;
    }
}

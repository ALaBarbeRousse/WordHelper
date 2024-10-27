package helper.api.jpa;

import helper.model.Language;
import helper.model.Translation;
import helper.model.Word;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    Optional<Translation> findTranslationByWordLanguageAndTranslationLanguageAndWord(Language langFrom, Language langTo, Word word);

    Optional<Translation> findTranslationByWordLanguageAndWordAndTranslationLanguageAndTranslation(
        Language wordLanguage,
        Word word,
        Language translationLanguage,
        Word translation);

    Optional<Translation> findTranslationByPhysicalId(UUID physicalId);

    Integer countTranslationByWordLanguageAndTranslationLanguage(Language wordLanguage, Language translationLanguage);

    @Query("SELECT COUNT(t) FROM Translation t WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2 AND t NOT IN ?3")
    Integer countRestrictedTranslationsExclude(Language wordLanguage, Language translationLanguage, List<Translation> exclude);

    @Query("SELECT COUNT(t) FROM Translation t WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2 AND t IN ?3 AND t NOT IN ?4")
    Integer countRestrictedTranslationsIncludeExclude(Language l1, Language l2, List<Translation> include, List<Translation> exclude);

    @Query("SELECT COUNT(t) FROM Translation t WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2 AND t in ?3")
    Integer countTranslation(Language l1, Language l2, List<Translation> include);

    @Query("SELECT t FROM Translation t WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2 AND t NOT IN ?3")
    List<Translation> getRestrictedTranslationsExclude(Language l1, Language l2, List<Translation> exclude);

    @Query("SELECT t FROM Translation t WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2")
    List<Translation> getRestrictedTranslations(Language l1, Language l2);

    @Query("SELECT t FROM Translation t WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2 AND t IN ?3")
    List<Translation> getRestrictedTranslationsInclude(Language l1, Language l2, List<Translation> include);

    @Query("SELECT t FROM Translation t WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2 AND t IN ?3 AND t NOT IN ?4")
    List<Translation> getRestrictedTranslationsIncludeExclude(Language l1, Language l2, List<Translation> include, List<Translation> exclude);

    List<Translation> findTranslationsByWordLanguageAndTranslationLanguage(Language wl, Language tl);

    /* FIXME Сделано неправильно - не включает переводы, у которых без озвучки только одно слово. */
    @Query("SELECT t FROM Translation t LEFT JOIN Voice v ON (t.word = v.word OR t.translation = v.word)" +
        " WHERE t.wordLanguage = ?1 AND t.translationLanguage = ?2 AND v IS NULL ORDER BY RANDOM()")
    List<Translation> getRandomDeafTranslation(Language l1, Language l2, Pageable pageable);

    @Query("SELECT w FROM Word w LEFT JOIN Voice v ON w = v.word WHERE v IS NULL ORDER BY RANDOM()")
    List<Word> getRandomDeafWord(PageRequest of);

    /* Получаем все переводы, в которых присутствует данное слово */
    @Query("SELECT t FROM Translation t WHERE t.word = ?1 OR t.translation = ?1")
    List<Translation> findTranslationsByWord(Word w);
}

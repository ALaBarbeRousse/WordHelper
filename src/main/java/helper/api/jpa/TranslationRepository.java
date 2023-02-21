package helper.api.jpa;

import helper.model.Language;
import helper.model.Translation;
import helper.model.Word;
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
}

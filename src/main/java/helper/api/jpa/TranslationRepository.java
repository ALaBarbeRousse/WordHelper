package helper.api.jpa;

import helper.model.Language;
import helper.model.Translation;
import helper.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/* TODO */
public interface TranslationRepository extends JpaRepository<Translation, Long> {
    Optional<Translation> findTranslationByWordLanguageAndTranslationLanguageAndAndWord(Language langFrom, Language langTo, Word word);

    Optional<Translation> findTranslationByWordLanguageAndWordAndTranslationLanguageAndTranslation(
            Language wordLanguage,
            Word word,
            Language translationLanguage,
            Word translation);

//    @Query("SELECT t FROM Translation t WHERE ((t.language1 = ?1 AND t.language2 = ?3) OR (t.language1 = ?3 AND t.language2 = ?1)) AND ((t.word1 = ?2 AND t.word2 = ?4) OR (t.word1 = ?4 AND t.word2 = ?2))")
//    Optional<Translation> findTranslationByLanguage1AndWord1AndLanguage2AndWord2(
//            Language oneLanguage,
//            Word oneWord,
//            Language anotherLanguage,
//            Word anotherWord
//    );

//    @Query("SELECT t FROM Translation t WHERE (t.language1 = ?1 OR t.language2 = ?1) AND (t.language1 = ?2 OR t.language2 = ?2)")
//    List<Translation> getTranslationsByLanguage1AndLanguage2(Language language1, Language language2);

//    @Query("SELECT t FROM Translation t WHERE (t.language1 = ?1 OR t.language2 = ?1) AND (t.language1 = ?2 OR t.language2 = ?2) AND (t.word1 = ?3 OR t.word2 = ?3)")
//    Optional<Translation> findTranslation(Language lang1, Language lang2, Word word);
}

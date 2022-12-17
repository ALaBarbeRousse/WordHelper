package helper.api.jpa;

import helper.model.Language;
import helper.model.Translation;
import helper.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    Optional<Translation> findTranslationByLanguage1AndWord1AndLanguage2AndWord2(
            Language oneLanguage,
            Word oneWord,
            Language anotherLanguage,
            Word anotherWord
    );

    @Query("SELECT t FROM Translation t WHERE (t.language1 = ?1 OR t.language2 = ?1) AND (t.language1 = ?2 OR t.language2 = ?2)")
    List<Translation> getTranslationsByLanguage1AndLanguage2(Language language1, Language language2);
}

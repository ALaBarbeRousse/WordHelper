package helper.api.jpa;

import helper.model.Language;
import helper.model.Translation;
import helper.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    Optional<Translation> findTranslationByLanguage1AndWord1AndLanguage2AndWord2(
            Language oneLanguage,
            Word oneWord,
            Language anotherLanguage,
            Word anotherWord
    );
}

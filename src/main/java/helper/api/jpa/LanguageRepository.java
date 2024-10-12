package helper.api.jpa;

import helper.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    Optional<Language> findLanguageByName(String name);

    @Query("SELECT DISTINCT t.wordLanguage, t.translationLanguage FROM Translation t")
    List<List<Language>> getAvailableLanguages();
}
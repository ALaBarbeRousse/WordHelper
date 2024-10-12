package helper.api.jpa;

import java.util.Optional;

import helper.model.Language;
import helper.model.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeakersRepository extends JpaRepository<Speaker, Long> {
    Optional<Speaker> findByLanguageAndName(Language language, String name);
}

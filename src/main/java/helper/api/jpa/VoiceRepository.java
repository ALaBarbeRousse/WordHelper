package helper.api.jpa;

import java.util.Optional;

import helper.model.Language;
import helper.model.Speaker;
import helper.model.Voice;
import helper.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VoiceRepository extends JpaRepository<Voice, Long> {
    Optional<Voice> findByWordAndSpeaker(Word word, Speaker speaker);

    @Query(value = "SELECT COUNT(*) FROM Voice v WHERE v.word.writing = ?1 AND v.word.language = ?2")
    int getVoicesPresent(String word, Language language);
}

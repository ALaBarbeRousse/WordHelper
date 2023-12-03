package helper.api.jpa;

import java.util.Optional;

import helper.model.Speaker;
import helper.model.Voice;
import helper.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceRepository extends JpaRepository<Voice, Long> {
    Optional<Voice> findByWordAndSpeaker(Word word, Speaker speaker);
}

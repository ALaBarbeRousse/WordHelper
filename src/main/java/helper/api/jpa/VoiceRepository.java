package helper.api.jpa;

import java.util.Optional;
import java.util.List;

import helper.model.Language;
import helper.model.Speaker;
import helper.model.Voice;
import helper.model.Word;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VoiceRepository extends JpaRepository<Voice, Long> {
    Optional<Voice> findByWordAndSpeaker(Word word, Speaker speaker);

    @Query(value = "SELECT COUNT(*) FROM Voice v WHERE v.word.writing = ?1 AND v.word.language = ?2")
    int getVoicesPresent(String word, Language language);

    @Query("SELECT v FROM Voice v WHERE v.word = ?1 ORDER BY RANDOM()")
    List<Voice> getRandomVoice(Word word, Pageable pageable);

    @Query(value = "SELECT v FROM Voice v WHERE v.word.writing = ?1 AND v.word.language = ?2")
    List<Voice> getVoices(String word, Language language);

    void deleteVoicesByWord(Word word);
}

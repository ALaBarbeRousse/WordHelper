package helper.api.jpa;

import helper.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {
    Optional<Word> findWordByWriting(String writing);

    List<Word> findByWritingStartingWith(String string);
}

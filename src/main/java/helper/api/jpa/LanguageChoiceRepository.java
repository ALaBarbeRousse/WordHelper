package helper.api.jpa;

import helper.model.LanguageChoice;
import helper.model.Role;
import helper.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LanguageChoiceRepository extends JpaRepository<LanguageChoice, Long> {
    Optional<LanguageChoice> findLanguageChoiceByStudent(Student student);
}

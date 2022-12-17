package helper.api.jpa;

import helper.model.Language;
import helper.model.Student;
import helper.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> getTrainingsByStudent(Student student);

    @Query("SELECT t FROM Training t WHERE t.student = ?1 AND (t.language1 = ?2 OR t.language2 = ?2) AND (t.language2 = ?3 OR t.language1 = ?3)")
    Optional<Training> findTrainingByStudentAndLanguages(Student student, Language language1, Language language2);
}

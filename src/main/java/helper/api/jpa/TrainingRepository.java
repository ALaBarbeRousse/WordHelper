package helper.api.jpa;

import helper.model.Language;
import helper.model.Student;
import helper.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TrainingRepository extends JpaRepository<Training, String> {

    Optional<Training> findTrainingByStudentAndLanguage1AndLanguage2(Student student, Language language1, Language language2);
    Optional<Training> getTrainingByPhysicalId(UUID physicalId);
}

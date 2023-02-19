package helper.api.jpa;

import helper.model.Training;
import helper.model.TrainingResult;
import helper.model.Translation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainingResultRepository extends JpaRepository<TrainingResult, Long> {
    List<TrainingResult> findByTraining(Training training);
    @Query("SELECT tr FROM TrainingResult tr WHERE tr.training = ?1 AND tr.weight >= ?2 ORDER BY tr.weight DESC")
    List<TrainingResult> findAppropriateResults(Training training, Float threshold, Pageable pageable);

    List<TrainingResult> findTrainingResultByTrainingAndTranslationIn(Training training, List<Translation> translations);

    List<TrainingResult> findTrainingResultByTrainingAndWeightIsLessThan(Training training, Float weight);
}

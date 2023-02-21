package helper.api.service;

import helper.api.jpa.TrainingResultRepository;
import helper.model.Training;
import helper.model.TrainingResult;
import helper.model.Translation;
import helper.model.dto.CheckResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingResultService {
    private final TrainingResultRepository trainingResultRepository;
    private final TranslationService translationService;

    @Value("${training.factor.incorrect}")
    private Float incorrectFactor;

    @Value("${training.factor.correct}")
    private Float correctFactor;

    @Value("${training.factor.renew}")
    private Float renewFactor;

    public void saveResults(Training training, List<CheckResultDTO> results) {
        List<Translation> collectedTranslations = results.stream()
                .map(dto -> translationService.findTranslation(dto.getId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Это записанные ранее результаты, найденные по списку Translation
        List<TrainingResult> foundResults = trainingResultRepository
                .findTrainingResultByTrainingAndTranslationIn(training, collectedTranslations);
        List<Translation> foundResultsTranslations = foundResults.stream()
                .map(TrainingResult::getTranslation)
                .collect(Collectors.toList());

        List<TrainingResult> collected = results.stream()
                .map(dto -> {
                    Translation translation = translationService.findTranslation(dto.getId()).orElse(null);
                    if (Objects.nonNull(translation)) {
                        boolean correct = dto.getCorrect();
                        float weight;
                        if (foundResultsTranslations.contains(translation)) {
                            TrainingResult foundResult = foundResults.stream()
                                    .filter(trainingResult -> trainingResult.getTraining().equals(training))
                                    .filter(trainingResult -> trainingResult.getTranslation().equals(translation))
                                    .findFirst()
                                    .orElseGet(() -> new TrainingResult(training, translation, 1F));
                            if (correct) {
                                foundResult.setWeight(foundResult.getWeight() * correctFactor);
                            } else {
                                foundResult.setWeight(foundResult.getWeight() * incorrectFactor);
                            }

                            return foundResult;
                        } else {
                            weight = correct?correctFactor:incorrectFactor;
                            return new TrainingResult(training, translation, weight);
                        }
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        try {
            trainingResultRepository.saveAllAndFlush(removeDuplicates(collected));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<TrainingResult> removeDuplicates(List<TrainingResult> collected) {
        List<TrainingResult> diff = new ArrayList<>();
        collected.forEach(trainingResult -> {
            if (!diff.contains(trainingResult)) {
                diff.add(trainingResult);
            } else {
                diff.stream()
                        .filter(tr -> tr.getTranslation().equals(trainingResult.getTranslation()))
                        .forEach(tr -> tr.setWeight(tr.getWeight() * trainingResult.getWeight()));
            }
        });
        return diff;
    }

    /**
     * Обновляем значения веса для записей тренировки, у которых он меньше 1.
     */
    public void renewCorrectAnswers(Training training) {
        List<TrainingResult> found = trainingResultRepository.findTrainingResultByTrainingAndWeightIsLessThan(training, 1F);
        found.forEach(trainingResult -> trainingResult.setWeight(trainingResult.getWeight() * renewFactor));
        trainingResultRepository.saveAllAndFlush(found);
    }

    public List<TrainingResult> getTrainingResults(Training training, float threshold, int amount) {
        return  trainingResultRepository.findAppropriateResults(training, threshold, PageRequest.of(0, amount));
    }

    public List<TrainingResult> getAllTrainingResults(Training training) {
        return trainingResultRepository.findByTraining(training);
    }
}

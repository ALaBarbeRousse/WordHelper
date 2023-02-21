package helper.api.service;

import helper.api.jpa.TrainingRepository;
import helper.model.Collection;
import helper.model.Language;
import helper.model.LanguageChoice;
import helper.model.Student;
import helper.model.Training;
import helper.model.TrainingResult;
import helper.model.Translation;
import helper.model.dto.LanguageCreateDTO;
import helper.model.dto.TrainingDTO;
import helper.model.dto.TrainingResultDTO;
import helper.model.dto.TrainingWordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingService {
    private final LanguageChoiceService languageChoiceService;
    private final LanguageService languageService;
    private final TrainingRepository trainingRepository;
    private final TranslationService translationService;
    private final TrainingResultService trainingResultService;
    private final CollectionService collectionService;

    /* Количество слов в выдаваемом списке на тренировку */
    @Value("${training.amount}")
    private int amount;

    @Value("${training.factor.threshold}")
    private Float threshold;

    public TrainingDTO getWordTraining(String lang1, String lang2, String cName) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Student) {
            Student student = (Student) principal;

            /* Выбирать надо по тому, какие языки переданы */
            Language l1 = languageService.findLanguageByName(lang1)
                    .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(lang1)));
            Language l2 = languageService.findLanguageByName(lang2)
                    .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(lang2)));

            LanguageChoice languageChoice = languageChoiceService.findLanguageChoice(student)
                .orElseGet(() -> {
                    /* Создать новый LanguageChoice */
                    Language language1 = languageService.findLanguageByName(lang1)
                            .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(lang1)));
                    Language language2 = languageService.findLanguageByName(lang2)
                            .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(lang2)));
                    return languageChoiceService.saveLanguageChoice(new LanguageChoice(student, language1, language2));
                });
            /* Если выбор не совпадает с записанным, переписать его. */
            if (!languageChoice.equalsLanguages(lang1, lang2)) {
                languageChoice.setLang1(l1);
                languageChoice.setLang2(l2);
                languageChoiceService.saveLanguageChoice(languageChoice);
            }

            Collection foundCollection = collectionService.findCollection(cName, l1, l2).orElse(null);

            Training training = trainingRepository.findTrainingByStudentAndLanguage1AndLanguage2AndCollection(
                student,
                languageChoice.getLang1(),
                languageChoice.getLang2(),
                foundCollection
            ).orElseGet(() ->
                trainingRepository.save(new Training(student, languageChoice.getLang1(), languageChoice.getLang2(), foundCollection))
            );

            /* Берём ранее сохранённые результаты тренировок */
            List<TrainingResult> restrictedTR = trainingResultService.getTrainingResults(training, threshold, amount);

            List<Translation> trainTranslations =  restrictedTR.stream()
                    .map(tr -> {
                        int rounded = Math.round(tr.getWeight());
                        return Collections.nCopies(rounded, tr.getTranslation());
                    })
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            if (trainTranslations.size() > amount) {
                trainTranslations = trainTranslations.subList(0, amount);
            }

            /* Список надо дополнить случайными переводами из БД, не попавшими до сих пор в результаты */
            if (trainTranslations.size() < amount) {
                 List<Translation> usedTranslations = getAllTrainingResults(training).stream()
                         .map(TrainingResult::getTranslation)
                         .collect(Collectors.toList());
                 List<Translation> collectionTranslations = Optional.ofNullable(foundCollection)
                         .map(Collection::getTranslations)
                         .orElse(List.of());
                int restrictedCount = translationService.getRestrictedCount(
                        training.getLanguage1(),
                        training.getLanguage2(),
                        usedTranslations,
                        collectionTranslations
                );
                if (restrictedCount + trainTranslations.size() <= 0) {
                    trainingResultService.renewCorrectAnswers(training);
                    return new TrainingDTO(null, List.of());
                }

                /* Подобрать список неиспользованных переводов с уникальными элементами */
                int requiredAmount = amount - trainTranslations.size();   // Это требуемое количество переводов
                if (requiredAmount > restrictedCount) {
                    /* Если требуемое количество больше наличествующего (restrictedCount), добавляем все наличествующие */
                    List<Translation> restrictedTranslations = translationService.getRestrictedTranslations(training.getLanguage1(),
                            training.getLanguage2(),
                            usedTranslations,
                            collectionTranslations
                    );
                    trainTranslations.addAll(restrictedTranslations);
                } else {
                    List<Translation> excludeList = new ArrayList<>(usedTranslations);
                    while (trainTranslations.size() < amount) {
                        Translation toAdd = translationService.getRandomTranslation(
                                training.getLanguage1(),
                                training.getLanguage2(),
                                excludeList,
                                collectionTranslations,
                                restrictedCount);
                        if (Objects.nonNull(toAdd)) {
                            excludeList.add(toAdd);
                            trainTranslations.add(toAdd);
                        }
                    }
                }
            }

            /* Удаляем дубликаты */
            trainTranslations = trainTranslations.stream().distinct().collect(Collectors.toList());

            List<TrainingWordDTO> words = trainTranslations.stream()
                    .map(translation -> {
                        if (Objects.isNull(translation.getPhysicalId())) {
                            translation.setPhysicalId(UUID.randomUUID());
                            return translationService.saveTranslation(translation);
                        }
                        return translation;
                    })
                    .map(translation -> new TrainingWordDTO(
                            translation.getPhysicalId(),
                            translation.getWord().getWriting(),
                            translation.getTranslation().getWriting()))
                    .collect(Collectors.toList());

            Collections.shuffle(words); // Перемешиваем слова

            return new TrainingDTO(training.getPhysicalId(), words);
        } else {
            throw new IllegalStateException("Couldn't determine current user");
        }
    }

    private List<TrainingResult> getAllTrainingResults(Training training) {
        return trainingResultService.getAllTrainingResults(training);
    }

    /* Записываем результаты тренировки в специальную табличку */
    public void saveTrainingResults(TrainingResultDTO dto) {
        trainingRepository.getTrainingByPhysicalId(dto.getId())
            .ifPresent(training -> trainingResultService.saveResults(training, dto.getResults()));
    }
}

package helper.api.service;

import helper.api.jpa.TrainingRepository;
import helper.model.Language;
import helper.model.LanguageChoice;
import helper.model.Student;
import helper.model.Training;
import helper.model.Translation;
import helper.model.dto.CheckRequestDTO;
import helper.model.dto.LanguageCreateDTO;
import helper.model.dto.TrainingWordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingService {
    private final LanguageChoiceService languageChoiceService;
    private final LanguageService languageService;
    private final TrainingRepository trainingRepository;
    private final TranslationService translationService;

    /* TODO */
    public TrainingWordDTO getTrainingWord(String lang1, String lang2) {
        Object pr = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (pr instanceof Student) {
            Student student = (Student) pr;

            /* Исправить выбор языка - выбирать надо по тому, какие языки переданы, а не какие записаны */
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
            if (!languageChoice.equalsLanguages(l1.getName(), l2.getName())) {
                languageChoice.setLang1(l1);
                languageChoice.setLang2(l2);
                languageChoiceService.saveLanguageChoice(languageChoice);
            }

            /* TODO Это должно использоваться при подборе веса */
            Training training = trainingRepository.findTrainingByStudentAndLanguages(
                    student,
                    languageChoice.getLang1(),
                    languageChoice.getLang2()
            ).orElseGet(() ->
                    trainingRepository.save(new Training(student, languageChoice.getLang1(), languageChoice.getLang2()))
            );

            /* TODO Надо выбрать из таблицы переводов случайный (с учётом веса) перевод,
            *   который бы соответствовал выбранным языкам */
            Translation randomTranslation = translationService.getRandomTranslation(training);
            if (
                    lang1.equals(randomTranslation.getLanguage1().getName())
                    && lang2.equals(randomTranslation.getLanguage2().getName())
            ) {
                return new TrainingWordDTO(randomTranslation.getWord1().getWriting(), randomTranslation.getWord2().getWriting());
            } else {
                return new TrainingWordDTO(randomTranslation.getWord2().getWriting(), randomTranslation.getWord1().getWriting());
            }
        } else {
            throw new IllegalStateException("Couldn't determine current user");
        }
    }

    /* TODO Записываем результаты тренировки в специальную табличку */
    public void saveTrainingResults(CheckRequestDTO dto) {
        return;
    }
}

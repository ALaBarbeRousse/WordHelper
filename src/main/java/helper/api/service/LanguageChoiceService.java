package helper.api.service;

import helper.api.jpa.LanguageChoiceRepository;
import helper.model.LanguageChoice;
import helper.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LanguageChoiceService {
    private final LanguageChoiceRepository repository;

    public Optional<LanguageChoice> findLanguageChoice(Student student) {
        return repository.findLanguageChoiceByStudent(student);
    }

    public void saveLanguageChoice(LanguageChoice languageChoice) {
        repository.save(languageChoice);
    }
}

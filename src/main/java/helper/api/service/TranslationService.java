package helper.api.service;

import helper.model.Language;
import helper.model.Translation;
import helper.model.Word;
import helper.api.jpa.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TranslationService {
    private final TranslationRepository translationRepository;

    /*
     * Try to find translation
     */
    public Optional<Translation> findTranslation(Language lang1, Word word1, Language lang2, Word word2) {
        return translationRepository.findTranslationByLanguage1AndWord1AndLanguage2AndWord2(
                lang1,
                word1,
                lang2,
                word2
        );
    }

    public Translation saveTranslation(Translation translation) {
        return translationRepository.save(translation);
    }
}

package helper.api.rest;

import helper.api.service.LanguageService;
import helper.api.service.TranslationService;
import helper.api.service.WordService;
import helper.model.Language;
import helper.model.Word;
import helper.model.dto.FindTranslationDTO;
import helper.model.dto.FindTranslationResultDTO;
import helper.model.dto.WordArticleEditDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/word")
@RequiredArgsConstructor
public class WordController {
    private final WordService wordService;
    private final TranslationService translationService;
    private final LanguageService languageService;

    @PostMapping
    public void saveWordPair(@RequestBody WordArticleEditDTO dto) {
        wordService.saveWordPair(dto);
    }

    /* Пытаемся найти перевод для заданного слова */
    @PostMapping(path =  "/translate")
    public FindTranslationResultDTO findTranslation(@RequestBody FindTranslationDTO dto) {
        /* Если язык не найден, возвращаем null */
        Language langFrom = languageService.findLanguageByName(dto.getFrom()).orElse(null);
        Language langTo = languageService.findLanguageByName(dto.getTo()).orElse(null);
        if (langFrom == null || langTo == null) {
            return null;
        }

        String translation = wordService.findTranslation(langFrom, dto.getWord(), langTo);
        return new FindTranslationResultDTO(translation);
    }
}

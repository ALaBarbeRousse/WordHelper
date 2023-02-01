package helper.api.rest;

import helper.api.service.LanguageService;
import helper.api.service.WordService;
import helper.model.Language;
import helper.model.dto.FindTranslationDTO;
import helper.model.dto.FindTranslationResultDTO;
import helper.model.dto.LanguageCreateDTO;
import helper.model.dto.WordArticleEditDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/word")
@RequiredArgsConstructor
public class WordController {
    private final WordService wordService;
    private final LanguageService languageService;

    @PostMapping
    public void saveWordPair(@RequestBody WordArticleEditDTO dto) {
        wordService.saveWordPair(dto);
    }

    /* Пытаемся найти перевод для заданного слова */
    @PostMapping(path =  "/translate")
    public FindTranslationResultDTO findTranslation(@RequestBody FindTranslationDTO dto) {
        Language langFrom = languageService.findLanguageByName(dto.getFrom())
                .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(dto.getFrom())));
        Language langTo;
        if (Objects.isNull(dto.getTo())) {
            langTo = null;
        } else {
            langTo = languageService.findLanguageByName(dto.getTo())
                    .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(dto.getTo())));
        }

        List<String> suspects = wordService.findSimilarWords(dto.getWord(), langFrom);
        return wordService.findTranslation(langFrom, dto.getWord(), langTo)
                .map(s -> new FindTranslationResultDTO(suspects, s))
                .orElse(new FindTranslationResultDTO(suspects, null));
    }
}

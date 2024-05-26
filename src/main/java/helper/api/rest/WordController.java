package helper.api.rest;

import helper.api.service.LanguageService;
import helper.api.service.SoundService;
import helper.api.service.WordService;
import helper.model.Language;
import helper.model.Voice;
import helper.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/word")
@RequiredArgsConstructor
@Slf4j
public class WordController {
    private final WordService wordService;
    private final LanguageService languageService;
    private final SoundService soundService;

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
            .map(translation -> {
                FindTranslationResultDTO retValue = new FindTranslationResultDTO(
                    suspects,
                    soundService.getVoicesPresent(langFrom, dto.getWord()),
                    translation,
                    soundService.getVoicesPresent(langTo, translation)
                );
                retValue.setWordSounds(soundService.getVoices(langFrom, dto.getWord()));
                retValue.setTranslationSounds(soundService.getVoices(langTo, translation));

                return retValue;
            })
            .orElseGet(() -> new FindTranslationResultDTO(
                suspects,
                false,
                null,
                false)
            );
    }

    @SneakyThrows
    @PostMapping(path = "/voice/random")
    public VoiceDTO findVoice(@RequestBody GetVoiceDTO getVoiceDTO) {
        Voice voice = soundService.getRandomVoice(getVoiceDTO.getLanguage(), getVoiceDTO.getWord());

        return new VoiceDTO(
            voice.getSpeaker().getName(),
            voice.getWord().getLanguage().getName(),
            voice.getWord().getWriting(),
            voice.getSound()
        );
    }

    @DeleteMapping(path = "/translate")
    public void deleteTranslation(@RequestBody WordArticleEditDTO dto) {
        wordService.deleteTranslation(dto);
    }
}

package helper.api.rest;

import helper.api.service.TranslationService;
import helper.api.service.web.VoiceService;
import helper.model.Translation;
import helper.model.dto.SoundingRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voice")
@RequiredArgsConstructor
@Slf4j
public class VoiceController {
    private final VoiceService speechatorsVoiceService;
    private final TranslationService translationService;

    @GetMapping
    public void getVoice(@RequestParam(name = "lang") String language, @RequestParam String word) {
        /* Для начала просто отправим каким-то образом запрос на сайт и запишем ответ в виде mp3-файла. */
        log.info("Запрос на получение звука \"{}\" ({}).", word, language);

        speechatorsVoiceService.getSound(language, word);
    }

    @GetMapping(value = "/random")
    public List<Translation> getRandomDeafTranslation(@RequestParam(name = "lang1") String lang1, @RequestParam(name = "lang2") String lang2) {
//        log.info("Получен запрос на получение случайного неозвученного перевода.");
        return translationService.getRandomDeafTranslation(lang1, lang2, 5);
    }

    @PostMapping(value = "/voices")
    public void findVoices(@RequestBody List<SoundingRequestDTO> dtos) {
//        log.info("Получен запрос на озвучку слов");
        speechatorsVoiceService.fetchSounds(dtos);
    }
}

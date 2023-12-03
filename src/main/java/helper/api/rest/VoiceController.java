package helper.api.rest;

import helper.api.service.web.SpeechatorsVoiceService;
import helper.api.service.web.VoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/voice")
@RequiredArgsConstructor
@Slf4j
public class VoiceController {
    private final VoiceService speechatorsVoiceService;

    @GetMapping
    public void getVoice(@RequestParam(name = "lang") String language, @RequestParam String word) {
        /* Для начала просто отправим каким-то образом запрос на сайт и запишем ответ в виде mp3-файла. */
        log.info("Запрос на получение звука \"{}\" ({}).", word, language);

        speechatorsVoiceService.getSound(language, word);
    }

    @GetMapping(value = "/random")
    public void getRandomTranslation(@RequestParam(name = "lang1") String lang1, @RequestParam(name = "lang2") String lang2) {
        /* todo */
        log.info("Получен запрос на получение случайного неозвученного перевода.");
    }
}

package helper.api.rest;

import helper.api.service.web.SpeechatorsVoiceService;
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
//    private final TTSFreeVoiceService voiceService;
    private final SpeechatorsVoiceService voiceService;

    @GetMapping
    public void getVoice(@RequestParam(name = "lang") String language, @RequestParam String word) {
        /* TODO Для начала просто отправим каким-то образом запрос на сайт и запишем ответ в виде mp3-файла. */
        log.info("Запрос на получение звука");

        voiceService.getSound(language, word);
    }
}

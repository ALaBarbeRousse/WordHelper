package helper.scheduled;

import helper.api.service.TranslationService;
import helper.api.service.web.VoiceService;
import helper.model.Word;
import helper.model.dto.SoundingRequestDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

@Slf4j
public class GetSoundBackgroundTask extends TimerTask {
    private final TranslationService translationService;
    private final VoiceService voiceService;

    public GetSoundBackgroundTask(TranslationService translationService, VoiceService voiceService) {
        this.translationService = translationService;
        this.voiceService = voiceService;
    }

    @Override
    public void run() {
        /* Загрузить неозвученное слово */
        List<Word> deafWords = translationService.getRandomDeafWord(1);
        Word deafWord = deafWords.stream()
            .findAny()
            .orElse(null);

        if (Objects.isNull(deafWord)) {
            return;
        }
        log.info("Фоновая загрузка озвучки для '{}' ({}).", deafWord.getWriting(), deafWord.getLanguage().getName());

        SoundingRequestDTO dto = new SoundingRequestDTO(
            deafWord.getLanguage().getName(),
            deafWord.getWriting()
        );

        try {
            voiceService.fetchSounds(Collections.singletonList(dto));
        } catch (IOException e) {
            log.error("Ошибка при выполнении задачи фоновой загрузки озвучки для слова {} ({})", deafWord.getWriting(), deafWord.getLanguage().getName());
        }
    }
}

package helper.api.service;

import helper.api.jpa.VoiceRepository;
import helper.model.Language;
import helper.model.Speaker;
import helper.model.Voice;
import helper.model.Word;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceHandler {
    private final VoiceRepository voiceRepository;

    public Voice findOrCreateVoice(Word word, Speaker speaker, byte[] value) {
        return voiceRepository.findByWordAndSpeaker(word, speaker)
            .orElseGet(() -> voiceRepository.save(new Voice(word, speaker, value)));
    }

    /* Определяем количество переводов для данного слова */
    public boolean getVoicesPresent(Language language, String word) {
        return voiceRepository.getVoicesPresent(word, language) > 0;
    }

    public Voice getVoice(Word word) {
        return voiceRepository.getRandomVoice(word, PageRequest.of(0, 1)).get(0);
    }
}

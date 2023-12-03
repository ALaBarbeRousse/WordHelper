package helper.api.service;

import helper.api.jpa.VoiceRepository;
import helper.model.Speaker;
import helper.model.Voice;
import helper.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoiceHandler {
    private final VoiceRepository voiceRepository;

    public Voice findOrCreateVoice(Word word, Speaker speaker, byte[] value) {
        return voiceRepository.findByWordAndSpeaker(word, speaker)
            .orElseGet(() -> voiceRepository.save(new Voice(word, speaker, value)));
    }
}

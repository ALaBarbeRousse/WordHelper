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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Voice getRandomVoice(Word word) {
        List<Voice> got = voiceRepository.getRandomVoice(word, PageRequest.of(0, 1));
        if (got.isEmpty()) {
            return null;
        } else {
            return got.get(0);
        }
    }

    /**
     * todo Получаем все голоса для данного слова на данном языке
     * @param language
     * @param word
     * @return
     */
    public Map<String,byte[]> getVoices(Language language, String word) {
        return voiceRepository.getVoices(word, language).stream()
            .collect(Collectors.toMap(voice -> voice.getSpeaker().getName(), Voice::getSound));
    }
}

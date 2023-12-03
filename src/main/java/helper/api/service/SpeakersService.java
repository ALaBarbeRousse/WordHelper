package helper.api.service;

import helper.api.jpa.SpeakersRepository;
import helper.model.Language;
import helper.model.Speaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpeakersService {
    private final SpeakersRepository speakersRepository;

    /* Находим спикера по его имени или создаём, если не найден */
    public Speaker findOrCreateSpeaker(Language language, String name) {
        return speakersRepository.findByLanguageAndName(language, name)
            .orElseGet(() -> speakersRepository.save(new Speaker(language, name)));
    }
}

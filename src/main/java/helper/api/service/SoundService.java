package helper.api.service;

import helper.model.Language;
import helper.model.Speaker;
import helper.model.Voice;
import helper.model.Word;
import helper.model.dto.LanguageCreateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SoundService {
    private final WordService wordService;
    private final SpeakersService speakersService;
    private final LanguageService languageService;
    private final VoiceHandler voiceHandler;

    public void saveVoices(String languageName, String word, Map<String, byte[]> voicesByName) {
        Language language = languageService.findOrCreateLanguageByName(languageName);

        Word foundWord = wordService.findWord(word)
            .orElseThrow(() -> new RuntimeException(String.format("Couldn't find word '%s'", word)));

        for (Map.Entry<String, byte[]> entry: voicesByName.entrySet()) {
            Speaker speaker = speakersService.findOrCreateSpeaker(language, entry.getKey());
            /* А дальше записывать звук в таблицу voice */
            Voice voice = voiceHandler.findOrCreateVoice(foundWord, speaker, entry.getValue());
        }
    }

    public boolean getVoicesPresent(Language language, String word) {
        return voiceHandler.getVoicesPresent(language, word);
    }
    public Map<String,byte[]> getVoices(Language language, String word) {
        return voiceHandler.getVoices(language, word);
    }

    public Voice getRandomVoice(String language, String writing) {
        Language lang = languageService.findLanguageByName(language)
            .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(language)));
        Word word = wordService.getOrSaveWord(writing, lang);
        return voiceHandler.getRandomVoice(word);
    }

    /**
     * Получаем случайную озвучку для слова
     * @param word
     * @return
     */
    public Voice getRandomVoice(Word word) {
        return voiceHandler.getRandomVoice(word);
    }
}

package helper.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
public class FindTranslationResultDTO {
    public FindTranslationResultDTO(List<String> suspect, boolean wordVoicePresent, String translation, boolean translationVoicePresent) {
        this.suspect = suspect;
        this.translation = translation;
        this.wordVoicePresent = wordVoicePresent;
        this.translationVoicePresent = translationVoicePresent;
    }

    private List<String> suspect;
    private String translation;

    private boolean wordVoicePresent;
    private Map<String, byte[]> wordSounds;

    private boolean translationVoicePresent;
    private Map<String, byte[]> translationSounds;
}

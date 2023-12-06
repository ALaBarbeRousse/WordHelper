package helper.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoiceDTO {
    private String speaker;
    private String language;
    private String word;
    private byte[] sound;
}

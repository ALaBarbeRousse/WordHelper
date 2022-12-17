package helper.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CheckResultDTO {
    private String word;
    private String translation;
    private Boolean correct;
}

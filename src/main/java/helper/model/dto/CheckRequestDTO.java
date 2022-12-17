package helper.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckRequestDTO {
    private String lang1, lang2;
    private CheckResultDTO result;
}

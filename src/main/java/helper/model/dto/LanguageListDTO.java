package helper.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LanguageListDTO {
    private List<LanguageDTO> list;

    private List<Long> usedLanguages;
}

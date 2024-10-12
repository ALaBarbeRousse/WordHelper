package helper.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CheckResultDTO {
    private UUID id;
    private Boolean correct;
}

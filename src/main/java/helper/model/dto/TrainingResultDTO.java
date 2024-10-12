package helper.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TrainingResultDTO {
    private UUID id;
    private List<CheckResultDTO> results;
}

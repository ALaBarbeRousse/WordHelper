package helper.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TrainingDTO {
    private UUID id;
    private List<TrainingWordDTO> words;
}

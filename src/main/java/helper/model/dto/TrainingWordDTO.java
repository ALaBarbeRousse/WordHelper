package helper.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TrainingWordDTO {
    private UUID p;
    private String w;
    private byte[] ws;
    private String t;
    private byte[] ts;
}

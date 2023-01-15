package helper.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CollectionSaveDTO {
    private String name;
    private List<String> langs;
    private List<List<String>> words;
}

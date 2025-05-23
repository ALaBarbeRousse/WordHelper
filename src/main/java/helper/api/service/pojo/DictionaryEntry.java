package helper.api.service.pojo;

//import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DictionaryEntry {

//    @NotEmpty(message = "Список языков не должен быть пустым")
    private List<String> languages;

//    @NotEmpty(message = "Список переводов не должен быть пустым")
    private List<List<String>> translations;
}

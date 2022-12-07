package helper.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WordArticleEditDTO {
    private String lang1, lang2;
    private String word1, word2;
}

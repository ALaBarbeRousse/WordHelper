package helper.api.rest;

import helper.api.service.WordService;
import helper.model.dto.WordArticleEditDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/word")
@RequiredArgsConstructor
public class WordController {
    private final WordService wordService;

    @PostMapping
    public void saveWordPair(@RequestBody WordArticleEditDTO dto) {
        wordService.saveWordPair(dto);
    }
}

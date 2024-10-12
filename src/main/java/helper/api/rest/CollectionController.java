package helper.api.rest;

import helper.api.service.CollectionService;
import helper.model.Translation;
import helper.model.dto.CollectionSaveDTO;
import helper.model.dto.FindTranslationDTO;
import helper.model.dto.TranslationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/collection")
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionService collectionService;
    @GetMapping("/names")
    public List<String> getCollectionNames() {
        return collectionService.getCollectionNames();
    }

    @PostMapping("/translation")
    public List<TranslationDTO> findTranslation(@RequestBody FindTranslationDTO dto) {
        return collectionService.findTranslation(dto).stream()
                .map(Translation::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public void saveCollection(@RequestBody CollectionSaveDTO dto) {
        collectionService.saveCollection(dto);
    }

    @GetMapping
    public List<List<String>> getCollection(@RequestParam("name") String name,
                                            @RequestParam("lang1") String lang1,
                                            @RequestParam("lang2") String lang2) {
        return collectionService.getCollection(name, lang1, lang2);
    }
}

package helper.api.rest;

import helper.api.service.ImportService;
import helper.api.service.TranslationService;
import helper.model.dto.DictionaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/translation")
@RequiredArgsConstructor
@Slf4j
public class TranslationController {
    private final TranslationService translationService;
    private final ImportService importService;

    @GetMapping("/languages")
    public List<String[]> getExportLanguages() {
        return translationService.getAvailableLanguages().stream()
                .map(lp -> new String[] {lp.getL1().getName(), lp.getL2().getName()})
                .collect(Collectors.toList());
    }

    /**
     * Выгружаем словари по заданным языковым парам
     *
     * @param dictionaries - Список языковых пар, по которым надо выгрузить словари
     */
    @PostMapping("/export")
    public List<DictionaryDTO> getExportedDictionaries(@RequestBody List<List<String>> dictionaries) {
        return translationService.findTranslations(dictionaries);
    }

    /**
     * Загружаем ранее экспортированный словарь
     */
    @PostMapping("/import")
    public ResponseEntity<String> importSavedDictionaries(@RequestBody MultipartFile file) {
        try {
            importService.handleFile(file);

            return ResponseEntity.ok("Словарь успешно импортирован.");
        } catch (Exception e) {
            log.error("Ошибка при импорте словаря", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при импорте словаря");
        }
    }
}

package helper.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import helper.api.service.pojo.DictionaryEntry;
import helper.model.Language;
import helper.model.Translation;
import helper.model.Word;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportService {
    private final LanguageService languageService;
    private final WordService wordService;
    private final TranslationService translationService;

    public void handleFile(MultipartFile file) {
        ObjectMapper mapper = new ObjectMapper();
        try {
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            if (true) {
//                throw new IOException("А просто так");
//            }

            /*  Итак, получили файл со словарём. Теперь его надо как-то обработать. */
            /* 1) Преобразовать в POJO */
            DictionaryEntry[] entries = mapper.readerForArrayOf(DictionaryEntry.class).readValue(file.getBytes());

            Stream.of(entries)
                .forEach(entry -> {
                    /* 2) Выделить языки и посмотреть, есть ли такие. Если нет - создать */
                    List<Language> languageList = entry.getLanguages().stream()
                        .map(languageService::findOrCreateLanguageByName)
                        .collect(Collectors.toList());

//                        forEach(languageService::findOrCreateLanguageByName);

                    /* 3) Берём перевод по порядку их списка */
                    entry.getTranslations()
                        .forEach(translationList -> {
                            /*
                            * В каждом переводе два слова.
                            * Для начала - убедиться, что они оба есть. Если нет - создать.
                            * */
                            List<Word> wordList = new ArrayList<>();
                            for (int i = 0; i < 2; i++) {
                                Language language = languageList.get(i);
                                String writing = translationList.get(i);

                                wordList.add(wordService.getOrSaveWord(writing, language));
                            }
                            translationService.saveTranslations(
                                Arrays.asList(
                                    new Translation(languageList.get(0), wordList.get(0), languageList.get(1), wordList.get(1), new Date()),
                                    new Translation(languageList.get(1), wordList.get(1), languageList.get(0), wordList.get(0), new Date())
                                )
                            );
                        });
                });
        } catch (Exception e) {
            log.error("Ошибка при импорте словаря", e);
            throw new RuntimeException(e);
        }
    }
}

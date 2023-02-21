package helper.api.service;

import helper.api.jpa.CollectionRepository;
import helper.model.Collection;
import helper.model.Language;
import helper.model.Student;
import helper.model.Translation;
import helper.model.Word;
import helper.model.dto.CollectionSaveDTO;
import helper.model.dto.FindTranslationDTO;
import helper.model.dto.LanguageCreateDTO;
import helper.model.dto.TranslationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CollectionService {
    private final CollectionRepository collectionRepository;
    private final LanguageChoiceService languageChoiceService;
    private final TranslationService translationService;
    private final LanguageService languageService;
    private final WordService wordService;

    /* Возвращаем имена подборок текущего пользователя с его установками языков */
    public List<String> getCollectionNames() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Student) {
            Student student = (Student) principal;
            return languageChoiceService.findLanguageChoice(student)
                    .map(lc -> collectionRepository.findCollectionsByOwnerAndLang1AndLang2(student, lc.getLang1(), lc.getLang2()))
                    .orElseGet(() -> collectionRepository.findCollectionsByOwner(student))
                    .stream().map(Collection::getName).collect(Collectors.toList());
        }
        return List.of();
    }

    public List<Translation> findTranslation(FindTranslationDTO dto) {
        /* Пробуем найти подходящие слова */
        Optional<Language> lang1 = languageService.findLanguageByName(dto.getFrom());
        Optional<Language> lang2 = languageService.findLanguageByName(dto.getTo());
        if (lang1.isPresent() && lang2.isPresent()) {
            List<Word> matchingWords = wordService.findMatchingWords(dto.getWord());
            return matchingWords.stream()
                    .map(word -> translationService.findTranslation(lang1.get(), word, lang2.get()).orElse(null))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(o -> o.getWord().getWriting()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    public Collection saveCollection(CollectionSaveDTO dto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Student) {
            Optional<Collection> collectionOpt = collectionRepository.findCollectionByNameAndOwner(dto.getName(), (Student) principal);
            if (collectionOpt.isEmpty()) {
                return createCollection(dto);
            } else {
                Collection collection = collectionOpt.get();
                Language lang1 = languageService.findLanguageByName(dto.getLangs().get(0))
                        .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(dto.getLangs().get(0))));
                collection.setLang1(lang1);
                Language lang2 = languageService.findLanguageByName(dto.getLangs().get(1))
                        .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(dto.getLangs().get(1))));
                collection.setLang2(lang2);
                List<Translation> translations = dto.getWords().stream()
                        .map(words -> {
                            Word w1 = wordService.findWord(words.get(0))
                                    .orElseThrow(() ->
                                            new IllegalArgumentException(format("Couldn't find word '%s'", words.get(0)))
                                    );
                            Word w2 = wordService.findWord(words.get(1))
                                    .orElseThrow(() ->
                                            new IllegalArgumentException(format("Couldn't find word '%s'", words.get(1)))
                                    );
//                            Optional<Translation> found = translationService.findTranslation(lang1, w1, lang2, w2); // todo

                            return translationService.findTranslation(lang1, w1, lang2, w2)
                                    .orElseThrow(() ->
                                            new IllegalArgumentException(format("Couldn't find translation '%s'(%s) -> '%s'(%s)",
                                                    words.get(0), dto.getLangs().get(0), words.get(1), dto.getLangs().get(1))
                                            )
                                    );
                        }).collect(Collectors.toList());
                collection.setTranslations(translations);
                return collectionRepository.save(collection);
            }
        }
        throw new IllegalStateException("Couldn't identify current user");
    }

    /**
     * Create a new collection
     * @param dto incoming parameters
     * @return created collection
     */
    private Collection createCollection(CollectionSaveDTO dto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Student) {
            Student owner = (Student) principal;
            Language lang1 = languageService.findLanguageByName(dto.getLangs().get(0))
                    .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(dto.getLangs().get(0))));
            Language lang2 = languageService.findLanguageByName(dto.getLangs().get(1))
                    .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(dto.getLangs().get(1))));

            List<Translation> translations = dto.getWords().stream()
                .map(wordsPair -> {
                    Word word1 = wordService.findWord(wordsPair.get(0)).orElseThrow(
                        () -> new IllegalArgumentException(format("Couldn't find word'%s", wordsPair.get(0))));
                    Word word2 = wordService.findWord(wordsPair.get(1)).orElseThrow(
                        () -> new IllegalArgumentException(format("Couldn't find word'%s", wordsPair.get(1))));
                    return translationService.findTranslation(lang1, word1, lang2, word2)
                        .orElseThrow(() -> new IllegalArgumentException(format("Couldn't find translation '%s <-> %s'",
                                wordsPair.get(0),
                                wordsPair.get(1))
                            ));
                }).collect(Collectors.toList());

            Collection collection = new Collection(
                    dto.getName(),
                    owner,
                    lang1,
                    lang2,
                    translations
            );
            return collectionRepository.save(collection);
        } else {
            throw new IllegalStateException("Couldn't identify current user.");
        }
    }

    public List<List<String>> getCollection(String name, String lang1, String lang2) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Student) {
//            Optional<Collection> found = collectionRepository.findCollectionByNameAndOwner(name, (Student) principal);
//            List<TranslationDTO> dtoss = found.map(Collection::getContentDTO).orElse(null);
//            List<TranslationDTO> sorted = dtoss.stream()
//                    .sorted(Comparator.comparing(TranslationDTO::getWord1))
//                    .collect(Collectors.toList());

            return collectionRepository.findCollectionByNameAndOwner(name, (Student) principal)
                .map(Collection::getContentDTO)
                .map(dtos -> dtos.stream()
                    .sorted(Comparator.comparing(TranslationDTO::getWord1).reversed() )
                    .map(dto -> {
                        List<String> translationContent = new ArrayList<>();
                        if (dto.getLang1().equalsIgnoreCase(lang1) && dto.getLang2().equalsIgnoreCase(lang2)) {
                            translationContent.add(dto.getWord1());
                            translationContent.add(dto.getWord2());
                        } else if (dto.getLang1().equalsIgnoreCase(lang2) && dto.getLang2().equalsIgnoreCase(lang1)) {
                            translationContent.add(dto.getWord2());
                            translationContent.add(dto.getWord1());
                        }
                        return translationContent;
                    }).collect(Collectors.toList())
                ).orElse(null);
        }

        throw new IllegalStateException("Couldn't identify current user.");
    }

    public Optional<Collection> findCollection(String cName, Language l1, Language l2) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Student) {
            Student student = (Student)principal;
            return collectionRepository.findCollectionByOwnerAndNameAndLanguages(student, cName, l1, l2);
        }

        throw new IllegalStateException("Couldn't identify current user.");
    }
}

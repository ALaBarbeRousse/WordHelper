package helper.api.service.web;

import helper.misc.FileHelper;
import helper.model.Language;
import helper.model.dto.SoundingRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Primary
public class TextospeechVoiceService extends VoiceService {
    private static final String BASE_URL = "https://textospeech.net/";

    private static final Duration WAIT_DURATION = Duration.of(30, ChronoUnit.SECONDS);

    private static final Map<String, String> LANGUAGE_TO_VALUE = new HashMap<>() {{
        this.put("english", "en-GB");
        this.put("русский", "ru-RU");
        this.put("suomi", "fi-FI");
        this.put("deutsch", "de-DE");
        this.put("français", "fr-FR");
        this.put("español", "es-ES");
        this.put("italiano", "it-IT");
    }};

    @Override
    public void getSound(String language, String word) {
        return;
    }

    @Override
    public void fetchSounds(List<SoundingRequestDTO> dtos) {
        /* Надо разложить входящие данные по языкам */
        Map<String, List<String>> map = new HashMap<>();
        for (SoundingRequestDTO dto: dtos) {
            Language lang = languageService.findLanguageByName(dto.getLanguage()).orElse(null);
            if (lang != null && !map.containsKey(lang.getName())) {
                map.put(lang.getName(), new ArrayList<>());
            }
            if (lang != null) {
                map.get(lang.getName()).add(dto.getWord());
            }
        }

        /* Проверить, нет ли озвучки для этого слова */
        Map<Language, List<String>> filteredMap = new HashMap<>();
        for (String lang: map.keySet()) {
            List<String> words = map.get(lang);
            Language language = languageService.findLanguageByName(lang).orElse(null);
            List<String> filtered = words.stream()
                .filter(word -> !soundService.getVoicesPresent(language, word))
                .collect(Collectors.toList());
            filteredMap.put(language, filtered);
        }

        WebDriver driver = new ChromeDriver(options);
        try {
            /* Открываем базу */
            driver.get(BASE_URL);
            /* Дожидаемся открытия странички */
            new WebDriverWait(driver, WAIT_DURATION)
                .until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

            /* Находим селектор языков */
            By selectLanguageBy = By.id("languages");
            Select languageSelect = new Select(driver.findElement(selectLanguageBy));

            /* Находим селектор спикеров */
            By speakersBy = By.id("voices");
            Select speakersSelect = new Select(driver.findElement(speakersBy));

            /* Находим текстовое поле */
            By textAreaBy = By.xpath("//div[@class='col-12 sctioin2Bg']/textarea");
            WebElement textArea = driver.findElement(textAreaBy);

            /* Находим кнопку "Generate" */
            By generateButtonBy = By.xpath("//div[@class='row-center-between mt-2']/button[@class='btn primary-btn bg-white mr-2']");
            WebElement generateButton = driver.findElement(generateButtonBy);

            /* Кнопка "Download" */
            By downloadButtonBy = By.xpath("//button[@class='btn btn-xl primary-btn text-center mx-auto mt-5 p-3']");

            /* Кнопка "Generate mare" */
            By generateMoreLinkBy = By.xpath("//section/div[@class='container upgrade-section']/div/a");

            File downloadFolder = new File(downloadFilePath);

            for (Entry<Language, List<String>> entry: filteredMap.entrySet()) {
                /* Переключить язык на указанный */
                languageSelect.selectByValue(LANGUAGE_TO_VALUE.get(entry.getKey().getName()));

                /* Собрать голоса спикеров */
                List<String> voices = speakersSelect.getOptions().stream()
                    .map(element -> element.getAttribute("value"))
                    .collect(Collectors.toList());

                /* Проходим по всем словам */
                for (String word: entry.getValue()) {
                    Map<String, byte[]> voicesByName = new HashMap<>();

                    /* Очищаем поле ввода и вставляем слово */
                    textArea.clear();
                    textArea.sendKeys(word);

                    /* Проходим по всем голосам */
                    for (String voice: voices) {
                        /* Очищаем папку голосов */
                        FileHelper.emptyFolder(downloadFolder);

                        /* Выбираем голос */
                        speakersSelect.selectByValue(voice);
                        /* Запускаем генерацию */
                        generateButton.click();

                        /* Ждём появления кнопки "Download" и жмём её */
                        new WebDriverWait(driver, WAIT_DURATION)
                            .until(ExpectedConditions.visibilityOfElementLocated(downloadButtonBy));
                        WebElement downloadButton = driver.findElement(downloadButtonBy);
                        downloadButton.click();

                        /* Жмём ссылку "Generate more" */
                        WebElement generateMoreLink = driver.findElement(generateMoreLinkBy);
                        generateMoreLink.click();
                        /* Голоса загружены */

                        /* Забрать скачанный файл */
                        voicesByName.put(voice, FileHelper.getFileBytes(FileHelper.getTheOnlyFile(downloadFolder)));
                    }
                    FileHelper.emptyFolder(downloadFolder);

                    /* Записать забранное в БД */
                    soundService.saveVoices(entry.getKey().getName(), word, voicesByName);
                }
            }
        } catch (TimeoutException e) {
            log.error("Таймаут при получении звука.");
        } catch (Exception e) {
            log.error("Ошибка при получении звука: {}.", e.getMessage(), e);
        } finally {
            driver.close();
        }
    }
}

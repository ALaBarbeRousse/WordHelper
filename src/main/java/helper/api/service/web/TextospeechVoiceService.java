package helper.api.service.web;

import helper.model.Language;
import helper.model.dto.SoundingRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.TimeoutException;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Primary
public class TextospeechVoiceService extends VoiceService {
    private static final String BASE_URL = "https://textospeech.net/text-to-speech/";

    private static final Map<String, String> LANGUAGE_TO_VALUE = new HashMap<>() {{
        this.put("english", "en-GB");
        this.put("русский", "ru-RU");
        this.put("suomi", "fi-FI");
        this.put("deutsch", "de-DE");
        this.put("français", "fr-FR");
        this.put("español", "es-ES");
        this.put("português", "pt-PT");
        this.put("italiano", "it-IT");
        this.put("polski", "pl-PL");
        this.put("svenska", "sv-SE");
    }};

    private static final Map<String, String> LANGUAGE_TO_URL = new HashMap<>() {{
        this.put("english", "english-united-states");
        this.put("русский", "russian-russia");
        this.put("suomi", "finnish-finland");
        this.put("deutsch", "german-germany");
        this.put("français", "french-france");
        this.put("español", "spanish-spain");
        this.put("português", "portuguese-portugal");
        this.put("italiano", "italian-italy");
        this.put("polski", "polish-poland");
        this.put("svenska", "swedish-sweden");
    }};

    @Override
    @Transactional
    public Map<String, byte[]> getSound(String language, String word) {
        Language lang = languageService.findLanguageByName(language).orElse(null);
        if (Objects.isNull(lang)) {
            throw new RuntimeException(String.format("Language by name '%s' not found.", language));
        }

        /* Проверить, нет ли озвучки для этого слова */
        if (!soundService.getVoicesPresent(lang, word)) {
//            WebDriver driver = new ChromeDriver(options);

            Map<String, byte[]> voicesByName = new HashMap<>();

            try {
                /* Открываем базу */
//                driver.get(BASE_URL);
//                /* Дожидаемся открытия странички */
//                new WebDriverWait(driver, WAIT_DURATION)
//                    .until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
//
//                /* Находим селектор языков */
//                By selectLanguageBy = By.id("languages");
//                Select languageSelect = new Select(driver.findElement(selectLanguageBy));
//
//                /* Находим селектор спикеров */
//                By speakersBy = By.id("voices");
//                Select speakersSelect = new Select(driver.findElement(speakersBy));
//
//                /* Находим текстовое поле */
//                By textAreaBy = By.xpath("//div[@class='col-12 sctioin2Bg']/textarea");
//                WebElement textArea = driver.findElement(textAreaBy);
//
//                /* Находим кнопку "Generate" */
//                By generateButtonBy = By.xpath("//div[@class='row-center-between mt-2']/button[@class='btn primary-btn bg-white mr-2']");
//                WebElement generateButton = driver.findElement(generateButtonBy);
//
//                /* Кнопка "Download" */
//                By downloadButtonBy = By.xpath("//button[@class='btn btn-xl primary-btn text-center mx-auto mt-5 p-3']");
//
//                /* Кнопка "Generate mare" */
//                By generateMoreLinkBy = By.xpath("//section/div[@class='container upgrade-section']/div/a");
//
//                File downloadFolder = new File(downloadFilePath);
//
//                /* Переключить язык на указанный */
//                languageSelect.selectByValue(LANGUAGE_TO_VALUE.get(lang.getName()));
//
//                /* Собрать голоса спикеров */
//                List<String> voices = speakersSelect.getOptions().stream()
//                    .map(element -> element.getAttribute("value"))
//                    .collect(Collectors.toList());
//
//
//                /* Очищаем поле ввода и вставляем слово */
//                textArea.clear();
//                textArea.sendKeys(word);
//
//                /* Проходим по всем голосам */
//                for (String voice: voices) {
//                    /* Очищаем папку голосов */
//                    FileHelper.emptyFolder(downloadFolder);
//
//                    /* Выбираем голос */
//                    speakersSelect.selectByValue(voice);
//                    /* Запускаем генерацию */
//                    generateButton.click();
//
//                    /* Ждём появления кнопки "Download" и жмём её */
//                    new WebDriverWait(driver, WAIT_DURATION)
//                        .until(ExpectedConditions.visibilityOfElementLocated(downloadButtonBy));
//                    WebElement downloadButton = driver.findElement(downloadButtonBy);
//                    downloadButton.click();
//
//                    /* Жмём ссылку "Generate more" */
//                    WebElement generateMoreLink = driver.findElement(generateMoreLinkBy);
//                    generateMoreLink.click();
//                    /* Голоса загружены */
//
//                    /* Забрать скачанный файл */
//                    voicesByName.put(voice, FileHelper.getFileBytes(FileHelper.getTheOnlyFile(downloadFolder)));
//                    log.info("Взята озвучка для '{}', голос: {}.", word, voice);
//                }
//
//                FileHelper.emptyFolder(downloadFolder);

                /* Записать забранное в БД */
//                soundService.saveVoices(language, word, voicesByName);



                return voicesByName;
            } catch (TimeoutException e) {
                log.error("Таймаут при получении звука.");
                throw e;
            } catch (Exception e) {
                log.error("Ошибка при получении звука: {}.", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return null;
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

        try {
            for (Entry<Language, List<String>> entry: filteredMap.entrySet()) {
                String addon = LANGUAGE_TO_URL.get(entry.getKey().getName());
                String locale = LANGUAGE_TO_VALUE.get(entry.getKey().getName());

                if (Objects.isNull(addon) || Objects.isNull(locale)) {
                    log.error("В списке не найден язык '{}'. Обратитесь к разработчику.", entry.getKey().getName());
                    break;
                }

                URL url = new URL(BASE_URL + addon);
                String regex = "\"(" + locale + "-.*?)\"";

                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                Pattern pattern = Pattern.compile(regex);
                List<String> speakers = new ArrayList<>();
                while ((inputLine = in.readLine()) != null) {
                    /* Нужно найти все включения, начинающиеся с 'fi-FI-' и заключённые в кавычки */
                    if (inputLine.trim().startsWith("voices") && inputLine.contains(locale + "-")) {
                        Matcher matcher = pattern.matcher(inputLine);
                        while (matcher.find()) {
                            speakers.add(matcher.group(1));
                        }
                    }
                }
                in.close();

                /* Нашли спикеров. Теперь надо отправить запрос (запросы) на получение звука */
                for (String word: entry.getValue()) {
                    Map<String, byte[]> voicesByName = new HashMap<>();

                    for (String speaker: speakers) {
                        MultipartBodyBuilder builder = new MultipartBodyBuilder();
                        builder.part("locale", locale);
                        builder.part("content", "<voice name=\"" + speaker + "\">" + word + "</voice>");
                        builder.part("ip", "185.165.241.171");  // Непонятно, что это за ip
                        MultiValueMap<String, HttpEntity<?>> body = builder.build();
                        WebClient webClient = WebClient.builder().baseUrl("https://textospeech.net").build();

                        ByteArrayResource got = webClient.post()
                            .uri("/app/restapi/create")
                            .bodyValue(body)
                            .retrieve()
                            .bodyToMono(ByteArrayResource.class)
                            .block();
                        byte[] byteArray = Objects.requireNonNull(got).getByteArray();
                        voicesByName.put(speaker, byteArray);
                    }
                    soundService.saveVoices(entry.getKey().getName(), word, voicesByName);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при получении озвучки", e);
        }
    }
}

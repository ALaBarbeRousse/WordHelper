package helper.api.service.web;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
/*
localhost:8081/api/voice?lang=suomi&word=maailma
*/
public class SpeechatorsVoiceService implements VoiceFetcher {
    private final String baseURL = "https://speechactors.com/text-to-speech/";

    String downloadFilePath = new File("voices").getAbsolutePath();

    private static final Map<String, String> LANGUAGE_TO_LINK = new HashMap<>() {{
        this.put("english", "english-united-states");
        this.put("русский", "russian-russia");
        this.put("suomi", "finnish-finland");
        this.put("deutsch", "german-germany");
        this.put("français", "french-france");
        this.put("español", "spanish-spain");
        this.put("italiano", "italian-italy");
    }};

    @Override
    public void getSound(String language, String word) {
        String url = baseURL + LANGUAGE_TO_LINK.get(language);

        ChromeOptions options = new ChromeOptions();
        Map<String, Object> preferences = new HashMap<>() {{
            this.put("profile.default_content_settings.popups", 0);
            this.put("download.default_directory", downloadFilePath);
            this.put("profile.default_content_setting_values.automatic_downloads", 1);
        }};
        options.setExperimentalOption("prefs", preferences);

        options.addArguments("headless");

        try {
            WebDriver driver = new ChromeDriver(options);
            driver.get(url);

            By inputTextBy = By.id("ms-input-text");
            WebElement textInput = driver.findElement(inputTextBy);
            textInput.sendKeys(word);

            WebDriverWait wait = new WebDriverWait(driver, Duration.of(10L, ChronoUnit.SECONDS));

            By voiceSelectBy = By.id("ms-voice-select");
            Select voiceSelect = new Select(driver.findElement(voiceSelectBy));
            List<WebElement> voiceSelectOptions = voiceSelect.getOptions();

            By saveButtonBy = By.id("save-button");
            WebElement saveButton = driver.findElement(saveButtonBy);

            for (WebElement option: voiceSelectOptions) {
                log.info("Голос: {}", option.getAttribute("value"));
                voiceSelect.selectByValue(option.getAttribute("value"));
                saveButton.click();

                By downloadButtonBy = By.id("download-button");
                wait.until(ExpectedConditions.elementToBeClickable(downloadButtonBy));
                WebElement downloadButton = driver.findElement(downloadButtonBy);
                downloadButton.click();

                By generateMoreBy = By.linkText("Generate More");
                wait.until(ExpectedConditions.presenceOfElementLocated(generateMoreBy));
                driver.findElement(generateMoreBy).click();

                wait.until(ExpectedConditions.presenceOfElementLocated(voiceSelectBy));
            }

            driver.close();
        } catch (Exception e) {
            log.error("Ошибка при получении звука: {}.", e.getMessage(), e);
        }
    }
}

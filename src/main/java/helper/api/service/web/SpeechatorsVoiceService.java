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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
/*
localhost:8081/api/voice?lang=suomi&word=maailma
*/
public class SpeechatorsVoiceService implements VoiceFetcher {
    private final String baseURL = "https://speechactors.com/text-to-speech/";

    @Override
    public void getSound(String language, String word) {
        Map<String, String> languageToLinkMap = new HashMap<>() {{
            this.put("english", "english-united-states");
            this.put("русский", "russian-russia");
            this.put("suomi", "finnish-finland");
            this.put("deutsch", "german-germany");
            this.put("française", "french-france");
            this.put("español", "spanish-spain");
            this.put("italiano", "italian-italy");
        }};

        String url = baseURL + languageToLinkMap.get(language);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");

        WebDriver driver = new ChromeDriver(options);
        driver.get(url);

        By inputTextBy = By.id("ms-input-text");
        WebElement textInput = driver.findElement(inputTextBy);
//        log.info("Найден элемент ввода текста: '{}'.", textInput.getTagName());
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
    }
}

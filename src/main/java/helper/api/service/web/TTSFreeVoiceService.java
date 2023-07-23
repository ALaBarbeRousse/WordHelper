package helper.api.service.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class TTSFreeVoiceService {
/*
localhost:8081/api/voice?lang=suomi&word=maailma
*/
    public void getVoice(String language, String word) {
        String url = "https://ttsfree.com/";

        try {
            HashMap<String, Object> prefs = new HashMap<>();
            prefs.put("profile.managed_default_content_settings.images", 2);

            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("prefs", prefs);

//            DesiredCapabilities chromeCaps = DesiredCapabilities.chrome();
//            chromeCaps.setCapability(ChromeOptions.CAPABILITY, options);


            WebDriver driver = new ChromeDriver();

            driver.manage().timeouts().implicitlyWait(Duration.of(5L, ChronoUnit.SECONDS));

            driver.get(url);

            By inputTextBy = By.id("input_text");
            By languageSelectBy = By.id("select_lang_bin");

//            WebDriverWait wait = new WebDriverWait(driver, Duration.of(1L, ChronoUnit.SECONDS));
//            wait.until(ExpectedConditions.visibilityOfElementLocated(inputTextBy));
            log.info("Ожидание завершено");

            WebElement languageSelect = driver.findElement(languageSelectBy);
            log.info("Найден элемент переключателя {}.", languageSelect.getTagName());
            WebElement textInput = driver.findElement(inputTextBy);
            log.info("Найден элемент ввода текста {}.", textInput.getTagName());



            driver.close();

            return;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}

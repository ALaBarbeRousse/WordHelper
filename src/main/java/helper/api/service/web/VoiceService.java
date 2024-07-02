package helper.api.service.web;

import helper.api.service.LanguageService;
import helper.api.service.SoundService;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class VoiceService implements VoiceFetcher {
    @Autowired
    SoundService soundService;

    @Autowired
    LanguageService languageService;

    String downloadFilePath = new File("voices").getAbsolutePath();

    protected final ChromeOptions options = new ChromeOptions() {{
        this.setPageLoadStrategy(PageLoadStrategy.EAGER);
//        this.addArguments("headless");
//        this.addArguments("--mute-audio");  // Глушим звуки
        this.setExperimentalOption("prefs", new HashMap<>() {{
            this.put("profile.default_content_settings.popups", 0);
            this.put("download.default_directory", downloadFilePath);
            this.put("profile.default_content_setting_values.automatic_downloads", 1);
        }});
    }};
}
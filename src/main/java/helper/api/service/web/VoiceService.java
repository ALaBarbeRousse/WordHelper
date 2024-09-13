package helper.api.service.web;

import helper.api.service.LanguageService;
import helper.api.service.SoundService;
import helper.model.Language;
import helper.model.dto.SoundingRequestDTO;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class VoiceService implements VoiceFetcher {
    @Autowired
    SoundService soundService;

    @Autowired
    LanguageService languageService;

    String downloadFilePath = new File("voices").getAbsolutePath();

    protected final ChromeOptions options = new ChromeOptions() {{
        this.setPageLoadStrategy(PageLoadStrategy.EAGER);
        this.addArguments("headless");
        this.addArguments("--mute-audio");  // Глушим звуки
        this.setExperimentalOption("prefs", new HashMap<>() {{
            this.put("profile.default_content_settings.popups", 0);
            this.put("download.default_directory", downloadFilePath);
            this.put("profile.default_content_setting_values.automatic_downloads", 1);
        }});
    }};

    protected Map<String, List<String>> arrangeData(List<SoundingRequestDTO> dtos) {
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
        return map;
    }

    protected Map<Language, List<String>> filterData(Map<String, List<String>> data) {
        Map<Language, List<String>> filteredMap = new HashMap<>();
        for (String lang: data.keySet()) {
            List<String> words = data.get(lang);
            Language language = languageService.findLanguageByName(lang).orElse(null);
            List<String> filtered = words.stream()
                .filter(word -> !soundService.getVoicesPresent(language, word))
                .collect(Collectors.toList());
            filteredMap.put(language, filtered);
        }
        return filteredMap;
    }
}
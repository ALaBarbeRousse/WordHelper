package helper.api.rest;

import helper.api.service.SettingsService;
import helper.model.dto.setting.BackgroundSoundingSettingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@Slf4j
@RequiredArgsConstructor
public class SettingsController {
    private final SettingsService settingService;

    @PostMapping(value = "/background/sounding")
    public void handleBackgroundSoundingSetting(@RequestBody BackgroundSoundingSettingDTO setting) {
        settingService.setSetting("background.sounding", setting);
    }

    @GetMapping(value = "/background/sounding")
    public String getBackgroundSoundingSetting() {
        return settingService.getSettings("background.sounding");
    }
}

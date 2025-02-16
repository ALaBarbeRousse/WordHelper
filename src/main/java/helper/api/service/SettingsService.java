package helper.api.service;

import helper.config.application.ApplicationSettings;
import helper.model.dto.setting.BackgroundSoundingSettingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SettingsService {
    private final ApplicationSettings applicationSettings;

    /**
     * Получаем настройки приложения по ключу
     * @param key ключ
     */
    public String getSettings(String key) {
        return applicationSettings.getSetting(key);
    }

    public void setSetting(String key, BackgroundSoundingSettingDTO setting) {
        applicationSettings.setSetting(key, setting);
    }
}

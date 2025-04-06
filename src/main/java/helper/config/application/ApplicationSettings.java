package helper.config.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import helper.api.jpa.SettingsRepository;
import helper.api.service.TranslationService;
import helper.api.service.web.VoiceService;
import helper.scheduled.GetSoundBackgroundTask;
import helper.model.application.Setting;
import helper.model.dto.setting.BackgroundSoundingSettingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Хранилище для настроек приложения
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationSettings {
    private Map<String, String> settings;

    private final SettingsRepository settingsRepository;

    private final TranslationService translationService;
    private final VoiceService voiceService;

    private Timer soundingTimer = new Timer();

    @PostConstruct
    private void loadConfig() {
        log.debug("Это loadConfig");

        settings = settingsRepository.findAll().stream().collect(toMap(Setting::getKey, Setting::getContents));

        /* Если в настройках стоит, что нужно загружать в фоне озвучку, делаем задачу */
        try {
            BackgroundSoundingSettingDTO backgroundSoundingSetting = new ObjectMapper().readValue(settings.get("background.sounding"), BackgroundSoundingSettingDTO.class);
            if (backgroundSoundingSetting.isEnabled()) {
                soundingTimer.scheduleAtFixedRate(
                    new GetSoundBackgroundTask(translationService, voiceService),
                    0,
                    backgroundSoundingSetting.getInterval()
                );
            }
        } catch (Exception e) {
            log.warn("Не удалось прочитать значение 'background.sounding' из конфига. Задача фоновой загрузки озвучки не будет запущена.");
        }
    }

    public String getSetting(String key) {
        return settings.get(key);
    }

    @Transactional
    public void setSetting(String key, BackgroundSoundingSettingDTO setting) {
        settings.put(key, setting.toString());
        /* Сохранить в репо */
        Setting toSave = new Setting().setKey(key).setContents(setting.toString());
        settingsRepository.saveAndFlush(toSave);

        /* Применить новые настройки */
        switch (key) {
            case "background.sounding": {   // Интервал фоновой загрузки озвучки
                soundingTimer.cancel();
                soundingTimer.purge();
                soundingTimer = new Timer();
                if (setting.isEnabled()) {
                    soundingTimer.scheduleAtFixedRate(
                        new GetSoundBackgroundTask(translationService, voiceService),
                        0,
                        setting.getInterval()
                    );
                }
                break;
            }
            default: {
                break;
            }
        }
    }
}

package helper.api.jpa;

import helper.model.application.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Setting, String>{
    Setting findSettingByKey(String key);
}

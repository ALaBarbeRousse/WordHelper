package helper.model.dto.setting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BackgroundSoundingSettingDTO {
    private boolean enabled;
    private int value;
    private Units unit;

    @Getter
    private enum Units {
        s,
        m,
        h,
        d
    }

    @Override
    public String toString() {
        return "{\"enabled\": " + enabled + ", \"value\": " + value + ", \"unit\": \"" + unit + "\"}";
    }

    /* Вычисляем интервал в миллисекундах */
    public long getInterval() {
        long multiplier = 0;
        switch (unit) {
            case s: {
                multiplier = 1000;
                break;
            }
            case m: {
                multiplier = 60*1000;
                break;
            }
            case h: {
                multiplier = 60*60*1000;
                break;
            }
            case d: {
                multiplier = 24*60*60*1000;
                break;
            }
        }

        return multiplier*value;
    }
}

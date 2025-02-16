package helper.scheduled;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.TimerTask;

@Slf4j
public class GetSoundBackgroundTask extends TimerTask {
    @Override
    public void run() {
        /* todo */
        log.info("Это выполнение GetSoundBackgroundTask: {}", new Date());
    }
}

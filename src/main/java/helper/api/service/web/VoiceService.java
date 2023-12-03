package helper.api.service.web;

import helper.api.service.SoundService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class VoiceService implements VoiceFetcher {
    @Autowired
    SoundService soundService;
}
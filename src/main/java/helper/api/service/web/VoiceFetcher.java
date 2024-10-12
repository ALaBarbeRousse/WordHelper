package helper.api.service.web;

import helper.model.dto.SoundingRequestDTO;

import java.io.IOException;
import java.util.List;

public interface VoiceFetcher {
    void getSound(String language, String word);

    void fetchSounds(List<SoundingRequestDTO> dtos) throws IOException;
}

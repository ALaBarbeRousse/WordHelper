package helper.api.service.web;

import helper.model.dto.SoundingRequestDTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface VoiceFetcher {
    Map<String, byte[]> getSound(String language, String word);

    void fetchSounds(List<SoundingRequestDTO> dtos) throws IOException;
}

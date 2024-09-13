//package helper.api.service.web;
//
//import helper.api.service.web.provider.FetchSoundsArgumentsSource;
//import helper.model.dto.SoundingRequestDTO;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ArgumentsSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//@SpringBootTest
//public class VoicemakerVoiceServiceTest {
//    @Autowired
//    private VoicemakerVoiceService voiceService;
//
//    @ParameterizedTest
//    @ArgumentsSource(FetchSoundsArgumentsSource.class)
//    void fetchSounds(List<SoundingRequestDTO> dtos) {
//        for (int i = 0; i < 1; i++) {
//            voiceService.fetchSounds(dtos);
//        }
//    }
//}

package helper.api.service.web.provider;

import helper.model.dto.SoundingRequestDTO;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Arrays;
import java.util.stream.Stream;

public class FetchSoundsArgumentsSource implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
            Arguments.of(
                Arrays.asList(
//                    new SoundingRequestDTO("english", "apple"),
//                    new SoundingRequestDTO("suomi", "aakkoset"),
//                    new SoundingRequestDTO("suomi", "postipankki"),
                    new SoundingRequestDTO("suomi", "voittaa")
                )
            )
        );
    }
}

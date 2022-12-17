package helper.api.rest;

import helper.api.service.TrainingService;
import helper.model.dto.CheckRequestDTO;
import helper.model.dto.TrainingWordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/training")
@RequiredArgsConstructor
public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping
    public TrainingWordDTO checkWordResult(@RequestBody CheckRequestDTO dto) {
        if (dto.getResult() != null) {
            trainingService.saveTrainingResults(dto);
        }
        return trainingService.getTrainingWord(dto.getLang1(), dto.getLang2());
    }
}

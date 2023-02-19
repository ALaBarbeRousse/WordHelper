package helper.api.rest;

import helper.api.service.TrainingService;
import helper.model.dto.GetTrainingDTO;
import helper.model.dto.TrainingDTO;
import helper.model.dto.TrainingResultDTO;
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
    public TrainingDTO getWordTraining(@RequestBody GetTrainingDTO dto) {
        return trainingService.getWordTraining(dto.getLang1(), dto.getLang2());
    }

    @PostMapping("/result")
    public void saveTrainingResults(@RequestBody TrainingResultDTO dtos) {
        trainingService.saveTrainingResults(dtos);
    }
}

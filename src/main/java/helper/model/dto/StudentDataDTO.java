package helper.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Передача информации о существующих студентах и о возможных ролях для редактирования списка ролей студента
 */
@Getter
@Setter
@AllArgsConstructor
public class StudentDataDTO {
    private List<StudentAuthorities> students;
    protected List<String> roles;
}

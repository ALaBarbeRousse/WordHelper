package helper.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String name;
    private String login;
    private Date created;
    private Date modified;
}

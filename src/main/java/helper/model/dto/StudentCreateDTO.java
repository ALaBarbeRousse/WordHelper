package helper.model.dto;

import lombok.Data;

@Data
public class StudentCreateDTO {
    private String name;
    private String login;
    private String password;
}

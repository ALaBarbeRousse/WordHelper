package helper.api.rest;

import helper.api.service.StudentService;
import helper.model.Student;
import helper.model.dto.StudentCreateDTO;
import helper.model.dto.StudentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public List<StudentDTO> getStudents() {
        return studentService.getStudents().stream().map(Student::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{login}")
    public StudentDTO findStudentByLogin(@PathVariable String login) {
        return studentService.findByLogin(login).toDTO();
    }

    /**
     * Register a new student
     * @param dto passed name, login and password
     * @return created student entity
     */
    @PostMapping
    public StudentDTO createStudent(@RequestBody StudentCreateDTO dto) {
        return studentService.createStudent(dto).toDTO();
    }
}

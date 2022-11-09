package helper.api.service;

import helper.api.jpa.StudentRepository;
import helper.model.Student;
import helper.model.dto.StudentCreateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

/**
 * Handling students (CRUD)
 * todo Сделать удаление и изменение
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {
    private final StudentRepository studentRepository;
    private final PasswordEncoder encoder;

    /**
     * New student registration
     * @param dto name, login and password
     * @return Created student
     */
    public Student createStudent(StudentCreateDTO dto) {
        validateUser(dto);

        /* Check whether user exists */
        if (studentRepository.findByLogin(dto.getLogin()).isPresent()) {
            throw new IllegalArgumentException(format("Student '%s' already exists.", dto.getLogin()));
        }

        try {
            Student student = new Student(dto.getName(), dto.getLogin());
            student.setPassword(encoder.encode(dto.getPassword()));
            Date now = new Date(System.currentTimeMillis());
            student.setCreated(now);
            student.setModified(now);
            return studentRepository.save(student);
        } catch (Exception e) {
            log.error("Something went wrong during student registration attempt", e);
            return null;
        }
    }

    /**
     * Retrieve all existing students
     * @return List of found students
     */
    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public Student findByLogin(String login) {
        return studentRepository.findByLogin(login).orElseGet(() -> {
            log.warn(format("Couldn't find a student by passed login '%s'", login));
            return null;
        });
    }

    /**
     * Verify the integrity of passed data
     * @param dto student creation parameters
     */
    private void validateUser(StudentCreateDTO dto) {
        if (Objects.isNull(dto.getLogin()) || dto.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Login field should be specified.");
        }
        if (Objects.isNull(dto.getName()) || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name field should be specified.");
        }
        if (Objects.isNull(dto.getPassword()) || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password field should be specified.");
        }
    }
}

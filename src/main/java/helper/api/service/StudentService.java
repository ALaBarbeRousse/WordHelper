package helper.api.service;

import helper.api.jpa.LanguageRepository;
import helper.api.jpa.StudentRepository;
import helper.model.Language;
import helper.model.Role;
import helper.model.Roles;
import helper.model.Student;
import helper.model.dto.LanguageCreateDTO;
import helper.model.dto.StudentCreateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
    private final LanguageService languageService;
    private final PasswordEncoder encoder;

    private final RoleService roleService;

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

        /* Check whether selected language exists. If not, then create */
        Language language = languageService.findLanguageByName(dto.getLanguage())
                .orElseGet(() -> languageService.createLanguage(new LanguageCreateDTO(dto.getLanguage())));

        try {
            Student student = new Student(dto.getName(), dto.getLogin());
            student.setPassword(encoder.encode(dto.getPassword()));
            Date now = new Date(System.currentTimeMillis());
            student.setCreated(now);
            student.setModified(now);
            student.setLanguage(language);

            /* Apply the only one role (Student) */
            Role studentRole = roleService.findRoleByName(Roles.ROLE_STUDENT.getRole().getAuthority());
            student.setRoles(Set.of(studentRole));
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
            throw new IllegalArgumentException("Login field should be filled in.");
        }
        if (Objects.isNull(dto.getName()) || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name field should be filled in.");
        }
        if (Objects.isNull(dto.getPassword()) || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password field should be filled in.");
        }
        if (Objects.isNull(dto.getLanguage()) || dto.getLanguage().trim().isEmpty()) {
            throw new IllegalArgumentException("Language field should be filled in.");
        }
    }

    /* Setting attributes for personal page */
    public void setPersonalAttributes(Model model) {
        model.addAttribute("student_name", getStudentName());
        model.addAttribute("my_language", getStudentLanguage());
    }

    private String getStudentLanguage() {
        Object pr = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (pr instanceof Student) {
            return Optional.ofNullable(((Student) pr).getLanguage()).map(Language::getName).orElse(null);
        } else {
            return null;
        }
    }

    public String getStudentName() {
        Object pr = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (pr instanceof Student) {
            return ((Student) pr).getName();
        } else {
            return null;
        }
    }
}

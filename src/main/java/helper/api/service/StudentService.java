package helper.api.service;

import helper.api.jpa.StudentRepository;
import helper.model.Language;
import helper.model.LanguageChoice;
import helper.model.Role;
import helper.model.Roles;
import helper.model.Student;
import helper.model.dto.LanguageChoiceDTO;
import helper.model.dto.LanguageCreateDTO;
import helper.model.dto.StudentChangeDataDTO;
import helper.model.dto.StudentCreateDTO;
import helper.model.dto.StudentAuthorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final LanguageChoiceService languageChoiceService;
    private final RoleService roleService;

    @PostConstruct
    private void init() {
        createAdminUser();
    }

    private Student createAdminUser() {
        return studentRepository.findByLogin("admin")
            .orElseGet(() -> {
                Student user = createStudent(
                        new StudentCreateDTO("Администратор", "admin", "admin", "русский"));
                Role adminRole = roleService.findRoleByName(Roles.ROLE_ADMIN.getRole().getAuthority());
                user.setRoles(Set.of(adminRole));
                return studentRepository.save(user);
            });
    }

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
        model.addAttribute("language_choice", getLanguageChoice());
    }

    /* Надо вернуть пару языков */
    private LanguageChoiceDTO getLanguageChoice() {
        Object pr = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (pr instanceof Student) {
            return languageChoiceService.findLanguageChoice((Student) pr)
                    .map(LanguageChoice::toDTO)
                    .orElse(null);
        } else {
            return null;
        }
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

    /**
     * Student's name and authorities;
     * @return List of all found students each with his authorities list
     */
    public List<StudentAuthorities> getStudentAuthorities() {
        return studentRepository.findAll().stream()
            .map(student -> new StudentAuthorities(
                    student.getName(),
                    student.getRoles().stream()
                        .map(Role::getAuthority)
                        .collect(Collectors.toList())
                )).collect(Collectors.toList());
    }

    /**
     * Update student's roles.
     * @param dto - Student's name and authorities info
     */
    public void updateStudentData(StudentChangeDataDTO dto) {
        Student user = studentRepository.findByName(dto.getName())
                .orElseThrow(() -> new IllegalArgumentException(format("Student '%s' has not been found", dto.getName())));

        /* Prohibition to remove Admin authority */
        if (isCurrentUser(user) && !dto.getAuthorities().contains("Admin")) {
            throw new IllegalArgumentException(format("Couldn't remove Admin authority from current user '%s'.", dto.getName()));
        }

        Set<Role> foundRoles = roleService.findRolesByAuthorities(dto.getAuthorities());
        user.setRoles(foundRoles);
        studentRepository.save(user);

        /* Renew authentication if needed */
        if (isCurrentUser(user)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), user.getAuthorities())
            );
        }
    }

    private boolean isCurrentUser(Student found) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal != null && principal.equals(found);
    }
}

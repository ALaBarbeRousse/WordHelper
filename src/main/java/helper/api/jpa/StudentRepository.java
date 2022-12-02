package helper.api.jpa;

import helper.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByLogin(String login);
    Optional<Student> findByName(String name);
}

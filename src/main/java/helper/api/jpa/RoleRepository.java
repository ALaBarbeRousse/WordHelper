package helper.api.jpa;

import helper.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByAuthority(String authority);
    Set<Role> getRolesByAuthorityIn(List<String> authorities);
}

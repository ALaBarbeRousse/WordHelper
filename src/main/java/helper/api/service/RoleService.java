package helper.api.service;

import helper.api.jpa.RoleRepository;
import helper.model.Role;
import helper.model.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    /* Check whether three main roles exist */
    @PostConstruct
    private void init() {
        List<Role> r = roleRepository.findAll();
        for (Roles role: Roles.values()) {
            Role foundRole = roleRepository.findByAuthority(role.getRole().getAuthority())
                    .orElseGet(() -> roleRepository.save(role.getRole()));
        }
    }

    public Role findRoleByName(String authority) {
        return roleRepository.findByAuthority(authority)
                .orElseGet(() -> roleRepository.save(new Role(authority, "")));
    }


    public Set<Role> findRolesByAuthorities(List<String> authorities) {
        return roleRepository.getRolesByAuthorityIn(authorities);
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public List<String> getRoleAuthorities() {
        return roleRepository.findAll().stream().map(Role::getAuthority).collect(Collectors.toList());
    }
}

package helper.model;

import helper.model.dto.StudentDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "students")
public class Student implements UserDetails {
    /**
     * Constructor by three fields
     * @param name Student's shown name
     * @param login Student's username
     */
    public Student(String name, String login) {
        this.name = name;
        this.login = login;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint")
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(128)")
    private String name;

    @Column(name = "login", nullable = false, unique = true, columnDefinition = "VARCHAR(32)")
    private String login;

    @Column(name = "password", nullable = false, unique = true, columnDefinition = "VARCHAR(128)")
    private String password;

    @Column(name = "created", nullable = false)
    private Date created;

    @Column(name = "modified", nullable = false)
    private Date modified;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public StudentDTO toDTO() {
        return new StudentDTO(id, name, login, created, modified, language);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

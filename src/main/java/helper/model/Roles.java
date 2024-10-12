package helper.model;

import lombok.Getter;

@Getter
public enum Roles {
    ROLE_STUDENT(new Role("Student", "Main role, for reading dictionaries and create and use collections.")),
    ROLE_EDITOR(new Role("Editor", "Editing dictionaries")),
    ROLE_ADMIN(new Role("Admin", "Handling users"));

    private final Role role;

    Roles(Role role) {
        this.role = role;
    }
}

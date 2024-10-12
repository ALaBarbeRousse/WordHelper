package helper.model;


import helper.model.dto.LanguageDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "languages")
@Setter
@Getter
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint")
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(128)")
    private String name;

    public LanguageDTO toDTO() {
        return new LanguageDTO(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Language && ((Language)obj).name.equals(name);
    }

    @Override
    public String toString() {
        return "Language: " + name;
    }
}

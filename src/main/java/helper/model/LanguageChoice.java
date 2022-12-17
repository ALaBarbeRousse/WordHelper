package helper.model;

import helper.model.dto.LanguageChoiceDTO;
import helper.model.dto.LanguageDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "language_choice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LanguageChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "lang1")
    private Language lang1;

    @ManyToOne
    @JoinColumn(name = "lang2")
    private Language lang2;

    public LanguageChoice(Student student, Language lang1, Language lang2) {
        this.student = student;
        this.lang1 = lang1;
        this.lang2 = lang2;
    }

    public List<LanguageDTO> getLanguages() {
        return List.of(lang1.toDTO(), lang2.toDTO());
    }

    public boolean equalsLanguages(String lang1, String lang2) {
        return this.lang1.getName().equals(lang1) && this.lang2.getName().equals(lang2);
    }

    public LanguageChoiceDTO toDTO() {
        return new LanguageChoiceDTO(lang1.getName(), lang2.getName());
    }
}

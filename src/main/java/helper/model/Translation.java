package helper.model;

import helper.model.dto.TranslationDTO;
import lombok.AccessLevel;
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

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "translation")
public class    Translation {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "word_language")
    private Language wordLanguage;

    @ManyToOne
    @JoinColumn(name = "word")
    private Word word;

    @ManyToOne
    @JoinColumn(name = "translation_language")
    private Language translationLanguage;

    @ManyToOne
    @JoinColumn(name = "translation")
    private Word translation;

    public Translation(Language wordLanguage, Word word, Language translationLanguage, Word translation) {
        this.wordLanguage = wordLanguage;
        this.word = word;
        this.translationLanguage = translationLanguage;
        this.translation = translation;
    }

    public TranslationDTO toDTO() {
        return new TranslationDTO(wordLanguage.getName(), word.getWriting(), translationLanguage.getName(), translation.getWriting());
    }
}

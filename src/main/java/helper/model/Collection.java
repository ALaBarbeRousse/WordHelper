package helper.model;

import helper.model.dto.TranslationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
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
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Table(name = "collection")
@NoArgsConstructor
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner")
    private Student owner;

    @ManyToOne
    @JoinColumn(name = "lang1")
    private Language lang1;

    @ManyToOne
    @JoinColumn(name = "lang2")
    private Language lang2;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(name = "collection_translation",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "translation_id")
    )
    private List<Translation> translations;

    public Collection(String name, Student owner, Language lang1, Language lang2, List<Translation> translations) {
        this.name = name;
        this.owner = owner;
        this.lang1 = lang1;
        this.lang2 = lang2;
        this.translations = translations;
    }

    public List<TranslationDTO> getContentDTO() {
        return translations.stream()
                .map(translation -> new TranslationDTO(
                        translation.getWordLanguage().getName(),
                        translation.getWord().getWriting(),
                        translation.getTranslationLanguage().getName(),
                        translation.getTranslation().getWriting()
                )).collect(Collectors.toList());
    }
}

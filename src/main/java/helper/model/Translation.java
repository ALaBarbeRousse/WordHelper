package helper.model;

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
public class Translation {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "language1")
    private Language language1;

    @ManyToOne
    @JoinColumn(name = "word1")
    private Word word1;

    @ManyToOne
    @JoinColumn(name = "language2")
    private Language language2;

    @ManyToOne
    @JoinColumn(name = "word2")
    private Word word2;

    public Translation(Language language1, Word word1, Language language2, Word word2) {
        this.language1 = language1;
        this.word1 = word1;
        this.language2 = language2;
        this.word2 = word2;
    }
}

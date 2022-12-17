package helper.model;

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
@Table(name = "training")
@NoArgsConstructor
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "lang1")
    private Language language1;

    @ManyToOne
    @JoinColumn(name = "lang2")
    private Language language2;

    public Training(Student student, Language language1, Language language2) {
        this.student = student;
        this.language1 = language1;
        this.language2 = language2;
    }

    /* todo Сделать подборки */
//    @ManyToOne
//    @JoinColumn(name = "collection")
//    private Collection collection;
}

package helper.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "training")
@NoArgsConstructor
public class Training {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid2", strategy = "uuid4")
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "physicalid", columnDefinition = "VARCHAR(36)")
    private UUID physicalId;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "lang1")
    private Language language1;

    @ManyToOne
    @JoinColumn(name = "lang2")
    private Language language2;

    @ManyToOne
    @JoinColumn(name = "collection")
    private Collection collection;

    public Training(Student student, Language language1, Language language2) {
        this.student = student;
        this.language1 = language1;
        this.language2 = language2;
    }
}

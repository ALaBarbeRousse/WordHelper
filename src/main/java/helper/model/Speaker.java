package helper.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "speakers")
@NoArgsConstructor
public class Speaker {
    public Speaker(Language language, String name) {
        this.language = language;
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint")
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(32)")
    private String name;

    @ManyToOne
    @JoinColumn(name = "language", columnDefinition = "bigint")
    private Language language;
}

package helper.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "voices",
    uniqueConstraints = @UniqueConstraint(columnNames = {"word", "speaker"}))
public class Voice {
    public Voice(Word word, Speaker speaker, byte[] sound) {
        this.word = word;
        this.speaker = speaker;
        this.sound = sound;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "word", columnDefinition = "bigint")
    private Word word;

    @ManyToOne
    @JoinColumn(name = "speaker")
    private Speaker speaker;

    @Column(name="sound")
    private byte[] sound;
}

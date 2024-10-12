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
import javax.persistence.UniqueConstraint;

@Entity
@Getter
@Setter
@Table(name = "training_results", uniqueConstraints = @UniqueConstraint(columnNames = {"training", "translation"}))
@NoArgsConstructor
public class TrainingResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "training")
    private Training training;

    @ManyToOne
    @JoinColumn(name = "translation")
    private Translation translation;

    @Column(name = "weight")
    private float weight;

    public TrainingResult(Training training, Translation translation, Float weight) {
        this.training = training;
        this.translation = translation;
        this.weight = weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TrainingResult) {
            TrainingResult another = (TrainingResult) obj;
            return this.training.equals(another.training) && this.translation.equals(another.translation);
        } else return false;
    }
}

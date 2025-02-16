package helper.model.application;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "settings")
@Getter
@Setter
@Accessors(chain = true)
public class Setting {
    @Id
    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "contents", nullable = false, length = 2048)
    private String contents;
}

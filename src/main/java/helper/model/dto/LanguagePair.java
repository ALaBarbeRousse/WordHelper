package helper.model.dto;

import helper.model.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LanguagePair {
    private Language l1, l2;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LanguagePair) {
            LanguagePair lp2 = (LanguagePair) obj;
            return (this.l1.equals(lp2.l1) && this.l2.equals(lp2.l2)) || (this.l1.equals(lp2.l2) && this.l2.equals(lp2.l1));
        }
        return false;
    }
}

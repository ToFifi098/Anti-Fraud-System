package antifraud.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@Entity
@Getter @Setter
@NoArgsConstructor
public class Max {
    @Id
    private Long id;
    private Long max;

    public Max(Long id, Long max) {
        this.id = id;
        this.max = max;
    }
}

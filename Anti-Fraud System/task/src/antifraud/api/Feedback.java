package antifraud.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class Feedback {
    private Long transactionId;
    private Transaction.status feedback;

    public Feedback(Long transactionId, Transaction.status feedback) {
        this.transactionId = transactionId;
        this.feedback = feedback;
    }
}

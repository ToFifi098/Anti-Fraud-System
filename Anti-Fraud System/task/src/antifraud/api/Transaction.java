package antifraud.api;

import antifraud.api.antifraud.IpService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {
    public enum status{
        ALLOWED,
        MANUAL_PROCESSING,
        PROHIBITED
    }
    public enum region{
        EAP,
        ECA,
        HIC,
        LAC,
        MENA,
        SA,
        SSA
    }

    @JsonProperty("transactionId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("amount")
    private long amount;
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("number")
    private String number;
    @JsonProperty("region")
    private region region;
    @JsonProperty("date")
    private LocalDateTime date;
    @JsonProperty("result")
    private status status;
    @JsonProperty("feedback")
    private status feedback;

    @JsonProperty("feedback")
    public String feedbackToString(){
        if(feedback == null) return "";
        return feedback.toString();
    }

    public status getFeedback(){
        return feedback;
    }

    public Transaction(long amount, String ip, String number, Transaction.region region, CharSequence sequence) {
        this.amount = amount;
        this.ip = ip;
        this.number = number;
        this.region = region;
        this.date = LocalDateTime.parse(sequence);
    }

}

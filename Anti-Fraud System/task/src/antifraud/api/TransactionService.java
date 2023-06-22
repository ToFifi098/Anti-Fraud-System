package antifraud.api;

import antifraud.api.antifraud.Card;
import antifraud.api.antifraud.CardService;
import antifraud.api.antifraud.Ip;
import antifraud.api.antifraud.IpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionService {

    @Autowired
    IpService ipService;
    @Autowired
    CardService cardService;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    MaxRepository maxRepository;

    private long maxAllowed;
    private long maxManual;

    @Bean
    private void setMax(){
        if(maxRepository.findAll().size() == 0){
            maxAllowed = 200;
            maxManual = 1500;
        }
        else {
            maxAllowed = Objects.requireNonNull(maxRepository.findById(1L).orElse(null)).getMax();
            maxManual = Objects.requireNonNull(maxRepository.findById(2L).orElse(null)).getMax();
        }
    }
    public Transaction getTransactionById(Long id){
        return transactionRepository.findById(id).orElse(null);
    }

    public boolean updateLimits(Transaction transaction){
        Transaction.status feedback = transaction.getFeedback();
        Transaction.status validity = transaction.getStatus();

        System.out.println(transaction);
        if(feedback == validity){
            return false;
        }
        if(feedback == Transaction.status.ALLOWED){
            maxAllowed = increaseLimit(maxAllowed, transaction.getAmount());
            if(validity == Transaction.status.PROHIBITED)
                maxManual = increaseLimit(maxManual, transaction.getAmount());
        }
        if(feedback == Transaction.status.MANUAL_PROCESSING){
            if(validity == Transaction.status.ALLOWED)
                maxAllowed = decreaseLimit(maxAllowed, transaction.getAmount());
            if(validity == Transaction.status.PROHIBITED)
                maxManual = increaseLimit(maxManual, transaction.getAmount());
        }
        if (feedback == Transaction.status.PROHIBITED){
            maxManual = decreaseLimit(maxManual, transaction.getAmount());
            if(validity == Transaction.status.ALLOWED)
                maxAllowed = decreaseLimit(maxAllowed, transaction.getAmount());
        }
        maxRepository.save(new Max(1L, maxAllowed));
        maxRepository.save(new Max(2L, maxManual));

        return true;
    }

    private long increaseLimit(long limit, long transactionValue){
        return (long) Math.ceil(0.8 * limit + 0.2 * transactionValue);
    }
    private long decreaseLimit(long limit, long transactionValue){
        return (long) Math.ceil(0.8 * limit - 0.2 * transactionValue);
    }

    public Result checkTransaction(Transaction transaction){
        Result result = new Result();
        if(!ipService.isValid(new Ip(transaction.getIp())) ||
                !cardService.isValid(new Card(transaction.getNumber())) ||
                transaction.getAmount() <= 0){
            return null;
        }

        List<Transaction> transactions = transactionRepository.findTransactionByNumberAndDifRegion(transaction.getNumber(), transaction.getRegion(), Transaction.status.PROHIBITED);

        HashSet<String> regions = new HashSet<>();
        for (int i = 0; i < transactions.size(); i++){
            long millis = Duration.between(transactions.get(i).getDate(), transaction.getDate()).toMillis();
            if(millis >= 3600000){

                break;
            }
            if(regions.size() > 3){

                break;
            }

            regions.add(transactions.get(i).getRegion().toString());
        }

        transactions = transactionRepository.findTransactionByNumberAndDifIp(transaction.getNumber(), transaction.getIp(), Transaction.status.PROHIBITED);
        HashSet<String> ips = new HashSet<>();
        for (int i = 0; i < transactions.size(); i++){

            if(transactions.get(i).getDate().isAfter(transaction.getDate())){
                continue;
            }
            if(Duration.between(transactions.get(i).getDate(), transaction.getDate()).toMillis() >= 3600000){
                break;
            }
            if(ips.size() > 3){
                break;
            }
            ips.add(transactions.get(i).getIp());
        }



        if(transaction.getAmount() > 0) {
            if (transaction.getAmount() <= maxAllowed) {
                result.setResult(Transaction.status.ALLOWED);
            }
            else if (transaction.getAmount() <= maxManual) {
                result.setResult(Transaction.status.MANUAL_PROCESSING);
                result.addInfo("amount");
            }
            else {
                result.setResult(Transaction.status.PROHIBITED);
                result.addInfo("amount");
            }
        }

        if(ipService.findByIp(transaction.getIp()) != null){
            if(result.getResult() != Transaction.status.PROHIBITED)
                result.setInfo(new ArrayList<>());
            result.setResult(Transaction.status.PROHIBITED);
            result.addInfo("ip");
        }

        if(cardService.findByNumber(transaction.getNumber()) != null){
            if(result.getResult() != Transaction.status.PROHIBITED)
                result.setInfo(new ArrayList<>());
            result.setResult(Transaction.status.PROHIBITED);
            result.addInfo("card-number");
        }


        if (regions.size() == 2 && (result.getResult() != Transaction.status.PROHIBITED)) {
            result.setResult(Transaction.status.MANUAL_PROCESSING);

            result.addInfo("region-correlation");
        }
        if (ips.size() == 2 &&  (result.getResult() != Transaction.status.PROHIBITED)) {
            result.setResult(Transaction.status.MANUAL_PROCESSING);
            result.addInfo("ip-correlation");

        }
        if(regions.size() >= 3){
            if(result.getResult() != Transaction.status.PROHIBITED)
                result.setInfo(new ArrayList<>());
            result.setResult(Transaction.status.PROHIBITED);
            result.addInfo("region-correlation");
        }
        if(ips.size() >= 3){
            if(result.getResult() != Transaction.status.PROHIBITED)
                result.setInfo(new ArrayList<>());
            result.setResult(Transaction.status.PROHIBITED);
            result.addInfo("ip-correlation");
        }

        transaction.setStatus(result.getResult());
        transactionRepository.save(transaction);

        return result;
    }

    public Iterable<Transaction> getAllTransactions(){
        return transactionRepository.findAll();
    }

    public List<Transaction> getAllTransactionsByNumber(String number){
        List<Transaction> output = transactionRepository.findTransactionByNumber(number);
        if(output.size() == 0 || output == null){
            return null;
        }
        return output;
    }

    public void clear(){
        transactionRepository.deleteAll();
    }

    public Transaction add(Transaction transaction){
        return transactionRepository.save(transaction);
    }
}

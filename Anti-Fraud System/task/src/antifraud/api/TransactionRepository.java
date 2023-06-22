package antifraud.api;

import antifraud.api.antifraud.Ip;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    @Query("select t from Transaction t where  t.number = :number and t.region != :region and t.status != :status order by t.date DESC")
    List<Transaction> findTransactionByNumberAndDifRegion(String number, Transaction.region region, Transaction.status status);

    @Query("select t from Transaction t where  t.number = :number and t.ip != :ip and t.status != :status order by t.date DESC")
    List<Transaction> findTransactionByNumberAndDifIp(String number, String ip, Transaction.status status);

    @Query("select t from Transaction t where  t.number = :number")
    List<Transaction> findTransactionByNumber(String number);

}

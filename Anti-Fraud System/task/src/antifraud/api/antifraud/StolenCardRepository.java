package antifraud.api.antifraud;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StolenCardRepository extends JpaRepository<Card, Long> {
}

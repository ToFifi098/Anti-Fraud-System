package antifraud.api.antifraud;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IpRepository extends JpaRepository<Ip,Long> {
}

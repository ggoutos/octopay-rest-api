package eu.octopay.repository;

import eu.octopay.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("select a from Account a where a.id = ?1")
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Account> findById(String id);
}

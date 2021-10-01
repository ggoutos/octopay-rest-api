package eu.octopay.repository;

import eu.octopay.domain.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface OperationRepository extends JpaRepository<Operation, String> {

    Optional<Operation> findById(String id);
    Set<Operation> findAllByAccount_IdAndTimestampBetweenOrderByTimestampDesc(
            String accountId, LocalDateTime timestampFrom, LocalDateTime timestampTo);
}

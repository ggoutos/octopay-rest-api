package eu.octopay.repository;

import eu.octopay.domain.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface OperationRepository extends JpaRepository<Operation, String> {

    ArrayList<Operation> findAllByAccount_IdAndTimestampBetweenOrderByTimestampDesc(
            String accountId, LocalDateTime timestampFrom, LocalDateTime timestampTo);
}

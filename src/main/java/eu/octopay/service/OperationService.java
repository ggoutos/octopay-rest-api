package eu.octopay.service;

import eu.octopay.domain.Account;
import eu.octopay.domain.Operation;
import eu.octopay.domain.OperationType;
import eu.octopay.exception.DebitAmountException;
import eu.octopay.exception.EntityNotFoundException;
import eu.octopay.model.OperationRequest;
import eu.octopay.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;


@Slf4j
@Service
@RequiredArgsConstructor
public class OperationService {

    private final AccountService accountService;
    private final OperationRepository operationRepository;

    public ArrayList<Operation> findOperationsBetween(String accountId, LocalDate dateFrom, LocalDate dateTo) {
        LocalDateTime dateTimeFrom = (dateFrom != null)
                ? dateFrom.atTime(LocalTime.MIN)
                : LocalDateTime.parse("1970-01-01T00:00:01.000000");

        LocalDateTime dateTimeTo = (dateTo != null)
                ? dateTo.atTime(LocalTime.MAX)
                : LocalDateTime.now();

        return operationRepository.findAllByAccount_IdAndTimestampBetweenOrderByTimestampDesc(
                accountId, dateTimeFrom, dateTimeTo);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public Operation saveCreditDebit(OperationRequest request) {
        Account account = accountService.findById(request.getAccountId()).orElseThrow(EntityNotFoundException::new);

        if (request.getType().equals(OperationType.DEBIT)) {
            BigDecimal balance = accountService.getBalance(request.getAccountId());
            if (balance.compareTo(request.getAmount()) < 0) {
                throw new DebitAmountException();
            }
        }

        Operation operation = new Operation(account, request.getType(), request.getAmount(), request.getComment());
        return operationRepository.save(operation);
    }

}

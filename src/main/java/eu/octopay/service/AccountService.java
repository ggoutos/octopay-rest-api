package eu.octopay.service;

import eu.octopay.domain.Account;
import eu.octopay.domain.Operation;
import eu.octopay.domain.OperationType;
import eu.octopay.exception.DebitAmountException;
import eu.octopay.exception.EntityNotFoundException;
import eu.octopay.model.OperationRequest;
import eu.octopay.repository.AccountRepository;
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
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
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
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(EntityNotFoundException::new);

        BigDecimal balance = getBalance(request.getAccountId());

        if (request.getType().equals(OperationType.DEBIT)) {
            if (balance.compareTo(request.getAmount()) < 0) {
                log.error("\n\nOperation Failed\n" +
                        "account id:" + request.getAccountId() + ", balance: " + balance +
                        ", operation: " + request.getType().toString() + ", amount: " + request.getAmount() + "\n");

                throw new DebitAmountException();
            }
        }

        log.info("\n\nAttempting Operation\n" +
                "account id:" + request.getAccountId() + ", balance: " + balance +
                ", operation: " + request.getType().toString() + ", amount: " + request.getAmount() + "\n");

        Operation operation = new Operation(account, request.getType(), request.getAmount(), request.getComment());
        return operationRepository.save(operation);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<Account> findById(String id) {
        return accountRepository.findById(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public BigDecimal getBalance(String id) {
        return findOperationsBetween(id, null, null).stream()
                .map(operation -> operation.getType().equals(OperationType.CREDIT)
                        ? operation.getAmount()
                        : operation.getAmount().multiply(BigDecimal.valueOf(-1L)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Account save(Account account) {
        return accountRepository.save(account);
    }
}

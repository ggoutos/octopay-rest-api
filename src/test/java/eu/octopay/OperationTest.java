package eu.octopay;

import eu.octopay.domain.Account;
import eu.octopay.domain.Operation;
import eu.octopay.domain.OperationType;
import eu.octopay.model.OperationRequest;
import eu.octopay.service.AccountService;
import eu.octopay.service.OperationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
public class OperationTest {

    @SpyBean
    @Autowired
    private OperationService operationService;

    @SpyBean
    @Autowired
    private AccountService accountService;

    @Test
    @Transactional
    void concurrentOperations() throws InterruptedException {
        // given
        final Account account = accountService.save(new Account());
        log.info("account id:" + account.getId());

        OperationRequest credit = new OperationRequest(
                account.getId(),
                OperationType.CREDIT,
                LocalDateTime.now(),
                BigDecimal.valueOf(1000),
                "καταθεση test");

        // katathesi
        operationService.saveCreditDebit(credit);

        assertEquals(0, accountService.getBalance(account.getId()).compareTo(BigDecimal.valueOf(1000)));

        // analhpsh
        OperationRequest debit = new OperationRequest(
                account.getId(),
                OperationType.DEBIT,
                LocalDateTime.now(),
                BigDecimal.valueOf(1000),
                "αναληψη test");

        // analhpsh
        OperationRequest debit2 = new OperationRequest(
                account.getId(),
                OperationType.DEBIT,
                LocalDateTime.now(),
                BigDecimal.valueOf(1000),
                "αναληψη test");

        ArrayList<OperationRequest> operations = new ArrayList<>();
        operations.add(debit);
        operations.add(debit2);


        // when
        final ExecutorService executor = Executors.newFixedThreadPool(operations.size());

        for (final OperationRequest request : operations) {
            executor.execute(() -> {
                try {
                    operationService.saveCreditDebit(request);
                } catch (Exception e) {
                    log.info(request.getType().toString() + " " + request.getAmount());
                    log.info(e.getMessage());
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));

        // then
        final ArrayList<Operation> operationsSaved = operationService.findOperationsBetween(account.getId(), null, null);

        assertAll(
                () -> assertEquals(2, operationsSaved.size()),
                // order by timestamp desc lifo
                () -> assertEquals(0, operationsSaved.get(0).getAmount().compareTo(BigDecimal.valueOf(1000))),
                () -> assertEquals(0, operationsSaved.get(1).getAmount().compareTo(BigDecimal.valueOf(1000))),
                () -> assertEquals(0, accountService.getBalance(account.getId()).compareTo(BigDecimal.ZERO)),
                () -> verify(operationService, times(3)).saveCreditDebit(any())
        );
    }

}

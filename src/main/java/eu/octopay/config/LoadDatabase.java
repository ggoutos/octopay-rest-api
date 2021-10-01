package eu.octopay.config;

import eu.octopay.domain.Account;
import eu.octopay.domain.Operation;
import eu.octopay.domain.OperationType;
import eu.octopay.repository.AccountRepository;
import eu.octopay.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LoadDatabase {
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> initAccounts();
    }

    private void initAccounts() {
        if (accountRepository.findAll().isEmpty()) {
            Account account1 = new Account();
            Account account2 = new Account();

            accountRepository.save(account1);
            accountRepository.save(account2);

            Operation operation = new Operation(
                    account1,
                    OperationType.CREDIT,
                    BigDecimal.valueOf(1000),
                    "πίστωση");

            Operation operation2 = new Operation(
                    account1,
                    OperationType.DEBIT,
                    BigDecimal.valueOf(250),
                    "χρέωση");

            Operation operation3 = new Operation(
                    account2,
                    OperationType.CREDIT,
                    BigDecimal.valueOf(250),
                    "ακομα μια πίστωση");

            operationRepository.save(operation);
            operationRepository.save(operation2);
            operationRepository.save(operation3);
        }

        accountRepository.findAll().forEach(account -> log.info("Preloaded " + account));
        operationRepository.findAll().forEach(operation -> log.info("Preloaded " + operation));
    }

}

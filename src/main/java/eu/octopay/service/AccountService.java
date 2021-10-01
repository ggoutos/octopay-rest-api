package eu.octopay.service;

import eu.octopay.domain.Account;
import eu.octopay.domain.OperationType;
import eu.octopay.exception.EntityNotFoundException;
import eu.octopay.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(String id) {
        return accountRepository.findById(id);
    }

    //    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BigDecimal getBalance(String id) {
        return findById(id).orElseThrow(EntityNotFoundException::new)
                .getOperations()
                .stream()
                .map(operation -> operation.getType().equals(OperationType.CREDIT)
                        ? operation.getAmount()
                        : operation.getAmount().multiply(BigDecimal.valueOf(-1L)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

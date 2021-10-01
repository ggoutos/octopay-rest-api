package eu.octopay.service;

import eu.octopay.domain.Operation;
import eu.octopay.model.OperationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class OperationService {

    private final AccountService accountService;

    @Transactional(readOnly = true)
    public Operation operationExecute(OperationRequest request) {
        try {
            return accountService.saveCreditDebit(request);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Operation on the same account is happening in concurrent transaction. Will try again... " + request.getAccountId());
            return accountService.saveCreditDebit(request);
        }
    }

}

package eu.octopay.controller;

import eu.octopay.domain.Account;
import eu.octopay.domain.OperationType;
import eu.octopay.exception.EntityNotFoundException;
import eu.octopay.model.BalanceResponse;
import eu.octopay.model.OperationRequest;
import eu.octopay.service.AccountService;
import eu.octopay.service.AssemblerService;
import eu.octopay.service.OperationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AssemblerService assemblerService;
    private final AccountService accountService;
    private final OperationService operationService;

    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all accounts", description = "Get all accounts. For testing purposes.")
    public ResponseEntity<List<EntityModel<Account>>> all() {

        return assemblerService.toModel(accountService.findAll());
    }

    @GetMapping("/accounts/{id}")
    @Operation(summary = "Find account by id", description = "Find account by id")
    public ResponseEntity<EntityModel<Account>> one(@PathVariable String id) {

        Optional<Account> account = accountService.findById(id);

        return assemblerService.toModel(account);
    }

    @Transactional(readOnly = true)
    @GetMapping("/accounts/{id}/operations")
    @Operation(summary = "Find account's operations by id", description = "Find account's operations by id")
    public ResponseEntity<List<EntityModel<eu.octopay.domain.Operation>>> operations(
            @PathVariable String id,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate dateTo) {

        Account account = accountService.findById(id).orElseThrow(EntityNotFoundException::new);

        List<EntityModel<eu.octopay.domain.Operation>> operations;

        if (dateFrom == null && dateTo == null) {
            operations = account.getOperations().stream()
                    .sorted(Comparator.comparing(eu.octopay.domain.Operation::getTimestamp))
                    .map(EntityModel::of)
                    .collect(Collectors.toList());
        } else {
            operations = operationService.findOperationsBetween(id, dateFrom, dateTo).stream()
                    .map(EntityModel::of)
                    .collect(Collectors.toList());
        }

        if (operations.isEmpty()) {
            throw new EntityNotFoundException();
        }

        return ResponseEntity.ok(operations);
    }

    @Transactional(readOnly = true)
    @GetMapping("/accounts/{id}/balance")
    @Operation(summary = "Find account balance", description = "Find account balance")
    public ResponseEntity<EntityModel<BalanceResponse>> balance(@PathVariable String id) {

        BigDecimal balance =  accountService.getBalance(id);
        BalanceResponse response = new BalanceResponse(id, balance);

        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(AccountController.class).one(id)).withSelfRel(),
                linkTo(methodOn(AccountController.class).balance(id)).withRel("balance"),
                linkTo(methodOn(AccountController.class).operations(id, null, null)).withRel("operations"),
                linkTo(methodOn(AccountController.class).all()).withRel("all")));
    }

    @Transactional(readOnly = true)
    @PostMapping("/accounts/{id}/credit")
    @Operation(summary = "Credit account operation", description = "Credit account operation (ΠΙΣΤΩΣΗ)")
    public ResponseEntity<EntityModel<eu.octopay.domain.Operation>> credit(@PathVariable String id, @RequestBody OperationRequest request) {
        request.setAccountId(id);
        request.setType(OperationType.CREDIT);

        eu.octopay.domain.Operation operation = operationService.saveCreditDebit(request);

        return ResponseEntity.ok(EntityModel.of(operation,
                linkTo(methodOn(AccountController.class).one(id)).withSelfRel(),
                linkTo(methodOn(AccountController.class).balance(id)).withRel("balance"),
                linkTo(methodOn(AccountController.class).operations(id, null, null)).withRel("operations"),
                linkTo(methodOn(AccountController.class).all()).withRel("all")));
    }

    @Transactional(readOnly = true)
    @PostMapping("/accounts/{id}/debit")
    @Operation(summary = "Debit account operation", description = "Debit account operation (ΧΡΕΩΣΗ)")
    public ResponseEntity<EntityModel<eu.octopay.domain.Operation>> debit(@PathVariable String id, @RequestBody OperationRequest request) {
        request.setAccountId(id);
        request.setType(OperationType.DEBIT);

        eu.octopay.domain.Operation operation = operationService.saveCreditDebit(request);

        return ResponseEntity.ok(EntityModel.of(operation,
                linkTo(methodOn(AccountController.class).one(id)).withSelfRel(),
                linkTo(methodOn(AccountController.class).balance(id)).withRel("balance"),
                linkTo(methodOn(AccountController.class).operations(id, null, null)).withRel("operations"),
                linkTo(methodOn(AccountController.class).all()).withRel("all")));
    }
}


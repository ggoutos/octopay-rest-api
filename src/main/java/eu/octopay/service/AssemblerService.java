package eu.octopay.service;

import eu.octopay.assembler.AccountAssembler;
import eu.octopay.assembler.OperationAssembler;
import eu.octopay.domain.Account;
import eu.octopay.domain.Operation;
import eu.octopay.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssemblerService {

    private final AccountAssembler accountAssembler;
    private final OperationAssembler operationAssembler;

    public <T extends Serializable> ResponseEntity<EntityModel<T>> toModel(Optional<T> optional) {
        return toModel(optional.orElseThrow(EntityNotFoundException::new));
    }

    public <T extends Serializable> ResponseEntity<EntityModel<T>> toModel(T entity) {

        if (entity == null) {
            throw new EntityNotFoundException();
        }

        RepresentationModelAssembler<T, EntityModel<T>> assembler =
                (RepresentationModelAssembler<T, EntityModel<T>>) getAssembler(entity);

        EntityModel<T> entityModel = assembler.toModel(entity);


        // In restful apis HTTP status must be different between get/post methods
        return ResponseEntity
//                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
//                .body(entityModel);
                .ok(entityModel);
    }

    public <T extends Serializable> ResponseEntity<List<EntityModel<T>>> toModel(Collection<T> list) {
        T firstElement = list.stream().findFirst().orElseThrow(EntityNotFoundException::new);

        RepresentationModelAssembler<T, EntityModel<T>> assembler =
                (RepresentationModelAssembler<T, EntityModel<T>>) getAssembler(firstElement);

        List<EntityModel<T>> listModel = list.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        // In restful apis HTTP status must be different between get/post methods
        return ResponseEntity
//                .created(listModel.get(0).getRequiredLink(IanaLinkRelations.SELF).toUri())
//                .body(listModel);
                .ok(listModel);
    }

    private <T extends Serializable> RepresentationModelAssembler<?, ?> getAssembler(T entity) {
        if (entity instanceof Account) {
            return accountAssembler;
        } else if (entity instanceof Operation) {
            return operationAssembler;
        }
        return null;
    }
}

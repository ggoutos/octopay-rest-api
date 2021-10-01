package eu.octopay.assembler;

import eu.octopay.domain.Operation;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class OperationAssembler implements RepresentationModelAssembler<Operation, EntityModel<Operation>> {

    @Override
    public EntityModel<Operation> toModel(Operation operation) {
        EntityModel<Operation> model = EntityModel.of(operation);
        return model;
    }
}

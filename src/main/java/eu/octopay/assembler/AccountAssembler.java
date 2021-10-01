package eu.octopay.assembler;

import eu.octopay.controller.AccountController;
import eu.octopay.domain.Account;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountAssembler implements RepresentationModelAssembler<Account, EntityModel<Account>> {

    @Override
    public EntityModel<Account> toModel(Account account) {

        EntityModel<Account> model = EntityModel.of(account,
                linkTo(methodOn(AccountController.class).one(account.getId())).withSelfRel(),
                linkTo(methodOn(AccountController.class).balance(account.getId())).withRel("balance"),
                linkTo(methodOn(AccountController.class).operations(account.getId(), null, null)).withRel("operations"),
                linkTo(methodOn(AccountController.class).all()).withRel("all"));

        return model;
    }
}

package com.vip.server.controllers;

import com.vip.server.domain.Account;
import com.vip.server.domain.ui.AccountUI;
import com.vip.server.exceptions.ui.AccountNotExistsOrNotEnoughRightsException;
import com.vip.server.services.AccountService;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotBlank;

@Controller
public class AccountController {
    private final static Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        super();
        this.accountService = accountService;
    }

    @Status(HttpStatus.CREATED)
    @Consumes(MediaType.APPLICATION_JSON)
    @Put(uri = "/accounts")
    public AccountUI createAccount(@NotBlank @Parameter String email) {
        logger.debug("create account {email:" + email + "}");
        final Account account = accountService.createAccount(email);
        return AccountUI.fromDomainAccount(account);
    }

    @Status(HttpStatus.FOUND)
    @Consumes(MediaType.APPLICATION_JSON)
    @Get(uri = "/accounts/{accountId}")
    public AccountUI getAccount(@PathVariable int accountId) throws AccountNotExistsOrNotEnoughRightsException {
        logger.debug("get account {accountId:" + accountId + "}");
        final Account account = accountService.findAccount(accountId)
                .orElseThrow(AccountNotExistsOrNotEnoughRightsException::new);
        return AccountUI.fromDomainAccount(account);
    }
}

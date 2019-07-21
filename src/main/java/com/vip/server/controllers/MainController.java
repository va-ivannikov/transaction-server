package com.vip.server.controllers;

import com.vip.server.domain.Account;
import com.vip.server.domain.Balance;
import com.vip.server.domain.Result;
import com.vip.server.domain.TransactionRequest;
import com.vip.server.exceptions.account.AccountException;
import com.vip.server.exceptions.balance.BalanceException;
import com.vip.server.exceptions.ui.InvalidAccountIdException;
import com.vip.server.exceptions.ui.InvalidMoneyAmountException;
import com.vip.server.exceptions.ui.NotFoundException;
import com.vip.server.exceptions.ui.UserRequestException;
import com.vip.server.services.AccountService;
import com.vip.server.services.BalanceService;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotBlank;

@Controller
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger("Controller");
    private final AccountService accountService;
    private final BalanceService balanceService;

    public MainController(AccountService accountService, BalanceService balanceService) {
        this.accountService = accountService;
        this.balanceService = balanceService;
    }

    @Status(HttpStatus.CREATED)
    @Consumes(MediaType.APPLICATION_JSON)
    @Put(uri = "/accounts")
    public Account createAccount(@NotBlank @Parameter String email) {
        return accountService.createAccount(email);
    }

    @Status(HttpStatus.FOUND)
    @Consumes(MediaType.APPLICATION_JSON)
    @Get(uri = "/accounts/{accountId}")
    public Account getAccount(@PathVariable Integer accountId) throws NotFoundException {
        return accountService.find(accountId)
                .orElseThrow(NotFoundException::new);
    }

    @Status(HttpStatus.CREATED)
    @Consumes(MediaType.APPLICATION_JSON)
    @Put(uri = "/accounts/{accountId}/money")
    public Balance addMoneyToAccount(@PathVariable Integer accountId, @Parameter Double amount) throws AccountException, BalanceException {
        balanceService.addMoneyToAccount(accountId, amount, "manual deposit");
        return balanceService.getBalance(accountId);
    }

    @Status(HttpStatus.FOUND)
    @Consumes(MediaType.APPLICATION_JSON)
    @Get(uri = "/accounts/{accountId}/money")
    public Balance getAccountBalance(@PathVariable Integer accountId) throws AccountException {
        return balanceService.getBalance(accountId);
    }

    @Status(HttpStatus.OK)
    @Consumes(MediaType.APPLICATION_JSON)
    @Post(uri = "/transaction")
    public Result transactionBetweenAccounts(@Body TransactionRequest transactionRequest)
            throws BalanceException, AccountException, UserRequestException {
        int fromId = transactionRequest.getFromAccountId();
        int toId = transactionRequest.getToAccountId();
        double amount = transactionRequest.getAmount();
        validateAccountId(fromId);
        validateAccountId(toId);
        validateAmount(amount);
        return balanceService.transferMoneyFromAccountTo(fromId, toId, amount);
    }

    private void validateAmount(double amount) throws InvalidMoneyAmountException {
        if (amount <= 0) {
            throw new InvalidMoneyAmountException();
        }
    }

    private void validateAccountId(Integer accountId) throws InvalidAccountIdException {
        if (accountId == null || accountId < 1) {
            throw new InvalidAccountIdException(accountId);
        }
    }

    @Error(global = true)
    public HttpResponse<JsonError> error(HttpRequest request, Throwable thr) {
        logger.error("Error on request " + request.getPath(), thr);
        JsonError error = new JsonError("Error: " + thr.getMessage());
        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }
}
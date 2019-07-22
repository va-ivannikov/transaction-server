package com.vip.server.controllers;

import com.vip.server.domain.Balance;
import com.vip.server.domain.ui.BalanceUI;
import com.vip.server.domain.ui.ResultUI;
import com.vip.server.domain.ui.TransactionRequestUI;
import com.vip.server.exceptions.account.AbstractAccountException;
import com.vip.server.exceptions.balance.AbstractBalanceException;
import com.vip.server.exceptions.ui.AbstractUserRequestException;
import com.vip.server.exceptions.ui.InvalidAccountIdException;
import com.vip.server.exceptions.ui.InvalidMoneyAmountException;
import com.vip.server.services.BalanceService;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@Controller
public class BalanceController {
    private final static Logger logger = LoggerFactory.getLogger(BalanceController.class);
    private BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        super();
        this.balanceService = balanceService;
    }

    @Status(HttpStatus.CREATED)
    @Consumes(MediaType.APPLICATION_JSON)
    @Put(uri = "/accounts/{accountId}/money")
    public BalanceUI addMoneyToAccount(@PathVariable int accountId, @Parameter BigDecimal amount) throws AbstractAccountException, AbstractBalanceException {
        logger.debug(String.format("add money to account: {accountId:%s, amount:%s}", accountId, amount.toString()));
        balanceService.addMoneyToAccount(accountId, amount, "manual deposit");
        Balance balance = balanceService.getBalance(accountId);
        return BalanceUI.convertFromDomainBalance(balance);
    }

    @Status(HttpStatus.FOUND)
    @Consumes(MediaType.APPLICATION_JSON)
    @Get(uri = "/accounts/{accountId}/money")
    public BalanceUI getAccountBalance(@PathVariable int accountId) throws AbstractAccountException {
        logger.debug(String.format("get balance: {accountId:%s}", accountId));
        Balance balance = balanceService.getBalance(accountId);
        return BalanceUI.convertFromDomainBalance(balance);
    }

    @Status(HttpStatus.OK)
    @Consumes(MediaType.APPLICATION_JSON)
    @Post(uri = "/transaction")
    public MutableHttpResponse<ResultUI> transactionBetweenAccounts(@Body TransactionRequestUI transactionRequestUI)
            throws AbstractBalanceException, AbstractAccountException, AbstractUserRequestException {
        logger.debug("transaction:" + transactionRequestUI);
        int fromAccountId = transactionRequestUI.getFromAccountId();
        int toAccountId = transactionRequestUI.getToAccountId();
        BigDecimal amount = transactionRequestUI.getAmount();
        checkAccountIdIsPositive(fromAccountId);
        checkAccountIdIsPositive(toAccountId);
        checkAmountMoreThanZero(amount);
        ResultUI resultUI = balanceService.transferMoneyFromAccountTo(fromAccountId, toAccountId, amount);
        return resultUI.isSuccess()
                ? HttpResponse.ok(resultUI)
                : HttpResponse.badRequest(resultUI);
    }

    private void checkAmountMoreThanZero(BigDecimal amount) throws InvalidMoneyAmountException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidMoneyAmountException();
        }
    }

    private void checkAccountIdIsPositive(Integer accountId) throws InvalidAccountIdException {
        if (accountId == null || accountId < 1) {
            throw new InvalidAccountIdException();
        }
    }
}

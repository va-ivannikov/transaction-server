package com.vip.server.services;

import com.vip.server.domain.Account;
import com.vip.server.domain.Balance;
import com.vip.server.exceptions.account.AccountException;
import com.vip.server.exceptions.account.AccountNotFoundException;
import com.vip.server.exceptions.balance.AmountShouldBePositiveException;
import com.vip.server.exceptions.balance.BalanceException;
import com.vip.server.exceptions.balance.SameAccountException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class BalanceServiceTest extends AbstractTest {
    @Inject
    public AccountService accountService;
    @Inject
    public BalanceService balanceService;

    private Account account;

    @BeforeEach
    void prepare() {
        account = accountService.createAccount(getRandomEmail());
    }

    @Test
    void checkBalanceOnFreshAccount() throws AccountException {
        Balance balance = balanceService.getBalance(account.getId());
        assertEquals(account.getId(), balance.getAccountId());
        assertEquals(0.0, balance.getCurrent());
    }

    @Test
    void addMoney() throws AccountException, BalanceException {
        assertTrue(balanceService.addMoneyToAccount(account.getId(), 100.0, "test"));
        Balance balance = balanceService.getBalance(account.getId());
        assertEquals(100.0, balance.getCurrent());
    }

    @Test
    void transferMoney() throws BalanceException, AccountException {
        Account account2 = accountService.createAccount(getRandomEmail());
        balanceService.addMoneyToAccount(account.getId(), 100.0, "test");
        balanceService.transferMoneyFromAccountTo(account.getId(), account2.getId(), 50.0);
        Balance balance1 = balanceService.getBalance(account.getId());
        Balance balance2 = balanceService.getBalance(account2.getId());
        assertEquals(50.0, balance1.getCurrent());
        assertEquals(50.0, balance2.getCurrent());
    }

    @Test
    void getBalanceForNotExistedAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> balanceService.getBalance(Integer.MAX_VALUE));
    }

    @Test
    void addNegativeAmountToAccount() {
        assertThrows(AmountShouldBePositiveException.class,
                () -> balanceService.addMoneyToAccount(account.getId(), -10.0, "test"));
    }

    @Test
    void addMoneyToNotExistedAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> balanceService.addMoneyToAccount(Integer.MAX_VALUE, 10.0, "test"));
    }

    @Test
    void transferFromNotExistedAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> balanceService.transferMoneyFromAccountTo(Integer.MAX_VALUE, account.getId(), 10.0));
    }

    @Test
    void transferToNotExistedAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> balanceService.transferMoneyFromAccountTo(account.getId(), Integer.MAX_VALUE, 10.0));
    }

    @Test
    void transferFromToSameAccount() {
        assertThrows(SameAccountException.class,
                () -> balanceService.transferMoneyFromAccountTo(account.getId(), account.getId(), 10.0));
    }

    @Test
    void transferNegativeValue() {
        Account account2 = accountService.createAccount(getRandomEmail());
        assertThrows(AmountShouldBePositiveException.class,
                () -> balanceService.transferMoneyFromAccountTo(account.getId(), account2.getId(), -10.0));
    }
}

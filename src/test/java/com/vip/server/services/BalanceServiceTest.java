package com.vip.server.services;

import com.vip.server.domain.Account;
import com.vip.server.domain.Balance;
import com.vip.server.exceptions.account.AbstractAccountException;
import com.vip.server.exceptions.account.AccountNotFoundException;
import com.vip.server.exceptions.account.OperationCantBePerformedOnTheSameAccount;
import com.vip.server.exceptions.balance.AbstractBalanceException;
import com.vip.server.exceptions.balance.AmountShouldBePositiveException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class BalanceServiceTest extends AbstractTest {
    private static final BigDecimal big100 = BigDecimal.valueOf(100);
    private static final BigDecimal big50 = BigDecimal.valueOf(50);
    @Inject
    private AccountService accountService;
    @Inject
    private BalanceService balanceService;

    private Account account;

    @BeforeEach
    void prepare() {
        account = accountService.createAccount(getRandomEmail());
    }

    @Test
    void checkBalanceOnFreshAccount() throws AbstractAccountException {
        Balance balance = balanceService.getBalance(account.getId());
        assertEquals(account.getId(), balance.getAccountId());
        assertEquals(BigDecimal.ZERO, balance.getCurrent());
    }

    @Test
    void addMoney() throws AbstractAccountException, AbstractBalanceException {
        assertTrue(balanceService.addMoneyToAccount(account.getId(), big100, "test"));
        Balance balance = balanceService.getBalance(account.getId());
        assertEquals(big100, balance.getCurrent());
    }

    @Test
    void transferMoney() throws AbstractBalanceException, AbstractAccountException {
        Account account2 = accountService.createAccount(getRandomEmail());
        balanceService.addMoneyToAccount(account.getId(), big100, "test");
        balanceService.transferMoneyFromAccountTo(account.getId(), account2.getId(), big50);
        Balance balance1 = balanceService.getBalance(account.getId());
        Balance balance2 = balanceService.getBalance(account2.getId());
        assertEquals(big50, balance1.getCurrent());
        assertEquals(big50, balance2.getCurrent());
    }


    @Test
    void transferMoneyConcurrent() throws AbstractBalanceException, AbstractAccountException, InterruptedException {
        Account account2 = accountService.createAccount(getRandomEmail());
        balanceService.addMoneyToAccount(account.getId(), big100, "test");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Callable<String>> callableTasks = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            callableTasks.add(() -> {
                try {
                    balanceService.transferMoneyFromAccountTo(account.getId(), account2.getId(), BigDecimal.ONE);
                } catch (AbstractBalanceException | AbstractAccountException e) {
                    e.printStackTrace();
                }
                return "";
            });
        }
        callableTasks.add(() -> {
            try {
                balanceService.transferMoneyFromAccountTo(account.getId(), account2.getId(), BigDecimal.valueOf(-10));
            } catch (AbstractBalanceException | AbstractAccountException e) {
                e.printStackTrace();
            }
            return "";
        });

        executorService.invokeAll(callableTasks);
        executorService.shutdown();

        assertEquals(big50, balanceService.getBalance(account.getId()).getCurrent());
        assertEquals(big50, balanceService.getBalance(account2.getId()).getCurrent());
    }

    @Test
    void getBalanceForNotExistedAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> balanceService.getBalance(Integer.MAX_VALUE));
    }

    @Test
    void addNegativeAmountToAccount() {
        assertThrows(AmountShouldBePositiveException.class,
                () -> balanceService.addMoneyToAccount(account.getId(), BigDecimal.TEN.negate(), "test"));
    }

    @Test
    void addMoneyToNotExistedAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> balanceService.addMoneyToAccount(Integer.MAX_VALUE, BigDecimal.TEN, "test"));
    }

    @Test
    void transferFromNotExistedAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> balanceService.transferMoneyFromAccountTo(Integer.MAX_VALUE, account.getId(), BigDecimal.TEN));
    }

    @Test
    void transferToNotExistedAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> balanceService.transferMoneyFromAccountTo(account.getId(), Integer.MAX_VALUE, BigDecimal.TEN));
    }

    @Test
    void transferFromToSameAccount() {
        assertThrows(OperationCantBePerformedOnTheSameAccount.class,
                () -> balanceService.transferMoneyFromAccountTo(account.getId(), account.getId(), BigDecimal.TEN));
    }

    @Test
    void transferNegativeValue() {
        Account account2 = accountService.createAccount(getRandomEmail());
        assertThrows(AmountShouldBePositiveException.class,
                () -> balanceService.transferMoneyFromAccountTo(account.getId(), account2.getId(), BigDecimal.TEN.negate()));
    }
}

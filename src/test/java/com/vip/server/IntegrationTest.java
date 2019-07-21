package com.vip.server;

import com.vip.server.domain.Account;
import com.vip.server.domain.Balance;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class IntegrationTest {
    @Inject
    TestClient client;

    private static String testEmail = "test1.email";

    @Test
    void createAccountAndValidate() {
        Account createdAccount = client.createAccount(testEmail).blockingGet();
        assertEquals(testEmail, createdAccount.getEmail(),
                "Validate email as expected");
        assertTrue(createdAccount.getId() > 0,
                "Account id should exists and be greater than 0.");
    }

    @Test
    void createAccountAndCheckBalance() {
        Account createdAccount = client.createAccount(testEmail).blockingGet();
        Balance accountBalance = client.getAccountBalance(createdAccount.getId()).blockingGet();
        assertEquals(createdAccount.getId(), accountBalance.getAccountId(),
                "AccountId from account and balance should be same.");
        assertEquals(0, accountBalance.getCurrent(), "Account without money on balance.");
    }

    @Test
    void createAccountAddMoneyAndCheckBalance() {
        double amount = 100.0;
        Account createdAccount = client.createAccount(testEmail).blockingGet();
        client.addMoneyToAccount(createdAccount.getId(), amount).blockingGet();
        Balance accountBalance = client.getAccountBalance(createdAccount.getId()).blockingGet();
        assertEquals(createdAccount.getId(), accountBalance.getAccountId(),
                "AccountId from account and balance should be same.");
        assertEquals(amount, accountBalance.getCurrent(),
                "Balance should be $" + amount + ".");
    }
}

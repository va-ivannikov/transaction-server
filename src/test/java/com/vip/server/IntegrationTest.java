package com.vip.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vip.server.domain.ui.AccountUI;
import com.vip.server.domain.ui.BalanceUI;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class IntegrationTest {
    //    @Inject
//    TestClient client;
    @Inject
    private EmbeddedServer server;

    private static String testEmail = "test1.email";
    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpClient httpClient = HttpClient.newHttpClient();

    private JavaSimpleClient client = new JavaSimpleClient();

    @BeforeEach
    void setup() {
        client.setHost(server.getURI());
    }

    @Test
    void createAccountAndValidate() throws InterruptedException, IOException {
        AccountUI createdAccount = client.createAccount(testEmail);
        assertEquals(testEmail, createdAccount.getEmail(),
                "Validate email as expected");
        assertTrue(createdAccount.getId() > 0,
                "Account id should exists and be greater than 0.");
    }

    @Test
    void createAccountAndCheckBalance() throws IOException, InterruptedException {
        AccountUI createdAccount = client.createAccount(testEmail);
        BalanceUI accountBalance = client.getBalanceForAccount(createdAccount.getId());
        assertEquals(createdAccount.getId(), accountBalance.getAccountId(),
                "AccountId from account and balance should be same.");
        assertEquals(BigDecimal.ZERO, accountBalance.getCurrent(), "Account without money on balance.");
    }

    @Test
    void createAccountAndAddMoney() throws IOException, InterruptedException {
        AccountUI createdAccount = client.createAccount(testEmail);
        client.addMoneyToAccount(createdAccount.getId(), 100.0);
        BalanceUI accountBalance = client.getBalanceForAccount(createdAccount.getId());
        assertEquals(createdAccount.getId(), accountBalance.getAccountId(),
                "AccountId from account and balance should be same.");
        assertEquals(BigDecimal.valueOf(100.0), accountBalance.getCurrent(), "Account without money on balance.");
    }

    @Test
    void transferMoneyBetweenAccounts() throws IOException, InterruptedException {
        AccountUI createdAccount1 = client.createAccount(testEmail);
        AccountUI createdAccount2 = client.createAccount(testEmail);
        client.addMoneyToAccount(createdAccount1.getId(), 100.0);
        client.transfer(createdAccount1.getId(), createdAccount2.getId(), 50.0);
        BalanceUI accountBalance1 = client.getBalanceForAccount(createdAccount1.getId());
        BalanceUI accountBalance2 = client.getBalanceForAccount(createdAccount2.getId());
        assertEquals(BigDecimal.valueOf(50.0), accountBalance1.getCurrent(),
                "Balance should be $50.");
        assertEquals(BigDecimal.valueOf(50.0), accountBalance2.getCurrent(),
                "Balance should be $50.");
    }

//    @Test
//    void createAccountAddMoneyAndCheckBalance() {
//        double amount = 100.0;
//        AccountUI createdAccount = client.createAccount(testEmail).blockingGet();
//        client.addMoneyToAccount(createdAccount.getId(), amount).blockingGet();
//        BalanceUI accountBalance = client.getAccountBalance(createdAccount.getId()).blockingGet();
//        assertEquals(createdAccount.getId(), accountBalance.getAccountId(),
//                "AccountId from account and balance should be same.");
//        assertEquals(amount, accountBalance.getCurrent(),
//                "Balance should be $" + amount + ".");
//    }
}

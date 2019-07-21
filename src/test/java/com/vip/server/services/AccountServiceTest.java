package com.vip.server.services;

import com.vip.server.domain.Account;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class AccountServiceTest extends AbstractTest {
    @Inject
    private AccountService accountService;

    @Test
    void createAndValidateAccount() {
        String expectedEmail = getRandomEmail();
        Account createdAccount = accountService.createAccount(expectedEmail);
        assertEquals(expectedEmail, createdAccount.getEmail());
        assertTrue(createdAccount.getId() > 0);
        assertFalse(createdAccount.isLocked());
        assertFalse(createdAccount.isClosed());
    }

    @Test
    void findNotExistsAccount() {
        Optional<Account> account = accountService.find(Integer.MAX_VALUE);
        assertTrue(account.isEmpty());
    }

    @Test
    void findAccount() {
        String expectedEmail = getRandomEmail();
        Account createdAccount = accountService.createAccount(expectedEmail);
        Optional<Account> optAccount = accountService.find(createdAccount.getId());
        assertTrue(optAccount.isPresent());
        Account account = optAccount.get();
        assertEquals(expectedEmail, account.getEmail());
        assertEquals(createdAccount.getId(), account.getId());
    }
}

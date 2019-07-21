package com.vip.server.services;

import com.vip.server.domain.Account;
import com.vip.server.exceptions.account.AccountException;
import com.vip.server.exceptions.account.AccountNotFoundException;
import com.vip.server.exceptions.account.CantDoPaymentOnLockedException;
import com.vip.server.repositories.AccountRepository;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account createAccount(String email) {
        return accountRepository.save(new Account(email));
    }

    @Override
    public Optional<Account> find(int id) {
        return accountRepository.findById(id);
    }

    @Override
    public Account validateAccountForPaymentsAndGet(int accountId) throws AccountException {
        Account account = find(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));
        if (account.isClosed()) {
            throw new CantDoPaymentOnLockedException("Account [" + accountId + " is closed/archived.");
        }
        if (account.isLocked()) {
            throw new CantDoPaymentOnLockedException("Account [" + accountId + " is locked.");
        }
        return account;
    }

    @Override
    public Account validateActiveAccountAndGetIt(int accountId) throws AccountException {
        Account account = find(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));
        if (account.isClosed()) {
            throw new CantDoPaymentOnLockedException("Account [" + accountId + " is closed/archived.");
        }
        return account;
    }
}
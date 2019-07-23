package com.vip.server.services;

import com.vip.server.domain.Account;
import com.vip.server.exceptions.account.AbstractAccountException;
import com.vip.server.exceptions.account.AccountIsDeletedException;
import com.vip.server.exceptions.account.AccountIsLockedForPaymentOperationException;
import com.vip.server.exceptions.account.AccountNotFoundException;
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
    public void updateAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    public Optional<Account> findAccount(int accountId) {
        return accountRepository.findById(accountId);
    }

    private Account getAccountIfExistsOrThrowNotFound(int accountId) throws AccountNotFoundException {
        return findAccount(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    public void lockAccountById(int accountId) throws AccountNotFoundException {
        Account account = getAccountIfExistsOrThrowNotFound(accountId);
        account.lock();
        updateAccount(account);
    }

    @Override
    public void unlockAccountById(int accountId) throws AccountNotFoundException {
        Account account = getAccountIfExistsOrThrowNotFound(accountId);
        account.unLock();
        updateAccount(account);
    }

    @Override
    public void checkIsAccountReadyForPayments(int accountId) throws AbstractAccountException {
        final Account account = getAccountIfExistsOrThrowNotFound(accountId);
        if (account.isDeleted()) {
            throw new AccountIsDeletedException(accountId);
        }
        if (account.isLocked()) {
            throw new AccountIsLockedForPaymentOperationException(accountId);
        }
    }

    @Override
    public void checkAccountIsActive(int accountId) throws AbstractAccountException {
        if (getAccountIfExistsOrThrowNotFound(accountId).isDeleted()) {
            throw new AccountIsDeletedException(accountId);
        }
    }
}
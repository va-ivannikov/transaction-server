package com.vip.server.services;

import com.vip.server.domain.Account;
import com.vip.server.exceptions.account.AbstractAccountException;
import com.vip.server.exceptions.account.AccountNotFoundException;

import java.util.Optional;

public interface AccountService {

    Account createAccount(String email);

    void updateAccount(Account account);

    Optional<Account> findAccount(int accountId);

    void lockAccountById(int accountId) throws AccountNotFoundException;

    void unlockAccountById(int accountId) throws AccountNotFoundException;

    void checkIsAccountReadyForPayments(int accountId) throws AbstractAccountException;

    void checkAccountIsActive(int accountId) throws AbstractAccountException;
}

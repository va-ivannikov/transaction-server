package com.vip.server.services;

import com.vip.server.domain.Account;
import com.vip.server.exceptions.account.AbstractAccountException;

import java.util.Optional;

public interface AccountService {

    Account createAccount(String email);

    Optional<Account> findAccount(int accountId);

    void checkIsAccountReadyForPayments(int accountId) throws AbstractAccountException;

    void checkAccountIsActive(int accountId) throws AbstractAccountException;
}

package com.vip.server.services;

import com.vip.server.domain.Account;
import com.vip.server.exceptions.account.AccountException;

import java.util.Optional;

public interface AccountService {

    Account createAccount(String email);

    Optional<Account> find(int id);

    Account validateAccountForPaymentsAndGet(int accountId) throws AccountException;

    Account validateActiveAccountAndGetIt(int accountId) throws AccountException;
}

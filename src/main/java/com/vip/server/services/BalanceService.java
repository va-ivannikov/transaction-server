package com.vip.server.services;

import com.vip.server.domain.Balance;
import com.vip.server.domain.Result;
import com.vip.server.exceptions.account.AccountException;
import com.vip.server.exceptions.balance.BalanceException;

public interface BalanceService {
    boolean addMoneyToAccount(int accountId, Double amount, String reason) throws AccountException, BalanceException;

    Balance getBalance(int accountId) throws AccountException;

    Result transferMoneyFromAccountTo(int fromId, int toId, double amount) throws BalanceException, AccountException;
}

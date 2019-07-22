package com.vip.server.services;

import com.vip.server.domain.Balance;
import com.vip.server.domain.ui.ResultUI;
import com.vip.server.exceptions.account.AbstractAccountException;
import com.vip.server.exceptions.balance.AbstractBalanceException;

import java.math.BigDecimal;

public interface BalanceService {
    boolean addMoneyToAccount(int accountId, BigDecimal amount, String reason) throws AbstractAccountException, AbstractBalanceException;

    Balance getBalance(int accountId) throws AbstractAccountException;

    ResultUI transferMoneyFromAccountTo(int fromAccountById, int toAccountById, BigDecimal amount) throws AbstractBalanceException, AbstractAccountException;
}

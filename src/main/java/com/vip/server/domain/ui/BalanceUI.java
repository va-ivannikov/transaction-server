package com.vip.server.domain.ui;

import com.vip.server.domain.Balance;

import java.math.BigDecimal;

public class BalanceUI {
    private int accountId;
    private BigDecimal current;

    private BalanceUI() {}

    public static BalanceUI convertFromDomainBalance(Balance balance) {
        return new BalanceUI(balance);
    }

    private BalanceUI(Balance balance) {
        this.accountId = balance.getAccountId();
        this.current = balance.getCurrent();
    }

    public int getAccountId() {
        return accountId;
    }

    public BigDecimal getCurrent() {
        return current;
    }
}

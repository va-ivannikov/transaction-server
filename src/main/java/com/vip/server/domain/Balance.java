package com.vip.server.domain;

import java.math.BigDecimal;
import java.util.StringJoiner;

public class Balance {
    private BigDecimal totalDeposit;
    private BigDecimal totalWithdraw;
    private BigDecimal totalHold;
    private int accountId;

    private Balance() {}

    public Balance(int accountId, BigDecimal totalDeposit, BigDecimal totalWithdraw, BigDecimal totalHold) {
        this.accountId = accountId;
        this.totalDeposit = totalDeposit;
        this.totalWithdraw = totalWithdraw;
        this.totalHold = totalHold;
    }

    public BigDecimal getCurrent() {
        return totalDeposit.subtract(totalWithdraw).subtract(totalHold);
    }

    public Integer getAccountId() {
        return accountId;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Balance.class.getSimpleName() + "[", "]")
                .add("accountId=" + accountId)
                .add("deposited=" + totalDeposit)
                .add("withdraw=" + totalWithdraw)
                .add("hold=" + totalHold)
                .toString();
    }
}

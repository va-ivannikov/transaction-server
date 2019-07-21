package com.vip.server.domain;

import java.util.StringJoiner;

public class Balance {
    private double current;
    private Integer accountId;

    private Balance() {}

    public Balance(Integer accountId, double current) {
        this.current = current;
        this.accountId = accountId;
    }

    public double getCurrent() {
        return current;
    }

    public Integer getAccountId() {
        return accountId;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Balance.class.getSimpleName() + "[", "]")
                .add("accountId=" + accountId)
                .add("current=" + current)
                .toString();
    }
}

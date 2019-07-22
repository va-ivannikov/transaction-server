package com.vip.server.domain.ui;

import java.math.BigDecimal;
import java.util.StringJoiner;

public class TransactionRequestUI {
    private int fromAccountId;
    private int toAccountId;
    private BigDecimal amount;

    private TransactionRequestUI() {}

    public TransactionRequestUI(int fromAccountId, int toAccountId, BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    public int getFromAccountId() {
        return fromAccountId;
    }

    public int getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TransactionRequestUI.class.getSimpleName() + "[", "]")
                .add("fromAccountId=" + fromAccountId)
                .add("toAccountId=" + toAccountId)
                .add("amount=" + amount)
                .toString();
    }
}

package com.vip.server.domain;

public class TransactionRequest {
    private int fromAccountId;
    private int toAccountId;
    private double amount;

    private TransactionRequest() {}

    public int getFromAccountId() {
        return fromAccountId;
    }

    public int getToAccountId() {
        return toAccountId;
    }

    public double getAmount() {
        return amount;
    }
}

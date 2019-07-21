package com.vip.server.domain;

import java.time.LocalDateTime;

public class Operation extends AbstractId<Integer> {
    private LocalDateTime localDateTime;
    private Integer accountId;
    private double amount;
    private String reason;
    private OperationType operationType;

    public Operation(OperationType opType, Integer accountId, double amount, String reason) {
        this.operationType = opType;
        this.localDateTime = LocalDateTime.now();
        this.accountId = accountId;
        this.amount = amount;
        this.reason = reason;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }
}


package com.vip.server.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Operation extends AbstractEntityWithId<Integer> {
    private LocalDateTime localDateTime;
    private Integer accountId;
    private BigDecimal amount;
    private String reason;
    private OperationType operationType;

    public Operation(OperationType opType, Integer accountId, BigDecimal amount, String reason) {
        this.operationType = opType;
        this.localDateTime = LocalDateTime.now();
        this.accountId = accountId;
        this.amount = amount;
        this.reason = reason;
    }

    public static Operation deposit(int toAccountById, BigDecimal amount, String reason) {
        return new Operation(OperationType.DEPOSIT, toAccountById, amount, reason);
    }

    public static Operation withdraw(int fromAccountById, BigDecimal amount, String reason) {
        return new Operation(OperationType.WITHDRAW, fromAccountById, amount, reason);
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OperationType getOperationType() {
        return operationType;
    }
}


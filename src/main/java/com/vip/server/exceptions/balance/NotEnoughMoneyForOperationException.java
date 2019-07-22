package com.vip.server.exceptions.balance;

public class NotEnoughMoneyForOperationException extends AbstractBalanceException {
    public NotEnoughMoneyForOperationException(int accountId) {
        super("Not enough money for operation on account[" + accountId + "].");
    }
}

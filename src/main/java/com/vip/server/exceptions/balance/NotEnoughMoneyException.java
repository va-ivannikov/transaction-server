package com.vip.server.exceptions.balance;

public class NotEnoughMoneyException extends BalanceException {
    public NotEnoughMoneyException(int accountId) {
        super("Not enough money on account[" + accountId + "].");
    }
}

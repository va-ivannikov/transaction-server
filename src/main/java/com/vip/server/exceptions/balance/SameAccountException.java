package com.vip.server.exceptions.balance;

public class SameAccountException extends BalanceException {
    public SameAccountException() {
        super("Cant do this operation between the same account.");
    }
}

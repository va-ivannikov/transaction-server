package com.vip.server.exceptions.balance;

public abstract class AbstractBalanceException extends Throwable {
    AbstractBalanceException(String message) {
        super("Balance Error: " + message);
    }

}


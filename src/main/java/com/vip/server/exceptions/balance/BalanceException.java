package com.vip.server.exceptions.balance;

public class BalanceException extends Throwable {
    BalanceException(String message) {
        super("Balance Error: " + message);
    }

}


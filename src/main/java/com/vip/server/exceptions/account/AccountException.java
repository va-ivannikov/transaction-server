package com.vip.server.exceptions.account;

public class AccountException extends Throwable {
    public AccountException(String message) {
        super("Account Error: " + message);
    }
}

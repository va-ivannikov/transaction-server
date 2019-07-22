package com.vip.server.exceptions.account;

public abstract class AbstractAccountException extends Throwable {
    public AbstractAccountException(String message) {
        super("Account Error: " + message);
    }
}

package com.vip.server.exceptions.account;

public class CantDoActionOnClosedException extends AccountException {
    public CantDoActionOnClosedException(String message) {
        super(message);
    }
}

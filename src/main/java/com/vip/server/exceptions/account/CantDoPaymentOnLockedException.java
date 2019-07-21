package com.vip.server.exceptions.account;

public class CantDoPaymentOnLockedException extends AccountException {
    public CantDoPaymentOnLockedException(String message) {
        super(message);
    }
}

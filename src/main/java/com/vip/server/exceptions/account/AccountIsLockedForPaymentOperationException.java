package com.vip.server.exceptions.account;

public class AccountIsLockedForPaymentOperationException extends AbstractAccountException {
    public AccountIsLockedForPaymentOperationException(int accountId) {
        super("Account [" + accountId + "] is locked for payment operation.");
    }
}

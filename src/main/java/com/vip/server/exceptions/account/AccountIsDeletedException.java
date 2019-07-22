package com.vip.server.exceptions.account;

public class AccountIsDeletedException extends AbstractAccountException {
    public AccountIsDeletedException(int accountId) {
        super("Account [" + accountId + "] is deleted.");
    }
}

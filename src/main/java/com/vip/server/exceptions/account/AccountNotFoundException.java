package com.vip.server.exceptions.account;

public class AccountNotFoundException extends AbstractAccountException {
    public AccountNotFoundException(int accountId) {
        super("Account [" + accountId + "] not found.");
    }
}

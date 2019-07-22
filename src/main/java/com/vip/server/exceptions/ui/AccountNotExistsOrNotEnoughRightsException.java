package com.vip.server.exceptions.ui;

public class AccountNotExistsOrNotEnoughRightsException extends AbstractUserRequestException {
    public AccountNotExistsOrNotEnoughRightsException() {
        super("Account not exists or not enough rights.");
    }
}

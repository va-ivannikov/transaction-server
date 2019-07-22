package com.vip.server.exceptions.ui;

public class InvalidAccountIdException extends AbstractUserRequestException {
    public InvalidAccountIdException() {
        super("Account id should be positive.");
    }
}

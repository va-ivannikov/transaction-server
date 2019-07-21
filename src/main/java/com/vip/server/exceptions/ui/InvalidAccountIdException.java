package com.vip.server.exceptions.ui;

public class InvalidAccountIdException extends UserRequestException {
    public InvalidAccountIdException(Integer accountId) {
        super("Account id should be positive.");
    }
}

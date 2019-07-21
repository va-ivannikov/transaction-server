package com.vip.server.exceptions.ui;

public class NotFoundException extends UserRequestException {
    public NotFoundException() {
        super("Account not exists or not enough rights.");
    }
}

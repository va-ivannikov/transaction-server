package com.vip.server.exceptions.ui;

public abstract class AbstractUserRequestException extends Throwable {
    AbstractUserRequestException(String message) {
        super(message);
    }
}

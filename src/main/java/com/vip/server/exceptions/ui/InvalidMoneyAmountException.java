package com.vip.server.exceptions.ui;

public class InvalidMoneyAmountException extends AbstractUserRequestException {
    public InvalidMoneyAmountException() {
        super("Invalid money count.");
    }
}

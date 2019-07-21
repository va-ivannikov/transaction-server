package com.vip.server.exceptions.ui;

public class InvalidMoneyAmountException extends UserRequestException {
    public InvalidMoneyAmountException() {
        super("Invalid money count.");
    }
}

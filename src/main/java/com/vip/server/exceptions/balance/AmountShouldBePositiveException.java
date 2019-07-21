package com.vip.server.exceptions.balance;

public class AmountShouldBePositiveException extends BalanceException {
    public AmountShouldBePositiveException() {
        super("For this operations amount should be positive.");
    }
}

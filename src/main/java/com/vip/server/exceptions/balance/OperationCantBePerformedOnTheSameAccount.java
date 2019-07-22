package com.vip.server.exceptions.balance;

public class OperationCantBePerformedOnTheSameAccount extends AbstractBalanceException {
    public OperationCantBePerformedOnTheSameAccount() {
        super("The operation cannot be performed on the same account.");
    }
}

package com.vip.server.exceptions.account;

public class OperationCantBePerformedOnTheSameAccount extends AbstractAccountException {
    public OperationCantBePerformedOnTheSameAccount() {
        super("The operation cannot be performed on the same account.");
    }
}

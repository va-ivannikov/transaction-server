package com.vip.server.domain.ui;

import com.vip.server.domain.Account;

public class AccountUI {
    private int id;
    private String email;

    private AccountUI() {}

    private AccountUI(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public static AccountUI fromDomainAccount(Account account) {
        return new AccountUI(account.getId(), account.getEmail());
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}

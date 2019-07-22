package com.vip.server.domain;

import java.util.StringJoiner;

public class Account extends AbstractEntityWithId<Integer> {
    private volatile boolean locked = false;
    private boolean deleted = false;
    private String email;

    private Account() {} //for deserialization
    public Account(String email) {
        this.email = email;
    }

    public void lock() {
        this.locked = true;
    }

    public void unLock() {
        this.locked = false;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setId(Integer id) {
        super.setId(id);
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Account.class.getSimpleName() + "[", "]")
                .add("id=" + getId())
                .add("locked=" + isLocked())
                .add("deleted=" + isDeleted())
                .add("email='" + getEmail() + "'")
                .toString();
    }
}

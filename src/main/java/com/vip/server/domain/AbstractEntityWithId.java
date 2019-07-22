package com.vip.server.domain;

import java.beans.Transient;

public abstract class AbstractEntityWithId<T> {
    private T id;

    public AbstractEntityWithId() {
        this.id = null;
    }

    @Transient
    public boolean isNew() {
        return id == null;
    }

    public void setId(T id) {
        this.id = id;
    }

    public T getId() {
        return this.id;
    }
}

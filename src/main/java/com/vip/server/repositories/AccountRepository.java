package com.vip.server.repositories;

import com.vip.server.domain.Account;

import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class AccountRepository extends Repository<Account, Integer> {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    Integer getNextId() {
        return counter.incrementAndGet();
    }
}

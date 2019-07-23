package com.vip.server.repositories;

import com.vip.server.domain.Hold;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class HoldRepository extends Repository<Hold, Integer> {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    Integer getNextId() {
        return counter.incrementAndGet();
    }

    public BigDecimal countHoldSumByAccountId(int accountId) {
        return storage.values().stream()
                .filter(hold -> hold.isActive() && hold.getAccountId() == accountId)
                .map(Hold::getAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}

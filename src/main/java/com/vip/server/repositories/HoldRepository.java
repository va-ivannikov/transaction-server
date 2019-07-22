package com.vip.server.repositories;

import com.vip.server.domain.Hold;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class HoldRepository extends Repository<Hold, Integer> {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Hold save(Hold account) {
        if (account.isNew()) {
            account.setId(counter.incrementAndGet());
        }
        return super.save(account);
    }

    public BigDecimal countHoldSumByAccountId(int accountId) {
        return storage.values().stream()
                .filter(hold -> hold.isActive() && hold.getAccountId() == accountId)
                .map(Hold::getAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}

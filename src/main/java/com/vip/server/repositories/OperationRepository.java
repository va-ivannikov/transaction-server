package com.vip.server.repositories;

import com.vip.server.domain.Operation;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class OperationRepository extends Repository<Operation, Integer> {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    Integer getNextId() {
        return counter.incrementAndGet();
    }

    public BigDecimal getSumByOperationAndAccountId(Operation.OperationType operationType, Integer accountId) {
        return storage.values().stream()
                .filter(operation ->
                        operation.getAccountId().equals(accountId)
                                && operation.getOperationType().equals(operationType))
                .map(Operation::getAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}

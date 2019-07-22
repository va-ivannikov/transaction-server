package com.vip.server.repositories;

import com.vip.server.domain.Operation;
import com.vip.server.domain.OperationType;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Singleton
public class OperationRepository extends Repository<Operation, Integer> {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Operation save(Operation operation) {
        if (operation.isNew()) {
            operation.setId(counter.incrementAndGet());
        }
        return super.save(operation);
    }

    public List<Operation> getOperationsByAccountId(Integer accountId) {
        return storage.values().stream()
                .filter(operation -> operation.getAccountId().equals(accountId))
                .collect(Collectors.toList());
    }

    public BigDecimal getSumByOperationAndAccountId(OperationType operationType, Integer accountId) {
        return storage.values().stream()
                .filter(operation ->
                        operation.getAccountId().equals(accountId)
                                && operation.getOperationType().equals(operationType))
                .map(Operation::getAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}

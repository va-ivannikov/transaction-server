package com.vip.server.services;

import com.vip.server.domain.*;
import com.vip.server.repositories.HoldRepository;
import com.vip.server.repositories.OperationRepository;
import com.vip.server.exceptions.account.AccountException;
import com.vip.server.exceptions.balance.AmountShouldBePositiveException;
import com.vip.server.exceptions.balance.BalanceException;
import com.vip.server.exceptions.balance.NotEnoughMoneyException;
import com.vip.server.exceptions.balance.SameAccountException;
import io.micronaut.scheduling.annotation.Scheduled;

import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static com.vip.server.domain.OperationType.DEPOSIT;
import static com.vip.server.domain.OperationType.WITHDRAW;

@Singleton
public class BalanceServiceImpl implements BalanceService {
    private final OperationRepository operationRepository;
    private final HoldRepository holdRepository;
    private final AccountService accountService;

    public BalanceServiceImpl(OperationRepository operationRepository,
                              HoldRepository holdRepository,
                              AccountService accountService) {
        this.operationRepository = operationRepository;
        this.accountService = accountService;
        this.holdRepository = holdRepository;
    }

    @Override
    public boolean addMoneyToAccount(int accountId, Double amount, String reason) throws BalanceException, AccountException {
        validateAmount(amount);
        accountService.validateAccountForPaymentsAndGet(accountId);
        operationRepository.save(new Operation(DEPOSIT, accountId, amount, reason));
        return true;
    }

    @Override
    public Balance getBalance(int accountId) throws AccountException {
        accountService.validateActiveAccountAndGetIt(accountId);
        double depositTotal = operationRepository.getSumByOperationAndAccountId(DEPOSIT, accountId);
        double withdrawTotal = operationRepository.getSumByOperationAndAccountId(WITHDRAW, accountId);
        double holdTotal = holdRepository.countHoldSumByAccountId(accountId);
        return new Balance(accountId, depositTotal - withdrawTotal - holdTotal);
    }

    @Override
    public Result transferMoneyFromAccountTo(int fromId, int toId, double amount) throws BalanceException, AccountException {
        if (fromId == toId) {
            throw new SameAccountException();
        }
        validateAmount(amount);
        accountService.validateAccountForPaymentsAndGet(fromId);
        accountService.validateAccountForPaymentsAndGet(toId);
        Hold hold = null;
        Operation deposit = null;
        Operation withdraw = null;
        try {
            hold = createHoldForAccount(fromId, amount,
                    String.format("Transfer between accounts: from [%s] to [%s].", fromId, toId));
            if (getBalance(fromId).getCurrent() < 0) {
                throw new NotEnoughMoneyException(fromId);
            }
            accountService.validateAccountForPaymentsAndGet(toId);
            deposit = operationRepository.save(new Operation(DEPOSIT, toId, amount, "Transfer from account " + fromId));
            withdraw = operationRepository.save(new Operation(WITHDRAW, fromId, amount, "Transfer to account " + toId));
            hold.markAsClosed(String.format("Transfer from %s to %s.", fromId, toId));
            holdRepository.save(hold);
            return Result.success(String.format("Transfer complete: $%s from account %s to %s.",
                    amount, fromId, toId));
        } catch (BalanceException | AccountException ae) {
            if (hold != null) {
                cancelHold(hold, "Transfer aborted.");
            }
            if (deposit != null) {
                operationRepository.delete(deposit);
            }
            if (withdraw != null) {
                operationRepository.delete(withdraw);
            }
            return Result.fail("Transfer operations aborted, check both accounts.");
        }
    }

    @Scheduled(fixedRate = "24h")
    public void validateOutdatedHolds() {
        List<Hold> all = holdRepository.findAll();
        all.stream()
                .filter(hold ->
                        hold.getHoldStatus() == Hold.HoldStatus.ACTIVE
                                && hold.lifeTimeIsMoreThanHours(24))
                .forEach(hold ->
                        cancelHold(hold, "Canceled by timeout"));
    }

    private void cancelHold(Hold hold, String reason) {
        hold.markAsCanceled(reason);
        holdRepository.save(hold);
    }

    private void completeHold(Hold hold, String reason) {
        hold.markAsClosed(reason);
        holdRepository.save(hold);
    }

    private Hold createHoldForAccount(int accountId, double amount, String reason) throws BalanceException, AccountException {
        if (getBalance(accountId).getCurrent() >= amount) {
            return holdRepository.save(new Hold(accountId, amount, reason));
        } else {
            throw new NotEnoughMoneyException(accountId);
        }
    }

    private void validateAmount(double amount) throws AmountShouldBePositiveException {
        if (amount <= 0) {
            throw new AmountShouldBePositiveException();
        }
    }
}

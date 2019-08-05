package com.vip.server.services;

import com.vip.server.domain.Balance;
import com.vip.server.domain.Hold;
import com.vip.server.domain.Operation;
import com.vip.server.domain.ui.ResultUI;
import com.vip.server.exceptions.account.AbstractAccountException;
import com.vip.server.exceptions.account.OperationCantBePerformedOnTheSameAccount;
import com.vip.server.exceptions.balance.AbstractBalanceException;
import com.vip.server.exceptions.balance.AmountShouldBePositiveException;
import com.vip.server.exceptions.balance.NotEnoughMoneyForOperationException;
import com.vip.server.repositories.HoldRepository;
import com.vip.server.repositories.OperationRepository;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.vip.server.domain.Operation.OperationType.DEPOSIT;
import static com.vip.server.domain.Operation.OperationType.WITHDRAW;

@Singleton
public class BalanceServiceImpl implements BalanceService {
    private final static Logger logger = LoggerFactory.getLogger(BalanceService.class);

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
    public boolean addMoneyToAccount(int accountId, BigDecimal amount, String reason) throws AbstractBalanceException, AbstractAccountException {
        checkAmountIsPositive(amount);
        accountService.checkIsAccountReadyForPayments(accountId);
        operationRepository.save(Operation.deposit(accountId, amount, reason));
        return true;
    }

    @Override
    public Balance getBalance(int accountId) throws AbstractAccountException {
        accountService.checkAccountIsActive(accountId);
        return new Balance(accountId,
                operationRepository.getSumByOperationAndAccountId(DEPOSIT, accountId),
                operationRepository.getSumByOperationAndAccountId(WITHDRAW, accountId),
                holdRepository.countHoldSumByAccountId(accountId)
        );
    }

    @Override
    public synchronized ResultUI transferMoneyFromAccountTo(int sourceAccountId, int destinationAccountId, BigDecimal amount) throws AbstractAccountException, AmountShouldBePositiveException {
        checkAmountIsPositive(amount);
        final String reason = String.format("Transfer amount[%s] between accounts: from [%s] to [%s] .",
                amount.toString(), sourceAccountId, destinationAccountId);
        logger.info(reason);
        validateReadyForTransferAccountsById(sourceAccountId, destinationAccountId);
        Hold holdFromSource = new Hold(sourceAccountId, amount, reason);
        Operation depositToAccountDestination = Operation.deposit(destinationAccountId, amount, holdFromSource.getOpeningReason());
        Operation withdrawFromAccountSource = Operation.withdraw(sourceAccountId, amount, holdFromSource.getOpeningReason());

        accountService.lockAccountById(sourceAccountId);
        try {
            holdFromSource = holdRepository.save(holdFromSource);
            checkEnoughMoneyOnAccountForOperation(sourceAccountId, amount);
            depositToAccountDestination = operationRepository.save(depositToAccountDestination);
            withdrawFromAccountSource = operationRepository.save(withdrawFromAccountSource);
            completeHold(holdFromSource, "Transfer complete.");
            return ResultUI.success(String.format("Transfer complete: $%s from account %s to %s.",
                    amount, sourceAccountId, destinationAccountId));
        } catch (AbstractAccountException | NotEnoughMoneyForOperationException e) {
            rollbackOperationsByReason(holdFromSource, depositToAccountDestination, withdrawFromAccountSource, e);
            return ResultUI.fail(e.getMessage());
        } finally {
            accountService.unlockAccountById(sourceAccountId);
        }
    }

    private void rollbackOperationsByReason(Hold holdFromSource, Operation depositToAccountDestination, Operation withdrawFromAccountSource, Throwable e) {
        logger.error("Can't transfer between accounts [" + withdrawFromAccountSource.getAccountId() + "] -> " +
                "[" + depositToAccountDestination.getAccountId() + "].", e);
        operationRepository.delete(depositToAccountDestination);
        operationRepository.delete(withdrawFromAccountSource);
        cancelHold(holdFromSource, "Transfer is aborted.");
    }

    @Scheduled(fixedRate = "24h")
    public void closeOutdatedHolds() {
        logger.debug("Close outdated holds initiate.");
        final List<Hold> outdated = holdRepository.findAll().stream()
                .filter(hold -> hold.isActive() && hold.lifeTimeIsMoreThanHours(24))
                .collect(Collectors.toList());
        logger.debug("Outdated holds found: " + outdated.size());
        outdated.forEach(hold -> cancelHold(hold, "Canceled by timeout"));
    }

    private void cancelHold(Hold hold, String reason) {
        logger.debug("Hold [" + hold.getId() + "] canceled because: " + reason);
        hold.markAsCanceled(reason);
        holdRepository.save(hold);
    }

    private void completeHold(Hold hold, String reason) {
        logger.debug("Hold [" + hold.getId() + "] complete because: " + reason);
        hold.markAsComplete(reason);
        holdRepository.save(hold);
    }

    private void checkEnoughMoneyOnAccountForOperation(int accountId, BigDecimal expectedAmount) throws AbstractAccountException, NotEnoughMoneyForOperationException {
        if (getBalance(accountId).getCurrent().compareTo(expectedAmount) < 0) {
            throw new NotEnoughMoneyForOperationException(accountId);
        }
    }

    private void checkAmountIsPositive(BigDecimal amount) throws AmountShouldBePositiveException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AmountShouldBePositiveException();
        }
    }

    private void validateReadyForTransferAccountsById(int fromAccountById, int toAccountById) throws OperationCantBePerformedOnTheSameAccount, AbstractAccountException {
        if (fromAccountById == toAccountById) {
            throw new OperationCantBePerformedOnTheSameAccount();
        }
        accountService.checkIsAccountReadyForPayments(fromAccountById);
        accountService.checkIsAccountReadyForPayments(toAccountById);
    }
}

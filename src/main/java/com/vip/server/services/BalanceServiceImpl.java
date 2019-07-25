package com.vip.server.services;

import com.vip.server.domain.Balance;
import com.vip.server.domain.Hold;
import com.vip.server.domain.Operation;
import com.vip.server.domain.ui.ResultUI;
import com.vip.server.exceptions.account.AbstractAccountException;
import com.vip.server.exceptions.balance.AbstractBalanceException;
import com.vip.server.exceptions.balance.AmountShouldBePositiveException;
import com.vip.server.exceptions.balance.NotEnoughMoneyForOperationException;
import com.vip.server.exceptions.balance.OperationCantBePerformedOnTheSameAccount;
import com.vip.server.repositories.HoldRepository;
import com.vip.server.repositories.OperationRepository;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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
        final Function<Integer, Function<OperationRepository, Function<HoldRepository, Balance>>> createBalance =
                id -> opRep -> holdRep -> new Balance(id,
                        opRep.getSumByOperationAndAccountId(DEPOSIT, id),
                        opRep.getSumByOperationAndAccountId(WITHDRAW, id),
                        holdRep.countHoldSumByAccountId(id));
        return createBalance.apply(accountId).apply(operationRepository).apply(holdRepository);
    }

    @Override
    public ResultUI transferMoneyFromAccountTo(int fromAccountById, int toAccountById, BigDecimal amount) throws AbstractBalanceException, AbstractAccountException {
        logger.debug(
                String.format("Transfer from account [%s] to account [%s] for amount [%s] started.",
                        fromAccountById, toAccountById, amount));
        if (fromAccountById == toAccountById) {
            throw new OperationCantBePerformedOnTheSameAccount();
        }
        checkAmountIsPositive(amount);
        accountService.checkIsAccountReadyForPayments(fromAccountById);
        accountService.checkIsAccountReadyForPayments(toAccountById);

        Optional<Hold> holdOptional = Optional.empty();
        Optional<Operation> depositOptional = Optional.empty();
        Optional<Operation> withdrawOptional = Optional.empty();
        try {
            final String reason = String.format("Transfer amount[%s] between accounts: from [%s] to [%s] .",
                    amount.toString(), fromAccountById, toAccountById);
            holdOptional = Optional.of(createHoldForAccount(fromAccountById, amount, reason));
            accountService.checkIsAccountReadyForPayments(toAccountById);
            depositOptional = Optional.of(operationRepository.save(Operation.deposit(toAccountById, amount, reason)));
            withdrawOptional = Optional.of(operationRepository.save(Operation.withdraw(fromAccountById, amount, reason)));
            completeHold(holdOptional.get(), "Transfer complete.");
            logger.debug("Transfer complete.");
            return ResultUI.success(String.format("Transfer complete: $%s from account %s to %s.",
                    amount, fromAccountById, toAccountById));
        } catch (AbstractBalanceException |
                AbstractAccountException ae) {
            logger.error("Error during transfer.", ae);
            holdOptional.ifPresent(hold -> cancelHold(hold, "Transfer aborted because: " + ae.getMessage()));
            depositOptional.ifPresent(operationRepository::delete);
            withdrawOptional.ifPresent(operationRepository::delete);
            return ResultUI.fail("Transfer operations aborted, check please accounts.");
        }
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

    private Hold createHoldForAccount(int accountId, BigDecimal amount, String reason) throws AbstractBalanceException, AbstractAccountException {
        logger.debug(String.format("Create hold for account[%s] on amount[%s] because: %s",
                accountId, amount.toString(), reason));
        try {
            accountService.lockAccountById(accountId);
            if (getBalance(accountId).getCurrent().compareTo(amount) >= 0) {
                final Hold hold = holdRepository.save(new Hold(accountId, amount, reason));
                if (getBalance(accountId).getCurrent().compareTo(BigDecimal.ZERO) < 0) {
                    cancelHold(hold, "Not enough money after hold.");
                    throw new NotEnoughMoneyForOperationException(accountId);
                }
                return hold;
            } else {
                logger.debug("Cancel creation because not enough money.");
                throw new NotEnoughMoneyForOperationException(accountId);
            }
        } finally {
            accountService.unlockAccountById(accountId);
        }
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

    private void checkAmountIsPositive(BigDecimal amount) throws AmountShouldBePositiveException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AmountShouldBePositiveException();
        }
    }
}

package com.vip.server.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Hold extends AbstractEntityWithId<Integer> {
    public enum HoldStatus {
        ACTIVE, DONE, CANCELED
    }

    private final LocalDateTime openingTime;
    private final String openingReason;
    private final int accountId;
    private final BigDecimal amount;
    private HoldStatus holdStatus;
    private LocalDateTime closingTime;
    private String closingReason;

    public Hold(int accountId, BigDecimal amount, String openingReason) {
        this(HoldStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), openingReason, "", accountId, amount);
    }

    public Hold(HoldStatus holdStatus, LocalDateTime openingTime, LocalDateTime closingTime, String openingReason,
                String closingReason, int accountId, BigDecimal amount) {
        this.holdStatus = holdStatus;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.openingReason = openingReason;
        this.closingReason = closingReason;
        this.accountId = accountId;
        this.amount = amount;
    }

    public void markAsComplete(String closingReason) {
        this.holdStatus = HoldStatus.DONE;
        this.closingTime = LocalDateTime.now();
        this.closingReason = closingReason;
    }

    public void markAsCanceled(String cancelReason) {
        this.holdStatus = HoldStatus.CANCELED;
        this.closingTime = LocalDateTime.now();
        this.closingReason = cancelReason;
    }

    public Hold.HoldStatus getHoldStatus() {
        return holdStatus;
    }

    public boolean lifeTimeIsMoreThanHours(long hours) {
        return LocalDateTime.from(openingTime).until(LocalDateTime.now(), ChronoUnit.HOURS) > hours;
    }

    public boolean isActive() {
        return holdStatus == HoldStatus.ACTIVE;
    }

    public String getOpeningReason() {
        return openingReason;
    }

    public int getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}

package com.vip.server.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Hold extends AbstractId<Integer> {
    public enum HoldStatus {
        ACTIVE, DONE, CANCELED
    }

    private HoldStatus holdStatus;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
    private String openingReason;
    private String closingReason;
    private int accountId;
    private double amount;

    public Hold(int accountId, double amount, String openingReason) {
        this.holdStatus = HoldStatus.ACTIVE;
        this.openingTime = LocalDateTime.now();
        this.closingTime = openingTime;
        this.accountId = accountId;
        this.amount = amount;
        this.openingReason = openingReason;
        this.closingReason = "";
    }

    public Hold(HoldStatus holdStatus, LocalDateTime openingTime, LocalDateTime closingTime, String openingReason,
                String closingReason, int accountId, double amount) {
        this.holdStatus = holdStatus;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.openingReason = openingReason;
        this.closingReason = closingReason;
        this.accountId = accountId;
        this.amount = amount;
    }

    public void markAsClosed(String closingReason) {
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

    public LocalDateTime getOpeningTime() {
        return openingTime;
    }

    public LocalDateTime getClosingTime() {
        return closingTime;
    }

    public String getOpeningReason() {
        return openingReason;
    }

    public String getClosingReason() {
        return closingReason;
    }

    public int getAccountId() {
        return accountId;
    }

    public double getAmount() {
        return amount;
    }
}

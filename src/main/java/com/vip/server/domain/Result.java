package com.vip.server.domain;

public class Result {
    private boolean isSuccess;
    private String action;
    private String error;

    private Result() {}

    private Result(boolean isSuccess, String action, String error) {
        this.isSuccess = isSuccess;
        this.error = error;
        this.action = action;
    }

    public static Result success(String action) {
        return new Result(true, action, null);
    }

    public static Result fail(String error) {
        return new Result(false, null, error);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getAction() {
        return action;
    }

    public String getError() {
        return error;
    }
}

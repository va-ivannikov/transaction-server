package com.vip.server.domain.ui;

public class ResultUI {
    private boolean isSuccess;
    private String action;
    private String error;

    private ResultUI() {}

    private ResultUI(boolean isSuccess, String action, String error) {
        this.isSuccess = isSuccess;
        this.error = error;
        this.action = action;
    }

    public static ResultUI success(String action) {
        return new ResultUI(true, action, null);
    }

    public static ResultUI fail(String error) {
        return new ResultUI(false, null, error);
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

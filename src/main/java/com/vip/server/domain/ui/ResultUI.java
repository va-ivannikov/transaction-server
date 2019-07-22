package com.vip.server.domain.ui;

public class ResultUI {
    private boolean success;
    private String action;
    private String error;

    private ResultUI() {}

    private ResultUI(boolean success, String action, String error) {
        this.success = success;
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
        return success;
    }

    public String getAction() {
        return action;
    }

    public String getError() {
        return error;
    }
}

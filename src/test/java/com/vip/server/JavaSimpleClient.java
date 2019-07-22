package com.vip.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vip.server.domain.ui.AccountUI;
import com.vip.server.domain.ui.BalanceUI;
import com.vip.server.domain.ui.ResultUI;
import com.vip.server.domain.ui.TransactionRequestUI;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JavaSimpleClient {
    private ObjectMapper objectMapper = new ObjectMapper();
    private String host;
    private HttpClient httpClient;

    public JavaSimpleClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public void setHost(URI uri) {
        this.host = uri.toString();
    }

    public AccountUI createAccount(String testEmail) throws IOException, InterruptedException {
        URI accounts = URI.create(host + "/accounts");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Accept", "application/json")
                .uri(accounts)
                .PUT(HttpRequest.BodyPublishers.ofString(testEmail))
                .build();
        HttpResponse response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body().toString(), AccountUI.class);
    }

    public BalanceUI getBalanceForAccount(int accountId) throws IOException, InterruptedException {
        URI getBalance = URI.create(host + "/accounts/" + accountId + "/money");
        HttpRequest getAccountBalance = HttpRequest.newBuilder()
                .header("Accept", "application/json")
                .uri(getBalance)
                .GET()
                .build();
        HttpResponse getBalanceResponse = httpClient.send(getAccountBalance, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(getBalanceResponse.body().toString(), BalanceUI.class);
    }

    public BalanceUI addMoneyToAccount(int accountId, double amount) throws IOException, InterruptedException {
        URI addMoney = URI.create(host + "/accounts/" + accountId + "/money");
        HttpRequest addMoneyRequest = HttpRequest.newBuilder()
                .header("Accept", "application/json")
                .uri(addMoney)
                .PUT(HttpRequest.BodyPublishers.ofString(String.valueOf(amount)))
                .build();
        HttpResponse getBalanceResponse = httpClient.send(addMoneyRequest, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(getBalanceResponse.body().toString(), BalanceUI.class);
    }

    public ResultUI transfer(int fromAccountById, int toAccountById, double amount) throws IOException, InterruptedException {
        URI addMoney = URI.create(host + "/transaction");
        TransactionRequestUI transactionRequestUI = new TransactionRequestUI(fromAccountById, toAccountById, BigDecimal.valueOf(amount));
        HttpRequest addMoneyRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(addMoney)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(transactionRequestUI)))
                .build();
        HttpResponse getBalanceResponse = httpClient.send(addMoneyRequest, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(getBalanceResponse.body().toString(), ResultUI.class);
    }
}

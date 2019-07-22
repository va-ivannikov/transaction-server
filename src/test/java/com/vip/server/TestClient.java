package com.vip.server;

import com.vip.server.domain.ui.AccountUI;
import com.vip.server.domain.ui.BalanceUI;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Single;

@Client("/")
public interface TestClient extends HttpClient {

    @Put("/accounts")
    Single<AccountUI> createAccount(String email);

    @Put("/accounts/{accountId}/money")
    Single<BalanceUI> addMoneyToAccount(int accountId, double amount);

    @Get("/accounts/{accountId}/money")
    Single<BalanceUI> getAccountBalance(int accountId);

    @Get("/accounts/{accountId}")
    Single<AccountUI> getAccount(int accountId);
}

package com.vip.server;

import com.vip.server.domain.Account;
import com.vip.server.domain.Balance;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Single;

@Client("/")
public interface TestClient {

    @Put("/accounts")
    Single<Account> createAccount(String email);

    @Put("/accounts/{accountId}/money")
    Single<Balance> addMoneyToAccount(int accountId, double amount);

    @Get("/accounts/{accountId}/money")
    Single<Balance> getAccountBalance(int accountId);

    @Get("/accounts/{accountId}")
    Single<Account> getAccount(int accountId);
}

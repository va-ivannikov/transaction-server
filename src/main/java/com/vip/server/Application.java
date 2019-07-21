package com.vip.server;

import io.micronaut.runtime.Micronaut;

public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}

//curl -X PUT localhost:8080/accounts -H 'Content-Type: application/json' -d '{"email":"Test.email@com"}'
//curl -X PUT localhost:8080/accounts/1/money -H 'Content-Type: application/json' -d '{"amount": 100}'
//curl -X GET localhost:8080/accounts/1/money -H 'Content-Type: application/json'
//curl -X POST localhost:8080/transaction -H 'Content-Type: application/json' -d '{"fromAccountId": 1, "toAccountId": 2, "amount": 50}'
//curl -X GET localhost:8080/accounts/1/money -H 'Content-Type: application/json'
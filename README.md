# transaction-server
Transaction server example. Micronaut used.

For test:

curl -X PUT localhost:8080/accounts -H 'Content-Type: application/json' -d '{"email":"Test1.email@com"}'

curl -X PUT localhost:8080/accounts -H 'Content-Type: application/json' -d '{"email":"Test2.email@com"}'

curl -X PUT localhost:8080/accounts/1/money -H 'Content-Type: application/json' -d '{"amount": 100}'

curl -X GET localhost:8080/accounts/1/money

curl -X POST localhost:8080/transaction -H 'Content-Type: application/json' -d '{"fromAccountId": 1, "toAccountId": 2, "amount": 50}'

curl -X GET localhost:8080/accounts/1/money

curl -X GET localhost:8080/accounts/2/money

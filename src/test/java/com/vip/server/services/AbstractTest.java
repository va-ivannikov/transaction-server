package com.vip.server.services;

import java.util.UUID;

abstract class AbstractTest {
    String getRandomEmail() {
        return UUID.randomUUID().toString() + "@gmail.com";
    }
}

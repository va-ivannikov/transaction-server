package com.vip.server.controllers;

import com.vip.server.exceptions.account.AccountNotFoundException;
import com.vip.server.exceptions.ui.AccountNotExistsOrNotEnoughRightsException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.hateoas.JsonError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ExceptionController {
    private static final Logger logger = LoggerFactory.getLogger("Controller");

    public ExceptionController() {
    }

    @Error(global = true, exception = AccountNotFoundException.class)
    public HttpResponse<JsonError> catchNotFoundAccount(HttpRequest request, AccountNotFoundException thr) throws AccountNotExistsOrNotEnoughRightsException {
        throw new AccountNotExistsOrNotEnoughRightsException();
    }

    @Error(global = true)
    public HttpResponse<JsonError> error(HttpRequest request, Throwable thr) {
        logger.error("Error on request " + request.getPath(), thr);
        JsonError error = new JsonError("Error: " + thr.getMessage());
        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }
}
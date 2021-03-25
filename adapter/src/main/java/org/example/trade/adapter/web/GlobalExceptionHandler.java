package org.example.trade.adapter.web;

import org.example.trade.adapter.interfaces.translator.AccountIdTranslator;
import org.example.trade.infrastructure.AccountNotSupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String customHandler1(IllegalArgumentException e) {
        return e.getMessage();
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String customHandler2(NoSuchElementException e) {
        return e.getMessage();
    }

    @ExceptionHandler(AccountNotSupportedException.class)
    @ResponseStatus(code = HttpStatus.NOT_IMPLEMENTED)
    @ResponseBody
    public String customHandler2(AccountNotSupportedException e) {
        return "账户暂未支持: " + AccountIdTranslator.from(e.account());
    }
}
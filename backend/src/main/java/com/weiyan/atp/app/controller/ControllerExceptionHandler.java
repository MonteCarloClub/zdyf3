package com.weiyan.atp.app.controller;

import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.data.bean.Result;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;


@RestControllerAdvice
@Slf4j
@CrossOrigin //支持跨域访问
public class ControllerExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public Object handleException(BaseException ex) {
        log.warn("BaseException handler catched.", ex);
        return Result.failWithMessage(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<String> exceptionHandler(Exception ex) {
        log.error(ex.toString(), ex);
        return Result.internalError("Unknown Error Occurred!");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<String> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("request type not supported", ex);
        return Result.failWithMessage(-1, "Request method not supported");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("constraintViolationException", ex);
        return Result.failWithMessage(500, ex.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public Result<String> handleNullPointerException(NullPointerException ex) {
        log.warn("NullPointerException", ex);
        return Result.failWithMessage(500, ex.getMessage());
    }
}

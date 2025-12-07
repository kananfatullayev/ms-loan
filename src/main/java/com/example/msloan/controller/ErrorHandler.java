package com.example.msloan.controller;

import com.example.msloan.model.exception.ExceptionDto;
import com.example.msloan.model.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ExceptionDto handleException(Exception ex) {
        log.error("ActionLog.handleException.error# {}", ex.getMessage());
        return new ExceptionDto(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ExceptionDto handleNotFoundException(NotFoundException ex) {
        log.error("ActionLog.handleNotFoundException.error# {}", ex.getMessage());
        return new ExceptionDto(ex.getMessage());
    }
}


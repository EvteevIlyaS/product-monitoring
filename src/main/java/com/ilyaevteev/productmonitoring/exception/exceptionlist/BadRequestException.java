package com.ilyaevteev.productmonitoring.exception.exceptionlist;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String msg) {
        super(msg);
    }
}

package com.ilyaevteev.productmonitoring.exception.exceptionlist;

public class FailedDependencyException extends RuntimeException{
    public FailedDependencyException(String msg) {
        super(msg);
    }

}

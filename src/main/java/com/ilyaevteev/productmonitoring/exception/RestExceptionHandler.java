package com.ilyaevteev.productmonitoring.exception;

import com.ilyaevteev.productmonitoring.exception.exceptionlist.BadRequestException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.FailedDependencyException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.NotFoundException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.UnsupportedMediaTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse errorHandlerBadRequest(BadRequestException exception) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                new Date()
        );
    }

    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse errorHandlerNotFound(NotFoundException exception) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                new Date()
        );
    }

    @ExceptionHandler(value = {UnsupportedMediaTypeException.class})
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    protected ErrorResponse errorHandlerUnsupportedMediaType(UnsupportedMediaTypeException exception) {
        return new ErrorResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                exception.getMessage(),
                new Date()
        );
    }

    @ExceptionHandler(value = {FailedDependencyException.class})
    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
    protected ErrorResponse errorHandlerFailedDependency(FailedDependencyException exception) {
        return new ErrorResponse(
                HttpStatus.FAILED_DEPENDENCY.value(),
                exception.getMessage(),
                new Date()
        );
    }
}

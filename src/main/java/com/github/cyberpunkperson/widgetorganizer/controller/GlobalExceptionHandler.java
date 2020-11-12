package com.github.cyberpunkperson.widgetorganizer.controller;

import com.github.cyberpunkperson.widgetorganizer.controller.dto.ApiException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiException> handleUndefinedException(Exception exception, WebRequest request) {
        return handleExceptionInternal(
                exception,
                new ApiException(exception.getMessage()),
                new HttpHeaders(),
                INTERNAL_SERVER_ERROR,
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        BindingResult bindingResult = exception.getBindingResult();
        StringBuilder fieldErrorBuilder = new StringBuilder();
        bindingResult.getFieldErrors().forEach(fieldError -> fieldErrorBuilder.append(fieldError.getDefaultMessage()));

        return super.handleExceptionInternal(
                exception,
                new ApiException(fieldErrorBuilder.toString()),
                headers,
                BAD_REQUEST,
                request);
    }

    private ResponseEntity<ApiException> handleExceptionInternal(@NonNull Exception exception,
                                                                 @Nullable ApiException body,
                                                                 HttpHeaders headers,
                                                                 @NonNull HttpStatus status,
                                                                 @NonNull WebRequest request) {

        if (INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, exception, WebRequest.SCOPE_REQUEST);
        }
        exception.printStackTrace();
        return new ResponseEntity<>(body, headers, status);
    }

}

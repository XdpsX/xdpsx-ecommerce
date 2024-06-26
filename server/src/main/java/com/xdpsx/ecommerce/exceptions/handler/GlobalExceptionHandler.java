package com.xdpsx.ecommerce.exceptions.handler;

import com.xdpsx.ecommerce.dtos.error.ErrorDetails;
import com.xdpsx.ecommerce.exceptions.BadRequestException;
import com.xdpsx.ecommerce.exceptions.ResourceNotFoundException;
import com.xdpsx.ecommerce.exceptions.TokenException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDetails handleBadCredentials(HttpServletRequest request, BadCredentialsException ex){
        log.error(ex.getMessage(), ex);

        ErrorDetails errorDetails = new ErrorDetails("Wrong email or password!");
        errorDetails.setTimestamp(new Date());
        errorDetails.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorDetails.setPath(request.getServletPath());

        return errorDetails;
    }

    @ExceptionHandler(TokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDetails handleTokenException(HttpServletRequest request, TokenException e){
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage());
        errorDetails.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorDetails.setPath(request.getServletPath());
        return errorDetails;
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleMissingServletRequestPart(HttpServletRequest request, MissingServletRequestPartException e) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails("Parameter " + e.getRequestPartName()  + " is missing in the request");
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetails.setPath(request.getServletPath());
        return errorDetails;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleMaxUploadSizeExceeded(HttpServletRequest request, MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails("File size limit exceeded. Please upload a file with a smaller size.");
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetails.setPath(request.getServletPath());
        return errorDetails;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDetails handleResourceNotFound(HttpServletRequest request, ResourceNotFoundException e) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage());
        errorDetails.setStatus(HttpStatus.NOT_FOUND.value());
        errorDetails.setPath(request.getServletPath());
        return errorDetails;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleBadRequest(HttpServletRequest request, BadRequestException e){
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage());
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetails.setPath(request.getServletPath());
        return errorDetails;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleConstraintViolation(HttpServletRequest request, ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails("Validation Error");
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetails.setPath(request.getServletPath());

        Set<ConstraintViolation<?>> errors = e.getConstraintViolations();
        Map<String, String> details = new HashMap();
        errors.forEach(error -> {
            String[] splitted = error.getPropertyPath().toString().split("\\.");
            String fieldError = splitted[splitted.length - 1];
            details.put(fieldError, error.getMessage());
        });
        errorDetails.setDetails(details);

        return errorDetails;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleMethodArgumentNotValid(HttpServletRequest request, MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails("Validation Error");
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetails.setPath(request.getServletPath());

        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        errorDetails.setDetails(errors.stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
        return errorDetails;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDetails handleException(HttpServletRequest request, Exception e) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails("Internal Server Error");
        errorDetails.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.setPath(request.getServletPath());
        return errorDetails;
    }
}

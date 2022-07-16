/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.family.auth.core;

import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * This exceptions handler will only convert all exceptions that thrown by API to {@link ApiResult ApiResult}.
 * The exceptions logging or notifying to system maintain developers is delegating to {@link ExceptionNotifier ExceptionNotifier}.
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    private final ExceptionNotifier exceptionNotifier;

    public ApiExceptionHandler(ExceptionNotifier exceptionNotifier) {
        this.exceptionNotifier = exceptionNotifier;
    }

    @ExceptionHandler(ApiResultException.class)
    public ApiResult handleApiWidgetsApiResultException(ApiResultException ex) {
        ApiResult apiResult = ex.getApiResult();


        return apiResult;
    }

    @ExceptionHandler(RestClientException.class)
    public ApiResult handleRestClientException(HttpServletRequest request, RestClientException ex) {
        String message = String.format("RESTful API calling failure: %s", ex.getMessage());

        ApiResult apiResult = ApiResultFactory.failure(message);


        exceptionNotifier.notify(request, ex, message);

        return apiResult;
    }


    @ExceptionHandler(Exception.class)
    public ApiResult handleAllExceptions(HttpServletRequest request, Exception ex) {
        // prompt the root exception message
        String message = ex.getMessage();
        Throwable cause = ex.getCause();
        while (cause != null) {
            message = cause.getMessage();
            cause = cause.getCause();
        }

        ApiResult apiResult = ApiResultFactory.failure(String.format("请联系管理员, 系统未处理异常: %s", message));

        exceptionNotifier.notify(request, ex, message);

        return apiResult;
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResult handleDuplicateKeyException(HttpServletRequest request, DuplicateKeyException ex) {
        String promptMessage = "数据已经存在";

        String message = ex.getMessage();
        Throwable cause = ex.getCause();
        while (cause != null && cause.getCause() != null) {
            message = cause.getMessage();
            cause = cause.getCause();
        }

        ApiResult apiResult = ApiResultFactory.badRequest(promptMessage);

        exceptionNotifier.notify(request, cause, message);

        return apiResult;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException ex) {
        ApiResult apiResult = ApiResultFactory.badRequest(ex.getMessage());

        exceptionNotifier.notify(request, ex);

        return apiResult;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult handleMethodArgumentNotValid(HttpServletRequest request, MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            sb.append(error.getField()).append(": ").append(error.getDefaultMessage());
            sb.append(";");
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            sb.append(error.getObjectName()).append(": ").append(error.getDefaultMessage());
            sb.append(";");
        }

        ApiResult apiResult = ApiResultFactory.badRequest(sb.toString());

        exceptionNotifier.notify(request, ex, sb.toString());

        return apiResult;
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ApiResult handleHttpMediaTypeNotSupported(HttpServletRequest request, HttpMediaTypeNotSupportedException ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getContentType());
        sb.append(" media type is not supported. Supported media types are ");

        ex.getSupportedMediaTypes().forEach(t -> sb.append(t).append(", "));

        ApiResult apiResult =  ApiResultFactory.badRequest(sb.toString());

        exceptionNotifier.notify(request, ex, sb.toString());

        return apiResult;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResult handleMissingServletRequestParameterException(HttpServletRequest request, MissingServletRequestParameterException ex) {
        String message = String.format("缺少必要参数: %s, 参数类型: %s", ex.getParameterName(), ex.getParameterType());

        ApiResult apiResult =  ApiResultFactory.badRequest(message);

        exceptionNotifier.notify(request, ex, message);

        return apiResult;
    }

    @ExceptionHandler(JsonParseException.class)
    public ApiResult handleJsonParseException(HttpServletRequest request, JsonParseException ex) {
        String message = String.format("请求Body不是有效Json数据, 更多信息:%s", ex.getMessage());

        ApiResult apiResult =  ApiResultFactory.badRequest(message);

        exceptionNotifier.notify(request, ex, message);

        return apiResult;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        String message;
        if (cause instanceof JsonParseException) {
            message = String.format("请求Body不是有效Json数据, 更多信息:%s", cause.getMessage());
        } else {
            message = String.format("请求Body不是有效数据, 更多信息:%s", ex.getMessage());
        }

        ApiResult apiResult =  ApiResultFactory.badRequest(message);

        exceptionNotifier.notify(request, ex, message);

        return apiResult;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult handleHttpRequestMethodNotSupported(HttpServletRequest request, HttpRequestMethodNotSupportedException ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getMethod());
        sb.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> sb.append(t).append(" "));
        ApiResult apiResult =  ApiResultFactory.badRequest(sb.toString());
        exceptionNotifier.notify(request, ex, sb.toString());
        return apiResult;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResult handleNoHandlerFoundException(HttpServletRequest request, NoHandlerFoundException ex) {
        String message = String.format("No handler found for '%s' , Url: %s", ex.getHttpMethod(), ex.getRequestURL());

        ApiResult apiResult =  ApiResultFactory.badRequest(message);

        exceptionNotifier.notify(request, ex, message);

        return apiResult;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResult handleMethodArgumentTypeMismatch(HttpServletRequest request, MethodArgumentTypeMismatchException ex) {
        String message = ex.getName() + " should be of type " + ex.getRequiredType().getName();

        ApiResult apiResult =  ApiResultFactory.badRequest(message);

        exceptionNotifier.notify(request, ex, message);

        return apiResult;
    }
}
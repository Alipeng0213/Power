/*
 * Copyright (c) 2019 yingtingxu(徐应庭). All rights reserved.
 */

package com.family.auth.security.core;

import com.family.auth.core.ApiResult;
import com.family.auth.core.ApiResultFactory;
import com.family.auth.core.ExceptionNotifier;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OAuth2ExceptionApiResultRenderer implements OAuth2ExceptionRenderer {
    private final ExceptionNotifier exceptionNotifier;
    private List<HttpMessageConverter<?>> messageConverters = getDefaultMessageConverters();

    public OAuth2ExceptionApiResultRenderer(ExceptionNotifier exceptionNotifier) {
        this.exceptionNotifier = exceptionNotifier;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Override
    public void handleHttpEntityResponse(HttpEntity<?> responseEntity, ServletWebRequest webRequest) throws Exception {
        if (responseEntity == null) {
            return;
        }
        HttpInputMessage inputMessage = createHttpInputMessage(webRequest);
        HttpOutputMessage outputMessage = createHttpOutputMessage(webRequest);
        if (responseEntity instanceof ResponseEntity && outputMessage instanceof ServerHttpResponse) {
            // we using ApiResult<T> to represent the issues, so we set HttpStatus.OK
            ((ServerHttpResponse) outputMessage).setStatusCode(HttpStatus.OK);
        }
        HttpHeaders entityHeaders = responseEntity.getHeaders();
        if (!entityHeaders.isEmpty()) {
            outputMessage.getHeaders().putAll(entityHeaders);
        }
        Object body = responseEntity.getBody();
        if (body != null) {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (body instanceof OAuth2Exception) {
                OAuth2Exception exception = (OAuth2Exception) body;
                // get the root OAuth2 exception
                while (exception.getCause() instanceof OAuth2Exception) {
                    exception = (OAuth2Exception) exception.getCause();
                }
                String summary = exception.getSummary();
                ApiResult apiResult = ApiResultFactory.unauthorized(summary);
                if (exception instanceof InvalidClientException) {
                    apiResult = ApiResultFactory.badClientId(summary);
                } else if (exception instanceof InvalidTokenException) {
                    apiResult = ApiResultFactory.InvalidToken("token错误："+summary);
                }
                writeWithMessageConverters(apiResult, inputMessage, outputMessage);
            } else {
                if (body instanceof Exception) {
                    Throwable cause = (Exception) body;
                    exceptionNotifier.notify(request, cause, String.format("OAuth2ExceptionApiResultRenderer cannot handle: %s", cause.getMessage()));
                }
                writeWithMessageConverters(body, inputMessage, outputMessage);
            }
        } else {
            // flush headers
            outputMessage.getBody();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void writeWithMessageConverters(Object returnValue, HttpInputMessage inputMessage, HttpOutputMessage outputMessage) throws IOException, HttpMediaTypeNotAcceptableException {
        List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
        if (acceptedMediaTypes.isEmpty()) {
            acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
        }
        MediaType.sortByQualityValue(acceptedMediaTypes);
        Class<?> returnValueType = returnValue.getClass();
        List<MediaType> allSupportedMediaTypes = new ArrayList<>();
        for (MediaType acceptedMediaType : acceptedMediaTypes) {
            for (HttpMessageConverter messageConverter : messageConverters) {
                if (messageConverter.canWrite(returnValueType, acceptedMediaType)) {
                    messageConverter.write(returnValue, acceptedMediaType, outputMessage);
                    return;
                }
            }
        }
        for (HttpMessageConverter messageConverter : messageConverters) {
            allSupportedMediaTypes.addAll(messageConverter.getSupportedMediaTypes());
        }
        throw new HttpMediaTypeNotAcceptableException(allSupportedMediaTypes);
    }

    private List<HttpMessageConverter<?>> getDefaultMessageConverters() {
        List<HttpMessageConverter<?>> result = new ArrayList<>();
        result.addAll(new RestTemplate().getMessageConverters());
        return result;
    }

    private HttpInputMessage createHttpInputMessage(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return new ServletServerHttpRequest(servletRequest);
    }

    private HttpOutputMessage createHttpOutputMessage(NativeWebRequest webRequest) {
        HttpServletResponse servletResponse = (HttpServletResponse) webRequest.getNativeResponse();
        return new ServletServerHttpResponse(servletResponse);
    }
}

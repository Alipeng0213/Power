package com.family.auth.core;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ApiResultProcessor implements HandlerMethodReturnValueHandler {
    private final HandlerMethodReturnValueHandler delegate;

    public ApiResultProcessor(HandlerMethodReturnValueHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnType.hasMethodAnnotation(ApiResultDisabled.class)) {
            delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else {
            ApiResult apiResult;
            if(returnValue == null) {
                if (returnType.getParameterType().equals(Void.TYPE)) {
                    apiResult = ApiResultFactory.succeed(null);
                } else {
                    apiResult = ApiResultFactory.notContent();
                }
            }  else if(returnValue instanceof ApiResult) {
                apiResult = (ApiResult) returnValue;
            } else {
                apiResult = ApiResultFactory.succeed(returnValue);
            }

            delegate.handleReturnValue(apiResult, returnType, mavContainer, webRequest);
        }
    }
}
package com.family.auth.core;

public class ApiResultFactory {
    public static <T> ApiResult succeed(T result) {
        return new ApiResult<>(20000, "OK", result);
    }

    public static ApiResult notContent() {
        return new ApiResult(20400, "not content");
    }

    public static ApiResult reentry(String message) {
        return new ApiResult(20500, message);
    }

    public static ApiResult redirect(String location) {
        return new ApiResult<>(30200, location, location);
    }

    public static ApiResult badRequest(String message) {
        return new ApiResult(40000, message);
    }

    public static ApiResult unauthorized(String message) {
        return new ApiResult(40100, message);
    }

    public static ApiResult notFound() {
        return new ApiResult(40400, "not found");
    }

    public static ApiResult failure(String message) {
        if (message == null || message.isEmpty()) {
            message = "system internal exception";
        }
        return new ApiResult(50000, message);
    }

    public static ApiResult InvalidToken(String message) {
        ApiResult result = new ApiResult(50100, message);
        result.setResult(message);
        return result;
    }

    public static ApiResult badClientId(String message) {
        return new ApiResult(50101, message);
    }
}
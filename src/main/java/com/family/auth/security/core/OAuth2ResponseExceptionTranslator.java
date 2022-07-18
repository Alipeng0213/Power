package com.family.auth.security.core;

import com.family.auth.core.ApiResult;
import com.family.auth.core.ApiResultFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;


@Configuration
public class OAuth2ResponseExceptionTranslator extends DefaultWebResponseExceptionTranslator {


    @Override
    public ResponseEntity translate(Exception e) throws Exception {

        ApiResult result;
        ResponseEntity<OAuth2Exception> translate = super.translate(e);
        HttpStatus statusCode = translate.getStatusCode();
        switch (statusCode) {
            case UNAUTHORIZED:
                result = ApiResultFactory.unauthorized(e.getMessage());
                break;
            default:
                result = ApiResultFactory.badRequest(e.getMessage());
        }
        return handleApiResult(result);
    }

    private ResponseEntity<ApiResult> handleApiResult(ApiResult result) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        ResponseEntity<ApiResult> response = new ResponseEntity<>(result, headers,
                HttpStatus.OK);
        return response;

    }

}

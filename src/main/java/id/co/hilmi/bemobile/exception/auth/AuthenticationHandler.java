package id.co.hilmi.bemobile.exception.auth;

import id.co.hilmi.bemobile.dto.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static id.co.hilmi.bemobile.constant.general.GeneralConstant.SOURCE_SYSTEM;

@ControllerAdvice
public class AuthenticationHandler  {
    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiResponse> handleInvalidTokenException(InvalidTokenException ex) {
        OAuth2Exception response = new OAuth2Exception(ex.getMessage(), ex);
        return new ResponseEntity<>(ApiResponse.builder()
                .sourceSystem(SOURCE_SYSTEM)
                .responseKey(HttpStatus.UNAUTHORIZED.toString())
                .message(ApiResponse.Message.builder()
                        .titleIdn(response.getMessage())
                        .descIdn(response.getMessage())
                        .titleEng(response.getMessage())
                        .descEng(response.getMessage())
                        .build())
                .build(), HttpStatus.UNAUTHORIZED);
    }
}

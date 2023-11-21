package id.co.hilmi.bemobile.exception.auth;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

public class InvalidTokenException extends OAuth2Exception {
    public InvalidTokenException(String msg, Throwable t) {
        super(msg, t);
    }
    @Override
    public String getOAuth2ErrorCode() {
        return "invalid_token";
    }
}

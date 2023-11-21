package id.co.hilmi.bemobile.exception.auth;

import id.co.hilmi.bemobile.dto.api.ApiResponse;
import id.co.hilmi.bemobile.dto.parameter.ErrorDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

import static id.co.hilmi.bemobile.constant.errorcode.ErrorCodeConstant.ALREADY_LOGIN_ERROR_CODE;
import static id.co.hilmi.bemobile.constant.general.GeneralConstant.SOURCE_SYSTEM;

@Slf4j
public class CustomResponseExceptionTranslator implements WebResponseExceptionTranslator {
    @Override
    public ResponseEntity<ApiResponse> translate(Exception e) throws Exception {
        if (e instanceof OAuth2Exception) {
            OAuth2Exception oAuth2Exception = (OAuth2Exception) e;
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .sourceSystem(SOURCE_SYSTEM)
                            .responseKey(ALREADY_LOGIN_ERROR_CODE)
                            .message(ApiResponse.Message.builder()
                                    .titleIdn(oAuth2Exception.getMessage())
                                    .descIdn(oAuth2Exception.getMessage())
                                    .titleEng(oAuth2Exception.getMessage())
                                    .descEng(oAuth2Exception.getMessage())
                                    .build())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );

        }
        log.info("return null");
        return null;
    }
}

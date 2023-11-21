package id.co.hilmi.bemobile.exception;

import id.co.hilmi.bemobile.dto.api.ApiResponse;
import id.co.hilmi.bemobile.exception.common.HilmiCommonExceptionHandler;
import id.co.hilmi.bemobile.exception.user.UserNotFoundException;
import id.co.hilmi.bemobile.exception.user.WrongPasswordException;
import id.co.hilmi.bemobile.service.parameter.ErrorCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static id.co.hilmi.bemobile.constant.errorcode.ErrorCodeConstant.USER_NOT_FOUND_ERROR_CODE;
import static id.co.hilmi.bemobile.constant.errorcode.ErrorCodeConstant.WRONG_PASSWORD_ERROR_CODE;
import static id.co.hilmi.bemobile.constant.general.GeneralConstant.SOURCE_SYSTEM;

@ControllerAdvice
@Slf4j
public class HilmiExceptionHandler extends HilmiCommonExceptionHandler {

    @Autowired
    ErrorCodeService errorCodeService;

    private ResponseEntity<Object> buildResponse(String code, String sourceSystem, HttpStatus status){
        try {
            var errorDetail = errorCodeService.getErrorCodeDto(code, sourceSystem);

            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .sourceSystem(sourceSystem)
                            .responseKey(code)
                            .message(ApiResponse.Message.builder()
                                    .titleIdn(errorDetail.getInMessage())
                                    .descIdn(errorDetail.getInMessage())
                                    .titleEng(errorDetail.getSourceSystem())
                                    .descEng(errorDetail.getEnMessage())
                                    .build())
                            .build(),
            status
            );

        }catch (Exception e){
            throw e;
        }
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> userNotFoundException(UserNotFoundException e) {
        log.info("Handling WrongChallangeCodeException");
        return buildResponse(USER_NOT_FOUND_ERROR_CODE
                ,SOURCE_SYSTEM, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<Object> wrongPasswordException(WrongPasswordException e) {
        log.info("Handling WrongChallangeCodeException");
        return buildResponse(WRONG_PASSWORD_ERROR_CODE
                ,SOURCE_SYSTEM, HttpStatus.BAD_REQUEST);
    }
}

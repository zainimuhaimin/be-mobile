package id.co.hilmi.bemobile.exception.common;

import id.co.hilmi.bemobile.dto.parameter.ErrorDetailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
@RestControllerAdvice
public class HilmiCommonExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(HilmiCommonExceptionHandler.class);

    public HilmiCommonExceptionHandler() {
    }

    @ExceptionHandler({CommonRestException.class})
    public ResponseEntity<? extends ErrorDetailDto> commonRestException(CommonRestException e) {
        log.info("Exception is commonRestException, message : {}", e.getMessage());
        return new ResponseEntity((ErrorDetailDto)e.getResponseEntity().getBody(), e.getResponseEntity().getStatusCode());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> exception(Exception e) {
        log.error("Exception is UNCAUGHT, details : ", e);
        ErrorDetailDto errorDetail = ErrorDetailDto.builder()
                .errorCode("0019").sourceSystem("Digital Banking")
                .enMessage("Please try again in a moment")
                .inMessage("Silakan coba beberapa saat lagi")
        .build();
        return new ResponseEntity(errorDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

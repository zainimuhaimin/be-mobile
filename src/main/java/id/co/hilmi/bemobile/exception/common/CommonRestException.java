package id.co.hilmi.bemobile.exception.common;

import id.co.hilmi.bemobile.dto.parameter.ErrorDetailDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

public class CommonRestException extends RestClientException {
    private static final long serialVersionUID = 525587716280227799L;
    protected ResponseEntity<? extends ErrorDetailDto> responseEntity;

    public CommonRestException(String message) {
        super(message);
    }

    public ResponseEntity<? extends ErrorDetailDto> getResponseEntity() {
        return this.responseEntity;
    }

    public void setResponseEntity(ResponseEntity<? extends ErrorDetailDto> responseEntity) {
        this.responseEntity = responseEntity;
    }
}

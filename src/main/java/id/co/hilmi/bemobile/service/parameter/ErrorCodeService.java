package id.co.hilmi.bemobile.service.parameter;

import id.co.hilmi.bemobile.dto.parameter.ErrorDetailDto;
import id.co.hilmi.bemobile.repositories.parameter.ErrorCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
public class ErrorCodeService {

    @Autowired
    private ErrorCodeRepository errorCodeRepository;

    public ErrorDetailDto getErrorCodeDto(String code, String sourceSystem){
        log.info("start get error code");

        ErrorDetailDto response = null;

        try {

        var responseDb = errorCodeRepository.getErrorDetail(code, sourceSystem);

        if (!ObjectUtils.isEmpty(responseDb)){
            ErrorDetailDto errorDetailDto = new ErrorDetailDto();
            errorDetailDto.setErrorCode(responseDb.getErrorCode());
            errorDetailDto.setEnMessage(responseDb.getEnMessage());
            errorDetailDto.setInMessage(responseDb.getInMessage());
            errorDetailDto.setSourceSystem(responseDb.getSourceSystem());
            response = errorDetailDto;
        }

        }catch (Exception e){
            log.info("error when get error code");
        }

        return response;
    }
}

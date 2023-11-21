package id.co.hilmi.bemobile.util.response;

import id.co.hilmi.bemobile.config.message.MessageResourceConfiguration;
import id.co.hilmi.bemobile.constant.locale.LocaleConstant;
import id.co.hilmi.bemobile.dto.api.ApiResponse;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Locale;

public class ResponseUtil {

    private static final MessageSource messageSource = (new MessageResourceConfiguration()).messageSource();

    private ResponseUtil() {
    }

    public static <T extends Serializable> ResponseEntity<Object> buildResponse(String responseKey, String messageKey, T data, HttpStatus httpStatus) {
        return new ResponseEntity(buildResponse(messageSource, responseKey, messageKey, data), httpStatus);
    }

    public static <T extends Serializable> ApiResponse buildResponse(MessageSource messageSource, String responseKey, String messageKey, T data) {
        return buildResponse(messageSource, responseKey, (String)messageKey, (Object[])null, (Serializable)data);
    }

    public static <T extends Serializable> ApiResponse buildResponse(MessageSource messageSource, String responseKey, String messageKey, Object[] messageData, T data) {
        return buildResponse(messageSource, responseKey, messageKey, messageData, data, "HILMI_ID");
    }

    public static <T extends Serializable> ApiResponse buildResponse(MessageSource messageSource, String responseKey, String messageKey, Object[] messageData, T data, String sourceError) {
        return ApiResponse.builder().responseKey(responseKey).message(getResponseMessage(messageKey, messageData, messageSource)).data(data).sourceSystem(sourceError).build();
    }

    public static ApiResponse.Message getResponseMessage(String messageKey, Object[] objects, MessageSource messageSource) {
        String key = messageKey.toLowerCase(Locale.ROOT);
        String titleKey = key.concat(".title");
        String titleDesc = key.concat(".desc");
        return ApiResponse.Message.builder()
                .titleEng(messageSource.getMessage(titleKey, objects, LocaleConstant.localeEN))
                .descEng(messageSource.getMessage(titleDesc, objects, LocaleConstant.localeEN))
                .titleIdn(messageSource.getMessage(titleKey, objects, LocaleConstant.localeID))
                .descIdn(messageSource.getMessage(titleDesc, objects, LocaleConstant.localeID))
                .build();
    }

}

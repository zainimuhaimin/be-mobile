package id.co.hilmi.bemobile.util.serializedeserialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public class SerializationUtil {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SerializationUtil.class);
    private static final String DEFAULT_API_TIMESTAMP_PATTERN = "dd-MM-yyyy HH:mm:ss";

    private SerializationUtil() {
    }

    private static void logErrorSerialization(Exception e) {
        log.error("exception on serialization {} {}", e.getClass().getName(), e);
    }

    private static ObjectMapper getSerializeDefaultMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"));
        return mapper;
    }

    private static String serialize(Object object, boolean writeWithPrettyPrinter) throws JsonProcessingException {
        return writeWithPrettyPrinter ? getSerializeDefaultMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object) : getSerializeDefaultMapper().writeValueAsString(object);
    }

    public static String serialize(Object object) throws JsonProcessingException {
        try {
            return serialize(object, false);
        } catch (Exception var2) {
            logErrorSerialization(var2);
            throw var2;
        }
    }

    public static String serializeReturnNullIfError(Object object) {
        try {
            return serialize(object);
        } catch (Exception var2) {
            logErrorSerialization(var2);
            return null;
        }
    }

    public static String serializeWithPrettyPrinter(Object object) throws JsonProcessingException {
        try {
            return serialize(object, true);
        } catch (Exception var2) {
            logErrorSerialization(var2);
            throw var2;
        }
    }

    public static String serializeWithPrettyPrinterReturnNullWhenError(Object object) {
        try {
            return serialize(object, true);
        } catch (Exception var2) {
            logErrorSerialization(var2);
            return null;
        }
    }
}

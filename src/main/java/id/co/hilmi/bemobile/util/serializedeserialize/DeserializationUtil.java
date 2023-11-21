package id.co.hilmi.bemobile.util.serializedeserialize;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DeserializationUtil {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DeserializationUtil.class);

    private DeserializationUtil() {
    }

    private static void logErrorDeserialization(Exception e) {
        log.error("exception on deserialization {} {}", e.getClass().getName(), e);
    }

    private static ObjectMapper getDeserializeDefaultMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        return mapper;
    }

    private static ObjectMapper getDeserializeDefaultMapper(boolean failOnUnknownProperties, PropertyNamingStrategy strategy) {
        ObjectMapper mapper = getDeserializeDefaultMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
        mapper.setPropertyNamingStrategy(strategy);
        return mapper;
    }

    private static <T> T deserialize(String jsonString, TypeReference<?> typeReference) throws IOException {
        return (T) getDeserializeDefaultMapper().readValue(jsonString, typeReference);
    }

    private static <T> T deserialize(String jsonString, Class<T> classTarget) throws IOException {
        return getDeserializeDefaultMapper().readValue(jsonString, classTarget);
    }

    public static <T> T deserializeReturnNullIfError(String jsonString, Class<T> classTarget) {
        try {
            return deserialize(jsonString, classTarget);
        } catch (Exception var3) {
            logErrorDeserialization(var3);
            return null;
        }
    }

    public static <T> T deserializeReturnNullIfError(String jsonString, TypeReference<?> classTarget) {
        try {
            return deserialize(jsonString, classTarget);
        } catch (Exception var3) {
            logErrorDeserialization(var3);
            return null;
        }
    }
}

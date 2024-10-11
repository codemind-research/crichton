package crichton.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtils {

    public static <T> T convertJsonStringToObject(String jsonString, Class<T> valueType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON string to object", e);
        }
    }


}

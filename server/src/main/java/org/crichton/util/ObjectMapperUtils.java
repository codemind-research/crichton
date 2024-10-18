package org.crichton.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;

public class ObjectMapperUtils {

    private ObjectMapperUtils() {
        throw new AssertionError();
    }

    public static <T> T convertJsonStringToObject(String jsonString, Class<T> valueType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON string to object", e);
        }
    }

    public static <T> T convertJsonFileToObject(String jsonFile, Class<T> valueType) {
        return convertJsonFileToObject(new File(jsonFile), valueType);
    }

    public static <T> T convertJsonFileToObject(File jsonFile, Class<T> valueType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonFile, valueType);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON string to object", e);
        }
    }

    public static String convertObjectToJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON string", e);
        }
    }


}

package org.crichton.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ObjectMapperUtils {

    private ObjectMapperUtils() {
        throw new AssertionError();
    }

    public static <T> List<T> convertJsonToList(InputStream inputStream, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<T> result = new ArrayList<>();

        try {

            JsonNode jsonNode = objectMapper.readTree(inputStream);

            // JSON이 배열인 경우
            if (jsonNode.isArray()) {
                // InputStream을 다시 읽기 위해 리셋하거나, 다시 받아야 함
                // 새로운 InputStream을 사용하여 배열을 처리
                result = objectMapper.readValue(jsonNode.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
            }
            // JSON이 단일 객체인 경우
            else {
                T obj = objectMapper.treeToValue(jsonNode, clazz);
                result.add(obj);  // 단일 객체를 리스트로 변환
            }

        }
        catch (IOException e) {}

        return result;
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

    public static <T> void saveObjectToJsonFile(T object, String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(new File(filePath), object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON string", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void saveObjectToJsonFile(T object, File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(file, object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON string", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

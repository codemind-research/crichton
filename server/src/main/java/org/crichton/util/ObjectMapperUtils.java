package org.crichton.util;

import com.fasterxml.jackson.core.JsonParser;
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

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private ObjectMapperUtils() {
        throw new AssertionError();
    }

    public static <T> List<T> convertJsonToList(InputStream inputStream, Class<T> clazz) {
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        List<T> result = new ArrayList<>();

        try {

            JsonNode jsonNode = mapper.readTree(inputStream);

            // JSON이 배열인 경우
            if (jsonNode.isArray()) {
                // InputStream을 다시 읽기 위해 리셋하거나, 다시 받아야 함
                // 새로운 InputStream을 사용하여 배열을 처리
                result = mapper.readValue(jsonNode.toString(), mapper.getTypeFactory().constructCollectionType(List.class, clazz));
            }
            // JSON이 단일 객체인 경우
            else {
                T obj = mapper.treeToValue(jsonNode, clazz);
                result.add(obj);  // 단일 객체를 리스트로 변환
            }

        }
        catch (IOException e) {}

        return result;
    }

    public static <T> T convertJsonStringToObject(String jsonString, Class<T> valueType) {
        try {
            return mapper.readValue(jsonString, valueType);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON string to object", e);
        }
    }

    public static <T> T convertJsonFileToObject(String jsonFile, Class<T> valueType) {
        return convertJsonFileToObject(new File(jsonFile), valueType);
    }

    public static <T> T convertJsonFileToObject(File jsonFile, Class<T> valueType) {
        try {
            return mapper.readValue(jsonFile, valueType);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON string to object", e);
        }
    }

    public static String convertObjectToJsonString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON string", e);
        }
    }

    public static <T> void saveObjectToJsonFile(T object, String filePath) {
        try {
            mapper.writeValue(new File(filePath), object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON string", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void saveObjectToJsonFile(T object, File file) {
        try {
            mapper.writeValue(file, object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON string", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

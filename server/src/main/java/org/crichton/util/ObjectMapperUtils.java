package org.crichton.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.crichton.util.functional.ValueModifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

    // JSON 파일의 특정 키에 대해 값을 수정하는 함수 (내부 객체까지 탐색)
    public static <T> void modifyJsonFile(String filePath, String key, ValueModifier<T> modifier, Class<T> valueType) throws IOException {
        // JSON 파일을 읽기
        modifyJsonFile(new File(filePath), key, modifier, valueType);
    }
    public static <T> void modifyJsonFile(Path jsonFilePath, String keyPath, ValueModifier<T> modifier, Class<T> valueType) throws IOException {
        // JSON 파일을 읽기
        modifyJsonFile(jsonFilePath.toFile(), keyPath, modifier, valueType);
    }

    public static <T> T convertValue(JsonNode jsonNode, Class<?> valueType) {
        return (T) mapper.convertValue(jsonNode, valueType);
    }

    public static <T> void modifyJsonFile(File jsonFile, String keyPath, ValueModifier<T> modifier, Class<T> valueType) throws IOException {
        // JSON 파일을 읽기
        JsonNode root = mapper.readTree(jsonFile);

        // JSON 구조 탐색하여 값 수정
        // 루트가 배열인 경우 배열 처리
        if (root.isArray()) {
            ArrayNode arrayNode = (ArrayNode) root;
            for (JsonNode element : arrayNode) {
                modifyJsonNode(element, keyPath.split("\\."), 0, modifier, valueType);
            }
        } else {
            // 중첩된 키 탐색 및 값 수정 (루트가 배열이 아닌 경우)
            modifyJsonNode(root, keyPath.split("\\."), 0, modifier, valueType);
        }

        // 수정된 내용을 다시 파일에 덮어쓰기
        mapper.writeValue(jsonFile, root);
    }

    // JsonNode에서 특정 키에 대해 값을 수정 (내부 객체 및 배열 포함)
    private static  <T> void modifyJsonNode(JsonNode node, String[] keyPath, int depth, ValueModifier<T> modifier, Class<T> valueType) throws IOException {

        if (node == null || keyPath.length == 0) {
            return;
        }

        String currentKey = keyPath[depth];
        JsonNode currentNode = node.get(currentKey);

        if (currentNode == null) {
            return;  // 현재 노드가 null이면 더 이상 탐색하지 않음
        }

        // 마지막 키에 도달했을 때 단순 값을 처리
        if (depth == keyPath.length - 1) {
            if (currentNode.isValueNode()) {  // TextNode, NumberNode 등의 단순 값 처리
                modifyKeyValue((ObjectNode) node, currentKey, modifier, valueType);
            }
            else if (currentNode.isArray()) {
                // 배열 처리
                ArrayNode arrayNode = (ArrayNode) currentNode;
                modifyArrayValues(arrayNode, modifier, valueType);
            }
        } else {
            // 중간 경로가 배열인 경우
            if (currentNode.isArray()) {
                ArrayNode arrayNode = (ArrayNode) currentNode;
                for (JsonNode element : arrayNode) {
                    modifyJsonNode(element, keyPath, depth + 1, modifier, valueType);  // 배열 요소에 대해 남은 경로 탐색
                }
            }
            // 중간 경로가 객체인 경우
            else if (currentNode.isObject()) {
                modifyJsonNode(currentNode, keyPath, depth + 1, modifier, valueType);  // 객체일 경우 재귀적 탐색
            }
        }

    }

    // ObjectNode에서 특정 key의 값을 수정하는 로직
    private static <T> void modifyKeyValue(ObjectNode objectNode, String keyPath, ValueModifier<T> modifier, Class<T> valueType) throws IOException {
        JsonNode currentNode = objectNode.get(keyPath);
        if (currentNode != null) {
            // 현재 값을 지정한 타입으로 변환
            T currentValue = mapper.convertValue(currentNode, valueType);

            // 인터페이스에서 제공된 로직으로 값 수정
            T modifiedValue = modifier.modifyValue(currentValue);

            // 수정된 값으로 덮어쓰기 (타입에 따라 다르게 처리)
            objectNode.putPOJO(keyPath, modifiedValue);
        }
    }

    private static <T> void modifyArrayValues(ArrayNode arrayNode, ValueModifier<T> modifier, Class<T> valueType) throws IOException {
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode elementNode = arrayNode.get(i);

            // 단순 값인 경우만 처리 (문자열, 숫자 등)
            if (elementNode.isValueNode()) {
                T currentValue = mapper.convertValue(elementNode, valueType);

                // 수정 로직 적용
                T modifiedValue = modifier.modifyValue(currentValue);

                // 수정된 값을 배열에 반영
                arrayNode.set(i, mapper.valueToTree(modifiedValue));
            }
        }
    }

    public static JsonNode getJsonNode(File jsonFile) throws IOException {
        return mapper.readTree(jsonFile);
    }

    public static <T> List<T> convertJsonToList(File jsonFile, Class<T> valueType) throws IOException {
        return convertJsonToList(jsonFile.toPath(), valueType);
    }

    public static <T> List<T> convertJsonToList(Path jsonFilePath, Class<T> valueType) throws IOException {
        return convertJsonToList(Files.newInputStream(jsonFilePath), valueType);
    }

    public static <T> List<T> convertJsonToList(InputStream inputStream, Class<T> clazz) {

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
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static <T> List<T> convertJsonContentToList(String content, Class<T> valueType) {

        List<T> result = new ArrayList<>();

        try {
            JsonNode jsonNode = mapper.readTree(content);

            // JSON이 배열인 경우
            if (jsonNode.isArray()) {
                // InputStream을 다시 읽기 위해 리셋하거나, 다시 받아야 함
                // 새로운 InputStream을 사용하여 배열을 처리
                result = mapper.readValue(jsonNode.toString(), mapper.getTypeFactory().constructCollectionType(List.class, valueType));
            }
            // JSON이 단일 객체인 경우
            else {
                T obj = mapper.treeToValue(jsonNode, valueType);
                result.add(obj);  // 단일 객체를 리스트로 변환
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON string to object", e);
        }
    }

    public static <T> T convertJsonStringToObject(String content, Class<T> valueType) {
        try {
            return mapper.readValue(content, valueType);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON string to object", e);
        }
    }

    public static <T> T convertJsonToObject(String jsonFile, Class<T> valueType) {
        return convertJsonToObject(new File(jsonFile), valueType);
    }

    public static <T> T convertJsonToObject(Path jsonFile, Class<T> valueType) {
        return convertJsonToObject(jsonFile.toFile(), valueType);
    }

    public static <T> T convertJsonToObject(File jsonFile, Class<T> valueType) {
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
        saveObjectToJsonFile(object, new File(filePath));
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

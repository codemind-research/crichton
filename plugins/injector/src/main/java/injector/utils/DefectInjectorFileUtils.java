package injector.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

public class DefectInjectorFileUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    private DefectInjectorFileUtils() {
        throw new AssertionError();
    }

    public static String getTestSpecFileName(String defectJsonFile) throws IOException, NoSuchElementException {
        var jsonNode = mapper.readTree(new File(defectJsonFile));
        if(!jsonNode.has("build")) {
            throw new NoSuchElementException(defectJsonFile + " does not have build");
        }
        return jsonNode.get("build").asText();
    }

    public static String getTargetFileName(String defectJsonFile) {
        try {
            JsonNode rootNode = mapper.readTree(new File(defectJsonFile));
            return rootNode.get("target").asText();
        } catch (Exception e){
            return "";
        }
    }

}

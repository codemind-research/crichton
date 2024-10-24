package org.crichton;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class ConvertJsonTest {


    private File safeSpecJsonFile;
    private File inejctionResultCsvFile;

    @BeforeEach
    void setUp() throws Exception {
        // Load the file from the test resources directory
        safeSpecJsonFile = Paths.get(ConvertJsonTest.class.getClassLoader()
                .getResource("safe_spec.json").toURI()).toFile();

        assertThat(safeSpecJsonFile).isNotNull();
        assertThat(safeSpecJsonFile.exists()).isTrue();
        assertThat(safeSpecJsonFile.isFile()).isTrue();
        assertThat(safeSpecJsonFile.length()).isGreaterThan(0);


        inejctionResultCsvFile = Paths.get(ConvertJsonTest.class.getClassLoader()
                .getResource("injection_result.csv").toURI()).toFile();

        assertThat(inejctionResultCsvFile).isNotNull();
        assertThat(inejctionResultCsvFile.exists()).isTrue();
        assertThat(inejctionResultCsvFile.isFile()).isTrue();
        assertThat(inejctionResultCsvFile.length()).isGreaterThan(0);
    }

    @Disabled("아직 준비가 안됨")
    @Test
    void testDefectJsonProcessing() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

//            LinkedHashMap<String, Object> defectMap = objectMapper
//                    .readValue(safeSpecJsonFile, new TypeReference<List<LinkedHashMap<String, Object>>>() {})
//                    .stream()
//                    .collect(LinkedHashMap::new,
//                            (map, entry) -> {
//                                String key = entry.remove("id").toString();
//                                entry.putIfAbsent("safe", getSaveJsonData(Integer.parseInt(key)));
//                                map.put(key, entry);
//                            },
//                            LinkedHashMap::putAll);

            // AssertJ assertions
            Map<String, Object>  map = new ConcurrentHashMap<>();
            assertThat(map).isNotNull().isNotEmpty();
        } catch (Exception e) {
            // Fail the test if an exception occurs
            assertThat(true).isFalse(); // Using AssertJ for better error message
        }
    }

    @AfterEach
    void tearDown() {
        // Clean up resources if needed
        safeSpecJsonFile = null;
    }

}

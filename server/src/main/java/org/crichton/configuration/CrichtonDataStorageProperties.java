package org.crichton.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.util.Optional;

@Getter
@ConfigurationProperties(prefix = "crichton.data.storage")
@Slf4j
@RequiredArgsConstructor
public class CrichtonDataStorageProperties {

    private final Optional<String> basePath;

    public String getBasePath() {
        return basePath.orElse(System.getProperty("user.home") + File.separator + ".crichton" + File.separator + "data");
    }

    @PostConstruct
    public void init() {
        createDirectoryIfNotExists(getBasePath());
    }


    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            log.info("Creating data storage: {}", directory.getAbsolutePath());
            directory.mkdirs(); // 디렉토리가 없을 경우 생성
        }
        else {
            log.info("'{}' already exists.", directory.getAbsolutePath());
        }
    }
}

package org.crichton.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        log.info("Creating directory {}", path);
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs(); // 디렉토리가 없을 경우 생성
        }
    }
}

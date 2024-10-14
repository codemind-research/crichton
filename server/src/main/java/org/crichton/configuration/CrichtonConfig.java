package org.crichton.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.File;

@Configuration
@PropertySource("./config/crichton.properties")
@Getter
public class CrichtonConfig {

    @Value("${file.storage.base-path:${user.home}/.crichton/data}")
    private String dataStorageBasePath;


    @PostConstruct
    public void init() {
        createDirectoryIfNotExists(dataStorageBasePath);
    }

    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs(); // 디렉토리가 없을 경우 생성
        }
    }
}

package coyote.util;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CoyotePluginProperties extends Properties {

    @Getter
    public enum Property {
        CoyoteEnginePath("engine.path", "/usr/bin/CoyoteCLI"),
        ;

        final String key;
        final String defaultValue;

        Property(String key, String value) {
            this.key = key;
            this.defaultValue = value;
        }
    }

    public static CoyotePluginProperties loadProperties(String propertiesFilePath) {


        var properties = new CoyotePluginProperties();
        try (FileInputStream fileInputStream = new FileInputStream(propertiesFilePath)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public String getEnginePath() {
        return this.getProperty(Property.CoyoteEnginePath.getKey(), Property.CoyoteEnginePath.getDefaultValue());
    }

}
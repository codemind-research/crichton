package injector.utils;

import lombok.Getter;
import runner.util.PropertiesFileReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DefectInjectorProperties extends Properties {

    @Getter
    public enum Property {
        TrampolinePath("trampoline.path", "libs/trampoline"),
        GoilProcessPath("goil.process.path", "/usr/bin/goil"),
        GoilTemplatePath("goil.template.path", "libs/trampoline/goil/templates"),
        ViperPath("viper.path", "libs/trampoline/viper"),
        ;

        final String key;
        final Object defaultValue;

        Property(String key, Object value) {
            this.key = key;
            this.defaultValue = value;
        }

        public <T> T getDefaultValue() {
            return (T) defaultValue;
        }
    }

    public static DefectInjectorProperties loadProperties(String propertiesFilePath) {

        var properties = new DefectInjectorProperties();
        try (FileInputStream fileInputStream = new FileInputStream(propertiesFilePath)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public String getTrampolinePath() {
        return this.getProperty(Property.TrampolinePath.getKey(), Property.TrampolinePath.getDefaultValue());
    }

    public String getGoilProcessPath() {
        return this.getProperty(Property.GoilProcessPath.getKey(), Property.GoilProcessPath.getDefaultValue());
    }

    public String getGoilTemplatePath() {
        return this.getProperty(Property.GoilTemplatePath.getKey(), Property.GoilTemplatePath.getDefaultValue());
    }

    public String getViperPath() {
        return this.getProperty(Property.ViperPath.getKey(), Property.ViperPath.getDefaultValue());
    }

}

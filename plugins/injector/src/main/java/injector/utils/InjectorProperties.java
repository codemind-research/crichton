package injector.utils;

import injector.enumerations.InjectorBinaries;
import lombok.Getter;
import lombok.NonNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class InjectorProperties extends Properties {

    @Getter
    public enum Property {
        DefectInjectorEnginePath("engine.defect-injector.path", "libs/engines/DefectInjector.dll"),
        InjectionTesterEnginePath("engine.injection-tester.path", "libs/engines/InjectionTester.dll"),
        TrampolinePath("trampoline.path", "libs/trampoline"),
        GoilProcessPath("goil.process.path", "/usr/bin/goil"),
        GoilTemplatePath("goil.template.path", "libs/trampoline/goil/templates"),
        ViperPath("viper.path", "libs/trampoline/viper"),
        ;

        final String key;
        final String defaultValue;

        Property(String key, String value) {
            this.key = key;
            this.defaultValue = value;
        }
    }

    public static InjectorProperties loadProperties(String propertiesFilePath) {

        var properties = new InjectorProperties();
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

    public String getEnginePath(@NonNull InjectorBinaries binaries) {

        return switch (binaries) {
            case DEFECT -> this.getProperty(Property.DefectInjectorEnginePath.getKey(), Property.DefectInjectorEnginePath.getDefaultValue());
            case INJECTION -> this.getProperty(Property.InjectionTesterEnginePath.getKey(), Property.InjectionTesterEnginePath.getDefaultValue());
            default -> null;
        };
    }

}

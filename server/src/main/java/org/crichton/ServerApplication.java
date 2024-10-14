package org.crichton;

import org.crichton.configuration.Config;
import org.crichton.configuration.CrichtonDataStorageProperties;
import org.crichton.util.ManifestUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableConfigurationProperties(CrichtonDataStorageProperties.class)
public class ServerApplication {
    private static Config config;
    private static ConfigurableApplicationContext APP_CTX;

    public static void main(String[] args)  {


        ServerApplication.Version.set();
        System.out.println("Crichton Server Version: " + Version.getBuildInfo());
        application(args, true);
    }

    private static void application(String[] args, boolean b) {
        APP_CTX = new SpringApplicationBuilder(ServerApplication.class)
//                .bannerMode(Banner.Mode.OFF)
                .profiles("https")
                .run(args);
    }

    public static class Version {
        private static String build_info = "";
        public static String getBuildInfo() {
            return build_info;
        }

        public static void set() {
            build_info = ManifestUtil.getVersion();
        }
    }
}

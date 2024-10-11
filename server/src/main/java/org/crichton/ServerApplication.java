package org.crichton;

import org.crichton.configuration.Config;
import org.crichton.util.ManifestUtil;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ServerApplication {
    private static Config config;
    private static ConfigurableApplicationContext APP_CTX;

    public static void main(String[] args)  {
        if(args.length == 0){
            System.err.println("Usage : crichton server [port]");
            return;
        }
        config = new Config();
        config.setPort(Integer.parseInt(args[0]));
        ServerApplication.Version.set();
        System.out.println("Crichton Server Version: " + Version.getBuildInfo());
        application(args, true);
    }

    private static void application(String[] args, boolean b) {
        System.setProperty("server.port", String.valueOf(config.getPort()));
        APP_CTX = new SpringApplicationBuilder()
                .bannerMode(Banner.Mode.OFF)
                .sources(ServerApplication.class)
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

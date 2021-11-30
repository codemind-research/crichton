package crichton.server;

import crichton.manager.scalatest;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ServerApplication {
	private static Config config;
	private static ConfigurableApplicationContext APP_CTX;

	public static void test() {
		scalatest x = new scalatest();
		x.test();
	}

	public static void main(String[] args) throws IOException {
		config = new Config();
		ServerApplication.Version.set();

		System.out.println("Crichton Server Version: " + Version.getBuildInfo());

		test();

		application(args, true);
		config = new Config();

	}

	private static void application(String[] args, boolean b) {
		System.setProperty("server.port", String.valueOf(Config.getPort()));
		APP_CTX = new SpringApplicationBuilder()
				.bannerMode(Banner.Mode.OFF)
				.sources(ServerApplication.class)
				.profiles("https")
				.run(args);
	}

	public static class Version {
		private static String build_info = "1.0";
		public static String getBuildInfo() {
			return build_info;
		}

		public static void set() {
		}
	}
}

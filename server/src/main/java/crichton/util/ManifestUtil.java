package crichton.util;

import crichton.ServerApplication;

import java.net.URL;
import java.util.jar.Manifest;
import java.util.regex.Matcher;

public class ManifestUtil {
    
    public static Manifest manifest(Class clazz) throws Exception {

        String container = clazz.getProtectionDomain().getCodeSource().getLocation().toString();
        URL manifestUrl = new URL("jar:" + container + "!/META-INF/MANIFEST.MF");
        return new Manifest(manifestUrl.openStream());
    }

    public static String getVersion() {
        StringBuilder version = new StringBuilder();
        try {
            Manifest manifest = manifest(ServerApplication.class);
            String gitHash = manifest.getMainAttributes().getValue("GIT_HASH");
            Matcher matcher = RegexPatterns.VERSION_PATTERN.matcher(gitHash);
            if (matcher.find()){
                version.append(matcher.group(1)).append(".").append(matcher.group(2));
            }
        } catch (Exception e) {
            version.append("1.0");
        }
        return version.toString();
    }
}

package runner.loader;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runner.Plugin;
import runner.util.ClassLoaderUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class BasicPluginLoader implements PluginLoader {

    private static final Logger log = LoggerFactory.getLogger(BasicPluginLoader.class);

    private final PluginClassLoader pluginClassLoader;

    public BasicPluginLoader(@NonNull Path jarFilePath) throws MalformedURLException {
        URL jarUrl = jarFilePath.toUri().toURL();
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        this.pluginClassLoader = new PluginClassLoader(new URL[]{jarUrl}, parentClassLoader);
    }

    @Override
    public boolean isApplicable(Path pluginPath) {
        return Files.exists(pluginPath);
    }

    @Override
    public Optional<Plugin> loadPlugin(@NonNull String packageName) throws Exception {

        log.info("Loading plugin {}", packageName);
        List<String> classNames = findClassesInPackage(pluginClassLoader, packageName);

        // Plugin 클래스로더가 가지고 있는 클래스들을 가지고옴
        List<Class<?>> pluginClasses = classNames.stream()
                .map(className -> {
                    try {
                        return pluginClassLoader.loadPluginClass(className);
                    } catch (ClassNotFoundException e) {
                        log.warn(className + " not found.", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());

        // Plugin 인터페이스 구현된 Class 추출
        List<Class<?>> assignAblePluginClasses = pluginClasses.stream()
                .filter(classes -> {
                    if (Plugin.class.isAssignableFrom(classes.getClass())) {
                        return true;
                    }
                    return ClassLoaderUtils.findAllClassesAndInterfaces(classes).contains(Plugin.class);
                })
                .collect(Collectors.toUnmodifiableList());


        // Load the first class found in the package
        if (!assignAblePluginClasses.isEmpty()) {
            Class<?> pluginClass = assignAblePluginClasses.get(0);
            Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
            return Optional.of(plugin);
        }

        return Optional.empty();
    }


    private List<String> findClassesInPackage(ClassLoader classLoader, String packageName) {
        List<String> classNames = new ArrayList<>();

        URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
        URL jarUrl = urlClassLoader.getURLs()[0];
        try (JarFile jarFile = new JarFile(Paths.get(jarUrl.toURI()).toFile())) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith(packageName.replace('.', '/')) && entryName.endsWith(".class")) {
                    String className = entryName.substring(0, entryName.length() - 6);
                    className = className.replace('/', '.');
                    classNames.add(className);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classNames;
    }

}

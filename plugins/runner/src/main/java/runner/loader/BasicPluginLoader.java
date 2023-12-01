package runner.loader;

import lombok.NonNull;
import runner.Plugin;
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

public class BasicPluginLoader implements PluginLoader{

    private PluginClassLoader pluginClassLoader;

    public BasicPluginLoader(@NonNull Path jarFilePath) throws MalformedURLException {
        URL jarUrl = jarFilePath.toUri().toURL();
        this.pluginClassLoader = new PluginClassLoader(new URL[]{jarUrl});
    }

    @Override
    public boolean isApplicable(Path pluginPath) {
        return Files.exists(pluginPath);
    }

    @Override
    public Optional<Plugin> loadPlugin(@NonNull String packageName) throws Exception {
        List<String> classNames = findClassesInPackage(pluginClassLoader, packageName);

        List<Class<?>> pluginClasses = classNames.stream()
                                                 .map(className -> {
                                                     try {
                                                         return pluginClassLoader.loadPluginClass(className);
                                                     } catch (ClassNotFoundException e) {
                                                         return null;
                                                     }
                                                 })
                                                 .filter(Objects::nonNull)
                                                 .filter(Plugin.class::isAssignableFrom)
                                                 .collect(Collectors.toList());


        // Load the first class found in the package
        if (!pluginClasses.isEmpty()) {
            Class<?> pluginClass = pluginClasses.get(0);
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

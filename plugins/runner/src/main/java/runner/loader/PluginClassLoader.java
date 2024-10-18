package runner.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PluginClassLoader extends URLClassLoader {

    /**
     * Constructs a new URLClassLoader for the given URLs. The URLs will be
     * searched in the order specified for classes and resources after first
     * searching in the specified parent class loader.  Any {@code jar:}
     * scheme URL is assumed to refer to a JAR file.  Any {@code file:} scheme
     * URL that ends with a '/' is assumed to refer to a directory.  Otherwise,
     * the URL is assumed to refer to a JAR file which will be downloaded and
     * opened as needed.
     *
     * <p>If there is a security manager, this method first
     * calls the security manager's {@code checkCreateClassLoader} method
     * to ensure creation of a class loader is allowed.
     *
     * @param urls   the URLs from which to load classes and resources
     * @param parent the parent class loader for delegation
     * @throws SecurityException    if a security manager exists and its
     *                              {@code checkCreateClassLoader} method doesn't allow
     *                              creation of a class loader.
     * @throws NullPointerException if {@code urls} or any of its
     *                              elements is {@code null}.
     * @see SecurityManager#checkCreateClassLoader
     */
    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public PluginClassLoader(URL[] urls) {
        super(urls);
    }

    public Class<?> loadPluginClass(String className) throws ClassNotFoundException {
        return super.loadClass(className, true);
    }

}

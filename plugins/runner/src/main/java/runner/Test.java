package runner;

import runner.loader.BasicPluginLoader;
import runner.loader.PluginLoader;
import runner.paths.PluginPaths;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Test {
    public static void main(String[] args) throws Exception {
        HashMap<String,String> setting = new HashMap<>();
        setting.put("report", "/home/hanjin/coyoteCli/sample.csv");
        PluginRunner runner = new PluginRunner("coyote", "/home/hanjin/Sample/s1",setting);
        runner.run();
    }
}

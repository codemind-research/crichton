package crichton.domain.services;

import crichton.application.exceptions.CustomException;
import crichton.application.exceptions.code.FailedErrorCode;
import crichton.domain.dtos.TestDTO;
import crichton.paths.DirectoryPaths;
import crichton.util.FileUtils;
import crichton.util.PropertyFileReader;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zeroturnaround.zip.ZipUtil;
import runner.PluginRunner;
import runner.dto.ProcessedReportDTO;
import runner.dto.RunResult;

import java.io.File;
import java.util.*;

@Service("TestService")
public class TestServiceImpl implements TestService{

    @Override
    public TestDTO.TestResponse doPluginTest(TestDTO.PluginRequest request, MultipartFile settings) throws CustomException {
        String plugin = request.getPlugin();
        String targetSource = request.getSourcePath();
        if (!isCheckSourcePath(targetSource))
            throw new CustomException(FailedErrorCode.NOT_EXIST_TARGET_DIRECTORY);
        makeSettings(plugin, settings);
        RunResult pluginTestDone = runPluginTest(plugin, targetSource, request.getPluginSettings());
        return TestDTO.TestResponse.builder()
                                   .testResult(pluginTestDone.getIsSuccess())
                                   .reportData(pluginTestDone.getData())
                                   .build();
    }


    private void makeSettings(String plugin, MultipartFile settings) throws CustomException{
        try {
            if (settings == null)
                return;
            File zipPath = DirectoryPaths.generatePluginZipPath(plugin, settings.getOriginalFilename()).toFile();
            FileUtils.readMultipartFile(settings, zipPath);
            File unzipPath = DirectoryPaths.generatePluginUnZipPath(plugin, FilenameUtils.getBaseName(settings.getOriginalFilename())).toFile();
            if (unzipPath.exists()) {
                FileDeleteStrategy.FORCE.delete(unzipPath);
            }
            unzipPath.mkdir();
            ZipUtil.unpack(zipPath, unzipPath);
            zipPath.delete();
        }catch (Exception e) {
            throw new CustomException(FailedErrorCode.UPLOAD_FAILED);
        }
    }

    private RunResult runPluginTest(String pluginName, String sourcePath, HashMap<String,String> pluginSettings){
        try {
            PluginRunner runner = new PluginRunner(pluginName, sourcePath, pluginSettings);
            return runner.run();
        } catch (Exception e) {
            return new RunResult(false, new ProcessedReportDTO());
        }
    }


    @Override
    public TestDTO.PluginResponse getPlugin() throws CustomException {
        List<File> pluginList = FileUtils.getSubDirectories(DirectoryPaths.PLUGIN_PATH.toFile())
                                         .orElseThrow( () -> new CustomException(FailedErrorCode.NOT_EXIST_PLUGINS));
        return TestDTO.PluginResponse.builder()
                                     .pluginList(pluginList.stream().map(this::getPluginSetting).toList())
                                     .build();

    }

    private TestDTO.PluginSetting getPluginSetting(File plugin) {
        return TestDTO.PluginSetting.builder()
                                    .plugin(plugin.getName())
                                    .setting(readPluginProperties(plugin.getName()))
                                    .build();
    }

    private HashMap<String,String> readPluginProperties(String pluginName) {
        HashMap<String, String> propertyMap = new HashMap<>();
        String path = DirectoryPaths.generatePluginPropertiesPath(pluginName).toFile().getAbsolutePath();
        Properties properties = PropertyFileReader.readPropertiesFile(path);
        Set<String> keys = properties.stringPropertyNames();
        for (String key : keys) {
            String value = properties.getProperty(key);
            propertyMap.put(key, value);
        }
        return propertyMap;
    }

    private boolean isCheckSourcePath(String sourcePath) {
        return !sourcePath.isBlank() && new File(sourcePath).exists();
    }

}

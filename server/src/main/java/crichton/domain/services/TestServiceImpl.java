package crichton.domain.services;

import crichton.application.exceptions.CustomException;
import crichton.application.exceptions.code.FailedErrorCode;
import crichton.domain.dtos.LogDTO;
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
import runner.paths.PluginPaths;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
            File zipPath = PluginPaths.generatePluginZipPath(plugin, settings.getOriginalFilename()).toFile();
            FileUtils.readMultipartFile(settings, zipPath);
            File unzipPath = PluginPaths.generatePluginUnZipPath(plugin, FilenameUtils.getBaseName(settings.getOriginalFilename())).toFile();
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
        List<File> pluginList = FileUtils.getSubDirectories(PluginPaths.PLUGIN_DIR_PATH.toFile())
                                         .orElseThrow( () -> new CustomException(FailedErrorCode.NOT_EXIST_PLUGINS));
        return TestDTO.PluginResponse.builder()
                                     .pluginList(pluginList.stream().map(this::getPluginSetting).toList())
                                     .build();

    }

    @Override
    public LogDTO.LogResponse getCrichtonLog(LogDTO.LogRequest request){
        File file = PluginPaths.CRICHTON_LOG_PATH.toFile();
        try {
            if (!file.exists()) {
                throw new Exception();
            }
            int startpos = request.getStartpos();
            long maxline = request.getMaxline() > 0 ? request.getMaxline() : Integer.MAX_VALUE;
            return getLog(file, startpos, maxline);
        } catch (Exception e) {
            return LogDTO.LogResponse.builder()
                                     .text(new StringBuilder())
                                     .build();
        }
    }

    private TestDTO.PluginSetting getPluginSetting(File plugin) {
        return TestDTO.PluginSetting.builder()
                                    .plugin(plugin.getName())
                                    .setting(readPluginProperties(plugin.getName()))
                                    .build();
    }

    private HashMap<String,String> readPluginProperties(String pluginName) {
        HashMap<String, String> propertyMap = new HashMap<>();
        String path = PluginPaths.generatePluginPropertiesPath(pluginName).toFile().getAbsolutePath();
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

    private static LogDTO.LogResponse getLog(File file, int startpos, long maxline) throws IOException {
        LogDTO.LogResponse dto = new LogDTO.LogResponse();
        boolean read_trailers_mode = startpos < 0;

        long pos = 0;
        if (file.exists()) {
            BufferedReader bi = new BufferedReader(new FileReader(file));
            if (read_trailers_mode && file.length() >= 1024 * 1024) {
                try {
                    pos = file.length() - 1024 * 1024;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                pos = Math.max(startpos, 0);
            }
            bi.skip(pos);
            String line;
            LinkedList<String> lines = new LinkedList<>();
            int linecount = 0;
            while ((line = bi.readLine()) != null) {
                if (linecount > maxline) {
                    if (read_trailers_mode)
                        lines.removeFirst();
                    else
                        break;
                }
                lines.add(line);
                pos += line.length() + 1;
                linecount++;
            }
            StringBuilder text = new StringBuilder();
            if (linecount > maxline) {
                // over maxline
                dto.setOverflow(true);
                for (String s : lines) {
                    text.append(s);
                    text.append("\n");
                }
            } else {
                // under maxline
                dto.setOverflow(false);
                for (String s : lines) {
                    text.append(s);
                    text.append("\n");
                }
            }
            bi.close();
            dto.setText(text);
            dto.setEndpos(pos);
        } else {
            dto.setText(new StringBuilder(""));
            dto.setEndpos(0);
        }
        return dto;
    }

}

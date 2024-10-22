package org.crichton.domain.services;

import org.crichton.application.exceptions.CustomException;
import org.crichton.application.exceptions.code.FailedErrorCode;
import org.crichton.paths.DirectoryPaths;
import org.crichton.util.FileUtils;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zeroturnaround.zip.ZipUtil;
import runner.paths.PluginPaths;

import java.io.File;

@Service("StorageService")
public class StorageServiceImpl implements StorageService{

    @Override
    public File uploadFile(MultipartFile source) throws CustomException {
        try {
            File crichtonLogPath = PluginPaths.CRICHTON_LOG_PATH.toFile();
            if (crichtonLogPath.exists()){
                crichtonLogPath.delete();
            }
            File downloadPath = DirectoryPaths.UPLOAD_PATH.toFile();
            if (!downloadPath.exists()) {
                downloadPath.mkdir();
            }
            File downloadSourcePath = DirectoryPaths.generateZipPath(source.getOriginalFilename()).toFile();
            FileUtils.readMultipartFile(source, downloadSourcePath);
            File unzipPath = DirectoryPaths.generateUnzipPath(FilenameUtils.getBaseName(source.getOriginalFilename())).toFile();
            if (unzipPath.exists()) {
                FileDeleteStrategy.FORCE.delete(unzipPath);
            }
            unzipPath.mkdir();
            ZipUtil.unpack(downloadSourcePath, unzipPath);
            downloadSourcePath.delete();
            return unzipPath;
        }catch (Exception e) {
            throw new CustomException(FailedErrorCode.UPLOAD_FAILED);
        }
    }


}

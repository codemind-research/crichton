package crichton.domain.services;

import crichton.application.exceptions.CustomException;
import crichton.application.exceptions.code.FailedErrorCode;
import crichton.paths.DirectoryPaths;
import crichton.util.FileUtils;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

@Service("StorageService")
public class StorageServiceImpl implements StorageService{

    @Override
    public File uploadFile(MultipartFile source) throws CustomException {
        try {
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
            File settingFile = DirectoryPaths.generateSettingsPath(FilenameUtils.getBaseName(source.getOriginalFilename())).toFile();
            if (settingFile.exists()){
                settingFile.delete();
            }
            return unzipPath;
        }catch (Exception e) {
            throw new CustomException(FailedErrorCode.UPLOAD_FAILED);
        }
    }


}

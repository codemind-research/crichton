package org.crichton.util;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.crichton.domain.utils.enums.UploadAllowFileDefine;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 파일 및 디렉터리 작업을 위한 유틸리티 클래스입니다.
 */
@Slf4j
public class FileUtils {

    /**
     * 지정된 디렉터리를 재귀적으로 삭제합니다.
     *
     * @param path 삭제할 디렉터리 경로
     * @throws IOException 파일 또는 디렉터리 삭제 중 오류가 발생할 경우
     */
    public static void deleteDirectoryRecursively(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file); // 파일 삭제
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir); // 디렉터리 삭제
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 지정된 디렉터리를 삭제합니다.
     * 디렉터리 내 모든 파일과 하위 디렉터리도 삭제됩니다.
     *
     * @param path 삭제할 디렉터리
     * @return 삭제 성공 여부
     */
    public static boolean removeDirectory(File path) {
        if (!path.exists())
            return false;

        File[] files = path.listFiles();
        for (File file : files) {
            if (file.isDirectory())
                removeDirectory(file);
            else
                file.delete();
        }

        return path.delete();
    }

    /**
     * MultipartFile 객체를 지정된 경로에 파일로 저장합니다.
     *
     * @param file MultipartFile 객체
     * @param downloadPath 저장할 파일 경로
     */
    public static void readMultipartFile(MultipartFile file, File downloadPath){
        try {
            if (downloadPath.exists()) {
                downloadPath.delete();
            }
            downloadPath.createNewFile();
            FileOutputStream fos = new FileOutputStream(downloadPath);
            fos.write(file.getBytes());
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 지정된 경로의 절대 경로를 반환합니다.
     *
     * @param path 경로 문자열
     * @param more 추가 경로들
     * @return 절대 경로 문자열
     */
    public static String getAbsolutePath(String path, String ...more) {
        return getAbsolutePath(Paths.get(path, more));
    }

    /**
     * Path 객체의 절대 경로를 반환합니다.
     *
     * @param path Path 객체
     * @return 절대 경로 문자열
     */
    private static String getAbsolutePath(Path path) {
        return path.normalize().toAbsolutePath().toString();
    }

    /**
     * 디렉터리와 파일 이름을 조합하여 경로를 반환합니다.
     *
     * @param directory 디렉터리 경로
     * @param fileName 파일 이름
     * @return Path 객체
     */
    public static Path getFilePath(String directory, String fileName) {
        return Paths.get(directory, fileName).normalize();
    }

    public static Path getFilePath(String directory, String ...fileName) {
        return Paths.get(directory, fileName).normalize();
    }


    /**
     * 지정된 디렉터리의 하위 디렉터리 목록을 반환합니다.
     *
     * @param directory 디렉터리 경로
     * @return 하위 디렉터리 목록이 포함된 Optional 객체
     */
    public static Optional<List<File>> getSubDirectories(File directory) {
        if (directory.isDirectory()) {
            File[] subdirectories = directory.listFiles(File::isDirectory);
            return Optional.ofNullable(subdirectories)
                    .map(arr ->
                            Stream.of(arr)
                                    .filter(File::isDirectory)
                                    .collect(Collectors.toList())
                    );
        } else {
            return Optional.empty();
        }
    }

    /**
     * 특정 디렉터리 내의 파일이 반드시 존재해야 함을 확인하는 메서드
     *
     * @param directory 파일이 포함된 디렉터리 경로 (절대 또는 상대 경로)
     * @param fileName  디렉터리 내에서 확인할 파일 이름 (파일 이름이 없으면 디렉터리만 확인)
     * @throws IllegalArgumentException 파일이 존재하지 않거나 파일이 아닌 경우 예외 발생
     */
    public static void assertFileExists(String directory, @Nullable String fileName) {
        var absoluteFilePath = getAbsolutePath(directory, fileName);
        var file = new File(absoluteFilePath);

        if (!file.exists() || !file.isFile()) {
            var message = String.format("There is no file '%s' in path '%s'.", fileName, directory);
            throw new IllegalArgumentException(message);
        } else {
            log.info("File '{}' already exists.", absoluteFilePath);
        }
    }

    public static void makeDirectory(String directory) {
        makeDirectory(new File(directory));
    }

    public static void makeDirectory(Path directory) {
        makeDirectory(directory.toFile());
    }

    public static void makeDirectory(File directory) {
        if(!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static class CompressFile {
        private CompressFile() {
            throw new IllegalStateException("Utility class");
        }

        public static void extractFile(MultipartFile file, String destDir) throws IOException {
            // MIME 타입과 확장자 분석
            String mimeType = FileUtils.getMimeTypeByTika(file);
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());

            UploadAllowFileDefine fileDefine = UploadAllowFileDefine.getByMimeTypeAndExtension(mimeType, fileExtension);
            if (fileDefine == null) {
                throw new IOException("Unsupported file format: MIME=" + mimeType + ", Extension=" + fileExtension);
            }

            File tempFile = Files.createTempFile("temp", "." + fileExtension).toFile();
            file.transferTo(tempFile);

            try {
                switch (fileDefine) {
                    case ZIP:
                        unzipFile(tempFile, destDir);
                        break;
                    case TAR:
                        extractTarFile(tempFile, destDir);
                        break;
                    case GZ:
                    case TGZ:
                    case TAR_GZ:
                        extractTarGzFile(tempFile, destDir);
                        break;
                    default:
                        throw new IOException("Unsupported extraction method for: " + fileDefine.name());
                }
            } finally {
                tempFile.delete();
            }
        }

        public static void unzipFile(File zipFile, String destDir) throws IOException {
            try (ZipFile zip = new ZipFile(zipFile)) {
                zip.extractAll(destDir);
            }
        }

        public static void extractTarGzFile(File tarGzFile, String destDir) throws IOException {
            try (FileInputStream fis = new FileInputStream(tarGzFile);
                 BufferedInputStream bis = new BufferedInputStream(fis);
                 GzipCompressorInputStream gis = new GzipCompressorInputStream(bis);
                 TarArchiveInputStream tis = new TarArchiveInputStream(gis)) {

                extractTarArchive(tis, destDir);
            }
        }

        public static void extractTarFile(File tarFile, String destDir) throws IOException {
            try (FileInputStream fis = new FileInputStream(tarFile);
                 TarArchiveInputStream tis = new TarArchiveInputStream(fis)) {

                extractTarArchive(tis, destDir);
            }
        }

        public static void extractTarArchive(TarArchiveInputStream tis, String destDir) throws IOException {
            TarArchiveEntry entry;
            while ((entry = tis.getNextTarEntry()) != null) {
                File outputFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    outputFile.getParentFile().mkdirs();
                    try (OutputStream os = new FileOutputStream(outputFile)) {
                        IOUtils.copy(tis, os);
                    }
                }
            }
        }

        /**
         * 최상위 디렉토리를 제거한 경로 반환
         * 예: dir1/sub1/file2.txt -> sub1/file2.txt
         */
        private static String removeTopLevelDirectory(String entryName) {
            int firstSlashIndex = entryName.indexOf('/');
            if (firstSlashIndex >= 0) {
                return entryName.substring(firstSlashIndex + 1); // 첫 번째 슬래시 이후 경로 반환
            }
            return entryName; // 슬래시가 없으면 그대로 반환
        }

    }

    /**
     * apache Tika라이브러리를 이용해서 파일의 mimeType을 가져옴
     *
     * @param multipartFile
     * @return
     */
    public static String getMimeTypeByTika(MultipartFile multipartFile) {
        try(var inputStream = multipartFile.getInputStream()) {

            Tika tika = new Tika();

            // MIME 타입 감지
            String mimeType = tika.detect(inputStream);
            log.debug("업로드 요청된 파일 {}의 mimeType:{}", multipartFile.getOriginalFilename(), mimeType);

            // 확장자가 .json이면서 mimeType이 text/plain이거나 application/octet-stream인 경우,
            // application/json으로 설정
            String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            if ("json".equalsIgnoreCase(fileExtension) &&
                    ("text/plain".equalsIgnoreCase(mimeType) || "application/octet-stream".equalsIgnoreCase(mimeType))) {
                mimeType = "application/json";
            }

            return mimeType;

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}

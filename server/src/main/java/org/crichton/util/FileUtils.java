package org.crichton.util;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
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

}

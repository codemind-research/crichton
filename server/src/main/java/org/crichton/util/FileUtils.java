package org.crichton.util;

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

    /**
     * 디렉터리와 파일 이름을 조합하여 절대 경로를 반환합니다.
     *
     * @param directory 디렉터리 경로
     * @param fileName 파일 이름
     * @return 절대 경로 Path 객체
     */
    public static Path getAbsoluteFilePath(String directory, String fileName) {
        return Paths.get(directory, fileName).toAbsolutePath().normalize();
    }

    /**
     * 파일의 내용을 읽고 StringBuilder에 저장하여 반환합니다.
     *
     * @param file 읽을 파일
     * @return 파일 내용이 저장된 StringBuilder 객체
     * @throws Exception 파일이 null인 경우
     */
    public static StringBuilder readFile(File file) throws Exception {
        if (file == null) {
            throw new Exception();
        } else {
            BufferedReader bi = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder lines = new StringBuilder();
            while ((line = bi.readLine()) != null) {
                lines.append(line);
                lines.append(System.lineSeparator());
            }

            bi.close();
            return lines;
        }
    }

    /**
     * 파일의 확장자를 반환합니다.
     *
     * @param file 파일 객체
     * @return 파일 확장자 (없을 경우 빈 문자열 반환)
     */
    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOfDot = name.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return ""; // 확장자가 없는 경우
        }
        return name.substring(lastIndexOfDot + 1);
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

}

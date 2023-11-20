package crichton.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class FileUtils {


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

    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOfDot = name.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return ""; // 확장자가 없는 경우
        }
        return name.substring(lastIndexOfDot + 1);
    }

}

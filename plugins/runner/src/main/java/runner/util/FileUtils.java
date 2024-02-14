package runner.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtils {

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

    public static boolean overWriteDump(File filename, String fileContents, String delim) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(filename, true);
            for (String line : fileContents.split(delim))
                fw.write(line + delim);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void moveFile(String current, String targetDirectory) {
        Path currentPath = Paths.get(current);
        Path targetPath = Paths.get(targetDirectory, currentPath.getFileName().toString());
        moveFileByPath(currentPath, targetPath);
    }

    public static void moveFileByPath(Path currentPath, Path targetPath) {
        try {
            Files.move(currentPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

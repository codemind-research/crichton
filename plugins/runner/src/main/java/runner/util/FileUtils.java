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
        try(var file = new FileWriter(filename, true)) {

            var lineSeparator = delim == null ? System.lineSeparator() : delim;

            for (String line : fileContents.split(lineSeparator)) {
                file.write(line + lineSeparator);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean overWriteDump(File filename, Exception error) {
        try(var fileWriter = new FileWriter(filename, true);
            var printWriter = new PrintWriter(fileWriter)) {

            printWriter.println("Exception occurred: " + error.getMessage());
            error.printStackTrace(printWriter);

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

package runner.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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

}

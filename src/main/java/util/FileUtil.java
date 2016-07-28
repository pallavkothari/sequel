package util;

import com.google.common.base.Charsets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by pkothari on 7/15/16.
 */
public class FileUtil {

    public static void write(String filename, String msg) {
        try {
            Files.write(init(filename), msg.getBytes(Charsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path init(String filename) throws IOException {
        String filenameToUse = filename.endsWith("sql") ? filename + "2" : filename + ".sql2";
        Path dir = Paths.get("output");
        if (!Files.isDirectory(dir)) Files.createDirectories(dir);
        Path file = dir.resolve(filenameToUse);
        if (!Files.exists(file)) Files.createFile(file);
        return file;
    }

    public static void write(String filename, Iterable<String> lines) {
        try {
            Files.write(init(filename), lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

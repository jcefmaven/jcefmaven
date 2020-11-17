package me.friwi.jcefmaven.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Util to modify file structures
 *
 * @author Fritz Windisch
 */
public class FileUtils {
    public static void deleteDirectory(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) deleteDirectory(f);
        }
        file.delete();
    }

    public static void extract(File file, File dir) throws IOException {
        byte[] buff = new byte[4096];
        ZipInputStream in = new ZipInputStream(new FileInputStream(file));
        ZipEntry entry;
        while ((entry = in.getNextEntry()) != null) {
            File f = new File(dir, entry.getName());
            if (entry.isDirectory()) {
                f.mkdirs();
            } else {
                f.getParentFile().mkdirs();
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(f);
                int r;
                while ((r = in.read(buff)) != -1) {
                    fos.write(buff, 0, r);
                }
                fos.flush();
                fos.close();
            }
            in.closeEntry();
        }
        in.close();
    }

    public static void replaceRecursive(File file, Map<String, String> replacements) throws IOException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) replaceRecursive(f, replacements);
        } else {
            String content = InputStreamUtils.readInputStreamToString(new FileInputStream(file),
                    128 * 1024, StandardCharsets.UTF_8);
            for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                content = content.replace(replacement.getKey(), replacement.getValue());
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        }
    }
}

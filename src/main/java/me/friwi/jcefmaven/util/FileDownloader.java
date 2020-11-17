package me.friwi.jcefmaven.util;

import me.friwi.jcefmaven.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Util to download files
 *
 * @author Fritz Windisch
 */
public class FileDownloader {
    public static File download(String url, String name) throws IOException {
        File ret = new File(Main.TEMP_DIR, name);
        download(url, ret);
        return ret;
    }

    public static void download(String url, File file) throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("Downloading " + url);
        URLConnection connection = new URL(url).openConnection();
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        long total = connection.getContentLengthLong();
        InputStream is = connection.getInputStream();
        byte[] buff = new byte[16 * 1024];
        int r;
        int current = 0;
        while ((r = is.read(buff)) != -1) {
            fos.write(buff, 0, r);
            current += r;
            if (total > 0) ProgressPrinter.printProgress(startTime, total, current, 1024 * 1024, "Mb");
        }
        is.close();
        fos.flush();
        fos.close();
        if (total > 0) System.out.println();
    }
}

package me.friwi.jcefmaven.util;

import me.friwi.jcefmaven.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Util to update the repository last_updated.txt timestamp
 *
 * @author Fritz Windisch
 */
public class RepoTimestamp {
    private static SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss z y", Locale.ENGLISH);

    public static void updateTimestamp() throws IOException {
        File stamp = new File(Main.CONFIGURATION.getWorkspaceDir(), Main.REPO_TIMESTAMP_LOCATION);
        stamp.getParentFile().mkdirs();
        stamp.createNewFile();
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        PrintWriter writer = new PrintWriter(new FileOutputStream(stamp));
        writer.print(sdf.format(new Date()));
        writer.flush();
        writer.close();
    }
}

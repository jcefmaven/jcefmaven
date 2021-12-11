package me.friwi.jcefmaven.macos;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class UnquarantineUtil {
    public static void unquarantine(File dir) throws IOException {
        Objects.requireNonNull(dir, "dir cannot be null");
        Process p = Runtime.getRuntime().exec("xattr -r -w com.apple.quarantine \"00c1;;;\" "+dir.getAbsolutePath());
        try {
            if(p.waitFor()>0){
                //Command failed
                throw new IOException("Failed to update xattr!");
            }
        } catch (InterruptedException e) {
            throw new IOException("Failed to update xattr!", e);
        }
    }
}

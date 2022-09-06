package me.friwi.jcefmaven.impl.step.extract;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to extract .tar.gz archives.
 * Preserves executable attributes.
 *
 * @author Fritz Windisch
 */
public class TarGzExtractor {
    private static final int BUFFER_SIZE = 4096;
    private static final Logger LOGGER = Logger.getLogger(TarGzExtractor.class.getName());

    public static void extractTarGZ(File installDir, InputStream in) throws IOException {
        Objects.requireNonNull(installDir, "installDir cannot be null");
        Objects.requireNonNull(in, "in cannot be null");
        GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(in);
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn)) {
            TarArchiveEntry entry;

            while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
                File f = new File(installDir, entry.getName());
                if (entry.isDirectory()) {
                    boolean created = f.mkdir();
                    if (!created) {
                        LOGGER.log(Level.SEVERE, "Unable to create directory '%s', during extraction of archive contents.\n",
                                f.getAbsolutePath());
                    } else {
                        if ((entry.getMode() & 0111) != 0 && !f.setExecutable(true, false)) {
                            LOGGER.log(Level.SEVERE, "Unable to mark directory '%s' executable, during extraction of archive contents.\n",
                                    f.getAbsolutePath());
                        }
                    }
                } else {
                    int count;
                    byte[] data = new byte[BUFFER_SIZE];
                    try (BufferedOutputStream dest = new BufferedOutputStream(
                            new FileOutputStream(f, false), BUFFER_SIZE)) {
                        while ((count = tarIn.read(data, 0, BUFFER_SIZE)) != -1) {
                            dest.write(data, 0, count);
                        }
                    }
                    if ((entry.getMode() & 0111) != 0 && !f.setExecutable(true, false)) {
                        LOGGER.log(Level.SEVERE, "Unable to mark file '%s' executable, during extraction of archive contents.\n",
                                f.getAbsolutePath());
                    }
                }
            }
        }
    }
}

package me.friwi.jcefmaven.impl.step.fetch;

import com.google.gson.Gson;
import me.friwi.jcefmaven.CefBuildInfo;
import me.friwi.jcefmaven.EnumPlatform;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to download the native packages from GitHub or central repository.
 * Central repository is only used as fallback.
 *
 * @author Fritz Windisch
 */
public class PackageDownloader {
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = Logger.getLogger(PackageDownloader.class.getName());

    private static final int BUFFER_SIZE = 16 * 1024;

    public static void downloadNatives(CefBuildInfo info, EnumPlatform platform, File destination,
                                       Consumer<Float> progressConsumer, Set<String> mirrors) throws IOException {
        Objects.requireNonNull(info, "info cannot be null");
        Objects.requireNonNull(platform, "platform cannot be null");
        Objects.requireNonNull(destination, "destination cannot be null");
        Objects.requireNonNull(progressConsumer, "progressConsumer cannot be null");
        Objects.requireNonNull(mirrors, "mirrors can not be null");
        if (mirrors.isEmpty()) {
            throw new RuntimeException("mirrors can not be empty");
        }

        //Create target file
        if (!destination.createNewFile()) {
            throw new IOException("Could not create target file " + destination.getAbsolutePath());
        }
        //Load maven version
        String mvn_version = loadJCefMavenVersion();

        //Try all mirrors
        Exception lastException = null;
        for (String mirror : mirrors) {
            String m = mirror
                    .replace("{platform}", platform.getIdentifier())
                    .replace("{tag}", info.getReleaseTag())
                    .replace("{mvn_version}", mvn_version);
            try {
                //Open connection to mirror
                URL url = new URL(m);
                HttpURLConnection uc = (HttpURLConnection) url.openConnection();
                try (InputStream in = uc.getInputStream()) {
                    if (uc.getResponseCode() != 200) {
                        LOGGER.log(Level.WARNING, "Request to mirror failed with code " + uc.getResponseCode()
                                + " from server: " + m);
                        continue;
                    }
                    long length = uc.getContentLengthLong();
                    //Transfer data
                    try (FileOutputStream fos = new FileOutputStream(destination)) {
                        long progress = 0;
                        progressConsumer.accept(0f);
                        byte[] buffer = new byte[BUFFER_SIZE];
                        long transferred = 0;
                        int r;
                        while ((r = in.read(buffer)) > 0) {
                            fos.write(buffer, 0, r);
                            transferred += r;
                            long newprogress = transferred * 100 / length;
                            if (newprogress > progress) {
                                progress = newprogress;
                                progressConsumer.accept((float) progress);
                            }
                        }
                        fos.flush();
                    }
                    //Cleanup
                    uc.disconnect();
                    return;
                } catch (IOException e) {
                    //Ignore error, will try fallback in follow-up code
                    lastException = e;
                    LOGGER.log(Level.WARNING, "Request failed with exception on mirror: " + m
                            + " (" + e.getClass().getSimpleName()
                            + (e.getMessage() == null ? "" : (": " + e.getMessage())) + ")");
                    uc.disconnect();
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Request failed with exception on mirror: " + m, e);
                lastException = e;
            }
        }
        //Throw exception if no download was successful
        if (lastException != null) {
            throw new IOException("None of the supplied mirrors were working", lastException);
        } else {
            throw new IOException("None of the supplied mirrors were working");
        }
    }

    private static String loadJCefMavenVersion() throws IOException {
        Map object;
        try (InputStream in = PackageDownloader.class.getResourceAsStream("/jcefmaven_build_meta.json")) {
            if (in == null) {
                throw new IOException("/jcefmaven_build_meta.json not found on class path");
            }
            object = GSON.fromJson(new InputStreamReader(in), Map.class);
        } catch (Exception e) {
            throw new IOException("Invalid json content in jcefmaven_build_meta.json", e);
        }
        return (String) object.get("version");
    }
}

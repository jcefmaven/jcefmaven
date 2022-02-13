package me.friwi.jcefmaven.impl.step.fetch;

import me.friwi.jcefmaven.CefBuildInfo;
import me.friwi.jcefmaven.EnumPlatform;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
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
    private static final Logger LOGGER = Logger.getLogger(PackageDownloader.class.getName());

    private static final String DOWNLOAD_URL = "https://github.com/jcefmaven/jcefmaven/releases/download/{mvn_version}/jcef-natives-{platform}-{tag}.jar";
    private static final String FALLBACK_DOWNLOAD_URL = "https://repo.maven.apache.org/maven2/me/friwi/" +
            "jcef-natives-{platform}/{tag}/jcef-natives-{platform}-{tag}.jar";

    private static final int BUFFER_SIZE = 16 * 1024;

    public static void downloadNatives(CefBuildInfo info, EnumPlatform platform, File destination, Consumer<Float> progressConsumer) throws IOException {
        Objects.requireNonNull(info, "info cannot be null");
        Objects.requireNonNull(platform, "platform cannot be null");
        Objects.requireNonNull(destination, "destination cannot be null");
        Objects.requireNonNull(progressConsumer, "progressConsumer cannot be null");
        //Create target file
        if (!destination.createNewFile()) {
            throw new IOException("Could not create target file " + destination.getAbsolutePath());
        }
        //Load maven version
        String mvn_version = loadJCefMavenVersion();
        //Open connection with authentication to github
        URL url = new URL(DOWNLOAD_URL
                .replace("{platform}", platform.getIdentifier())
                .replace("{tag}", info.getReleaseTag())
                .replace("{mvn_version}", mvn_version));
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        InputStream in = null;
        try {
            in = uc.getInputStream();
        } catch (IOException e) {
            //Ignore error, will try fallback in follow up code
            uc.disconnect();
        }
        if (uc.getResponseCode() != 200) {
            //Error while requesting from github, use maven central instead
            //(only accept code 200 to make sure that there is no partial response downloaded, like a redirection)
            LOGGER.log(Level.WARNING, "Requesting from sonatype due to " + uc.getResponseCode() + " from github");
            url = new URL(FALLBACK_DOWNLOAD_URL
                    .replace("{platform}", platform.getIdentifier())
                    .replace("{tag}", info.getReleaseTag()));
            uc = (HttpURLConnection) url.openConnection();
            in = uc.getInputStream();
        }
        long length = uc.getContentLengthLong();
        //Transfer data
        FileOutputStream fos = new FileOutputStream(destination);
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
        //Cleanup
        fos.close();
        in.close();
        uc.disconnect();
    }

    private static String loadJCefMavenVersion() throws IOException {
        JSONParser parser = new JSONParser();
        Object object;
        try {
            object = parser.parse(new InputStreamReader(PackageDownloader.class.getResourceAsStream("/jcefmaven_build_meta.json")));
        } catch (Exception e) {
            throw new IOException("Invalid json content in jcefmaven_build_meta.json", e);
        }
        if (!(object instanceof JSONObject)) throw new IOException("jcefmaven_build_meta.json did not contain a valid json body");
        return (String) ((JSONObject) object).get("version");
    }
}

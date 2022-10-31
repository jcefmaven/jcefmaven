package me.friwi.jcefmaven;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.cef.CefApp;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;


/**
 * Provides information about jcefmaven builds
 *
 * @author Fritz Windisch
 */
public class CefBuildInfo {
    private static final Gson GSON = new Gson();
    private static CefBuildInfo LOCAL_BUILD_INFO = null;
    private final String jcefUrl;
    private final String releaseTag;
    private final String releaseUrl;
    private final String platform;

    private CefBuildInfo(String jcefUrl, String releaseTag, String releaseUrl, String platform) {
        this.jcefUrl = jcefUrl;
        this.releaseTag = releaseTag;
        this.releaseUrl = releaseUrl;
        this.platform = platform;
    }

    /**
     * Reads the in-use jcefmaven build info from classpath
     *
     * @return jcefmaven build info
     * @throws NullPointerException if the build info "/build_meta.json" does not exist on classpath
     * @throws IOException          if reading the build info failed
     */
    public static CefBuildInfo fromClasspath() throws IOException {
        if (LOCAL_BUILD_INFO == null) {
            LOCAL_BUILD_INFO = loadData(
                    Objects.requireNonNull(CefApp.class.getResourceAsStream("/build_meta.json"),
                            "The build_meta.json file from the jcef-api artifact could not be read")
            );
        }
        return LOCAL_BUILD_INFO;
    }

    /**
     * Loads a CefBuildInfo instance from a file
     *
     * @param file the file to read
     * @return jcefmaven build info
     * @throws IOException if reading the file failed
     */
    public static CefBuildInfo fromFile(File file) throws IOException {
        return loadData(Files.newInputStream(file.toPath()));
    }

    private static CefBuildInfo loadData(InputStream in) throws IOException {
        Map object;
        try {
            object = GSON.fromJson(new InputStreamReader(in), Map.class);
        } catch (JsonParseException e) {
            throw new IOException("Invalid json content in build_meta.json", e);
        } finally {
            in.close();
        }
        return new CefBuildInfo(
                Objects.requireNonNull(object.get("jcef_url"), "No jcef_url specified in build_meta.json").toString(),
                Objects.requireNonNull(object.get("release_tag"), "No release_tag specified in build_meta.json").toString(),
                Objects.requireNonNull(object.get("release_url"), "No release_url specified in build_meta.json").toString(),
                Objects.requireNonNull(object.get("platform"), "No platform specified in build_meta.json").toString());
    }

    public String getJcefUrl() {
        return jcefUrl;
    }

    public String getReleaseTag() {
        return releaseTag;
    }

    public String getReleaseUrl() {
        return releaseUrl;
    }

    public String getPlatform() {
        return platform;
    }
}

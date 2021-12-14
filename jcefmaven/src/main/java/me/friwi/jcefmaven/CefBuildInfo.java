package me.friwi.jcefmaven;

import org.cef.CefApp;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Objects;


/**
 * Provides information about jcefmaven builds
 *
 * @author Fritz Windisch
 */
public class CefBuildInfo {
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
        return loadData(new FileInputStream(file));
    }

    private static CefBuildInfo loadData(InputStream in) throws IOException {
        JSONParser parser = new JSONParser();
        Object object = null;
        try {
            object = parser.parse(new InputStreamReader(in));
        } catch (ParseException e) {
            throw new IOException("Invalid json content in build_meta.json", e);
        }
        if (!(object instanceof JSONObject)) throw new IOException("build_meta.json did not contain a valid json body");
        JSONObject jsonObject = (JSONObject) object;
        return new CefBuildInfo(
                Objects.requireNonNull(jsonObject.get("jcef_url"), "No jcef_url specified in build_meta.json").toString(),
                Objects.requireNonNull(jsonObject.get("release_tag"), "No release_tag specified in build_meta.json").toString(),
                Objects.requireNonNull(jsonObject.get("release_url"), "No release_url specified in build_meta.json").toString(),
                Objects.requireNonNull(jsonObject.get("platform"), "No platform specified in build_meta.json").toString());
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

package me.friwi.jcefmaven;

import com.google.gson.Gson;
import me.friwi.jcefmaven.apibuild.JCefApiBuildPoller;
import me.friwi.jcefmaven.nativesbuild.JCefNativesBuildPoller;
import me.friwi.jcefmaven.pluginbuild.JCefMavenPluginPoller;
import me.friwi.jcefmaven.util.FileUtils;
import me.friwi.jcefmaven.util.InputStreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Application that assembles jcef natives, builds jcef api and a jcef bundle maven plugin
 * for convenient use in a maven environment.
 *
 * @author Fritz Windisch
 */
public class Main {
    public static final String JOGAMP_VERSION = "2.3.2";

    public static final String NATIVES_RELEASES_URL = "https://api.github.com/repos/jcefbuild/jcefbuild/releases?page={page}";
    public static final String NATIVES_FIRST_SUPPORTED_RELEASE_NAME = "84.3.8+gc8a556f+chromium-84.0.4147.105";
    public static final String NATIVES_PACKAGING_DIR = "java-cef-build-bin";
    public static final String REPO_TIMESTAMP_LOCATION = "maven/last_updated.txt";
    public static final String NATIVES_RELEASE_STORAGE_LOCATION = "maven/org/cef/jcef-natives{classifier}/{version}/jcef-natives{classifier}-{version}.jar";
    public static final String NATIVES_RELEASE_POM_NAME = "jcef-natives{classifier}-{version}.pom";
    public static final String LICENSE_FILE_NAME = "LICENSE.txt";

    public static final String API_RELEASES_URL = "https://api.github.com/repos/chromiumembedded/java-cef/commits?page={page}";
    public static final String API_CEF_VERSION_COMMIT_PREFIX = "Update to CEF version ";
    public static final String API_CEF_FIRST_SUPPORTED_RELEASE_NAME = "84.3.8+gc8a556f+chromium-84.0.4147.105";
    public static final String API_DOWNLOAD_URL = "https://codeload.github.com/chromiumembedded/java-cef/zip/{ref}";
    public static final String API_RELEASE_STORAGE_LOCATION = "maven/org/cef/jcef-api/{version}/jcef-api-{version}{classifier}.jar";
    public static final String API_RELEASE_POM_NAME = "jcef-api-{version}.pom";
    public static final String API_SOURCES_DIR = "src/main/java/org";
    public static final String API_RESOURCES_DIR = "src/main/resources";
    public static final String API_SOURCES_PREFIX = "java-cef-{ref}/java/org";
    public static final String API_ZIP_DIR = "java-cef-{ref}";

    public static final String LOADER_DOWNLOAD_URL = "https://github.com/jcefmaven/jcefloader/archive/master.zip";
    public static final String LOADER_CONTENT_DIR = "jcefloader-master";
    public static final String LOADER_RELEASE_STORAGE = "maven/org/cef/jcef-loader/{version}/jcef-loader-{version}{classifier}.jar";
    public static final String LOADER_RELEASE_POM_NAME = "jcef-loader-{version}.pom";

    public static final String JCEF_PACKAGE_DOWNLOAD_URL = "https://github.com/jcefmaven/jcef/archive/master.zip";
    public static final String JCEF_PACKAGE_CONTENT_DIR = "jcef-master";
    public static final String JCEF_PACKAGE_RELEASE_STORAGE = "maven/org/cef/jcef/{version}/jcef-{version}{classifier}.jar";
    public static final String JCEF_PACKAGE_RELEASE_POM_NAME = "jcef-{version}.pom";

    public static final String JCEF_MAIN_PACKAGE_DOWNLOAD_URL = "https://github.com/jcefmaven/jcefmain/archive/master.zip";
    public static final String JCEF_MAIN_PACKAGE_CONTENT_DIR = "jcefmain-master";
    public static final String JCEF_MAIN_PACKAGE_RELEASE_STORAGE = "maven/org/cef/jcef-main/{version}/jcef-main-{version}{classifier}.jar";
    public static final String JCEF_MAIN_PACKAGE_RELEASE_POM_NAME = "jcef-main-{version}.pom";

    public static final String PLUGIN_RELEASES_URL = "https://api.github.com/repos/jcefmaven/jcefbundlemavenplugin/tags?page={page}";
    public static final String PLUGIN_DOWNLOAD_URL = "https://codeload.github.com/jcefmaven/jcefbundlemavenplugin/zip/{ref}";
    public static final String PLUGIN_CONTENT_DIR = "jcefbundlemavenplugin-{ref}";
    public static final String PLUGIN_RELEASE_STORAGE = "maven/org/cef/jcef-bundle-maven-plugin/{version}/jcef-bundle-maven-plugin-{version}{classifier}.jar";
    public static final String PLUGIN_RELEASE_POM_NAME = "jcef-bundle-maven-plugin-{version}.pom";

    public static final String MAVEN_COMMAND = "mvn";

    public static final File TEMP_DIR = new File("temp");


    public static Configuration CONFIGURATION = null;

    public static void main(String[] args) throws IOException, InterruptedException {
        File configFile = new File("config.json");
        if (!configFile.exists()) {
            System.out.println("Please create a config file called \"config.json\" " +
                    "in the working directory of this process!");
            return;
        }
        Gson gson = new Gson();
        InputStream in = new FileInputStream(configFile);
        CONFIGURATION = gson.fromJson(
                InputStreamUtils.readInputStreamToString(in, 4096, StandardCharsets.UTF_8),
                Configuration.class);

        //Begin crawling
        while (true) {
            try {
                JCefNativesBuildPoller.refreshReleases();
                JCefApiBuildPoller.refreshReleases();
                JCefMavenPluginPoller.refreshReleases();

                //Clean own maven repository
                File m2 = new File(System.getProperty("user.home"), ".m2");
                FileUtils.deleteDirectory(m2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(CONFIGURATION.getCrawlInterval());
        }
    }
}

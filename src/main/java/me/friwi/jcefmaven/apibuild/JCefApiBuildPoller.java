package me.friwi.jcefmaven.apibuild;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.friwi.jcefmaven.Main;
import me.friwi.jcefmaven.nativesbuild.JCefNativesBuildPoller;
import me.friwi.jcefmaven.nativesbuild.NativeBuildEnum;
import me.friwi.jcefmaven.util.json.RemoteJsonIterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This class polls github for new jcef api releases.
 *
 * @author Fritz Windisch
 */
public class JCefApiBuildPoller {
    public static void refreshReleases() throws IOException {
        RemoteJsonIterator iterator = new RemoteJsonIterator(Main.API_RELEASES_URL);
        Deque<JsonObject> commits = new ArrayDeque<>();
        while (iterator.hasNext()) {
            JsonElement el = iterator.next();
            if (el.isJsonObject()) {
                JsonObject commit = el.getAsJsonObject();
                commits.push(commit);
                String name = commit.getAsJsonObject("commit").get("message").getAsString();
                if (name.equals(Main.API_CEF_VERSION_COMMIT_PREFIX + Main.API_CEF_FIRST_SUPPORTED_RELEASE_NAME)) break;
            }
        }
        String nativeVersion = null;
        int buildId = 1;
        while (commits.size() > 0) {
            JsonObject commit = commits.poll();
            String name = commit.getAsJsonObject("commit").get("message").getAsString();
            if (name.startsWith(Main.API_CEF_VERSION_COMMIT_PREFIX)) {
                nativeVersion = name.substring(Main.API_CEF_VERSION_COMMIT_PREFIX.length());
                buildId = 1;
            } else {
                buildId++;
            }

            //Check if this native version exists already. Can not build when there are no natives available yet
            String nativeReleaseName = null;
            for (File versionDir : JCefNativesBuildPoller.getReleaseStorageLocation("t", NativeBuildEnum.WIN64.getName()) //Jar file
                    .getParentFile() //Specific version
                    .getParentFile() //All versions
                    .listFiles()) { //List all versions
                if (versionDir.getName().equals(nativeVersion)) {
                    nativeReleaseName = versionDir.getName();
                    break;
                }
            }
            if (nativeReleaseName == null) {
                //Can not build yet
                break;
            }


            String version = nativeVersion + "-build-" + buildId;
            String ref = commit.get("sha").getAsString();

            File destLast = getLastBuildStepReleaseStorageLocation(version, null);
            if (!destLast.exists()) {
                try {
                    JCefApiReleaseBuilder.buildApiRelease(version, ref, nativeReleaseName);
                } catch (RuntimeException e) {
                    System.err.println("Error while building " + version + " from " + ref);
                    e.printStackTrace();
                }
            }
        }
    }

    public static File getReleaseStorageLocation(String version, String classifier) {
        return new File(Main.CONFIGURATION.getWorkspaceDir(),
                Main.API_RELEASE_STORAGE_LOCATION
                        .replace("{version}", version)
                        .replace("{classifier}", (classifier == null || classifier.isEmpty()) ? "" : ("-" + classifier)));
    }

    public static File getLastBuildStepReleaseStorageLocation(String version, String classifier) {
        return new File(Main.CONFIGURATION.getWorkspaceDir(),
                Main.JCEF_MAIN_PACKAGE_RELEASE_STORAGE
                        .replace("{version}", version)
                        .replace("{classifier}", (classifier == null || classifier.isEmpty()) ? "" : ("-" + classifier)));
    }
}

package me.friwi.jcefmaven.nativesbuild;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.friwi.jcefmaven.Main;
import me.friwi.jcefmaven.util.json.RemoteJsonIterator;

import java.io.File;
import java.io.IOException;

/**
 * This class initiates the build of new native versions from jcefbuild
 *
 * @author Fritz Windisch
 */
public class JCefNativesBuildPoller {
    public static void refreshReleases() throws IOException {
        RemoteJsonIterator iterator = new RemoteJsonIterator(Main.NATIVES_RELEASES_URL);
        while (iterator.hasNext()) {
            JsonElement el = iterator.next();
            if (el.isJsonObject()) {
                JsonObject release = el.getAsJsonObject();
                String name = release.get("name").getAsString();

                //Remove jcefbuild version from build name to match jcef version
                String[] parts = name.split("-");
                name = parts[1];
                for (int i = 2; i < parts.length; i++) {
                    name += "-" + parts[i];
                }

                File dest = getReleaseStorageLocation(name, NativeBuildEnum.WIN64.getName());
                if (!dest.exists()) {
                    JCefNativeReleaseBuilder.buildByJsonRelease(name, release);
                }

                if (name.equals(Main.NATIVES_FIRST_SUPPORTED_RELEASE_NAME)) break;
            }
        }
    }

    public static File getReleaseStorageLocation(String version, String classifier) {
        return new File(Main.CONFIGURATION.getWorkspaceDir(),
                Main.NATIVES_RELEASE_STORAGE_LOCATION
                        .replace("{version}", version)
                        .replace("{classifier}", (classifier == null || classifier.isEmpty()) ? "" : ("-" + classifier)));
    }
}

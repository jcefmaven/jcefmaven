package me.friwi.jcefmaven.pluginbuild;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.friwi.jcefmaven.Main;
import me.friwi.jcefmaven.util.json.RemoteJsonIterator;

import java.io.File;
import java.io.IOException;

/**
 * Initiates new maven plugin builds
 *
 * @author Fritz Windisch
 */
public class JCefMavenPluginPoller {
    public static void refreshReleases() throws IOException {
        RemoteJsonIterator iterator = new RemoteJsonIterator(Main.PLUGIN_RELEASES_URL);
        while (iterator.hasNext()) {
            JsonElement el = iterator.next();
            if (el.isJsonObject()) {
                JsonObject release = el.getAsJsonObject();
                String name = release.get("name").getAsString();

                File dest = getReleaseStorageLocation(name, null);
                if (!dest.exists()) {
                    JCefMavenPluginBuilder.buildByJsonRelease(name, release);
                }
            }
        }
    }

    public static File getReleaseStorageLocation(String version, String classifier) {
        return new File(Main.CONFIGURATION.getWorkspaceDir(),
                Main.PLUGIN_RELEASE_STORAGE
                        .replace("{version}", version)
                        .replace("{classifier}", (classifier == null || classifier.isEmpty()) ? "" : ("-" + classifier)));
    }
}

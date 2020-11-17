package me.friwi.jcefmaven.pluginbuild;

import com.google.gson.JsonObject;
import me.friwi.jcefmaven.Main;
import me.friwi.jcefmaven.metabuild.PackageBuilder;

import java.io.IOException;

/**
 * Downloads, builds and deploys new maven plugin versions
 *
 * @author Fritz Windisch
 */
public class JCefMavenPluginBuilder {
    private static String NATIVE_POM = null;

    public static void buildByJsonRelease(String name, JsonObject release) throws IOException {
        System.out.println("Building plugin version for " + name);
        //Build package from source url
        String ref = release.getAsJsonObject("commit").get("sha").getAsString();
        PackageBuilder.build(name, name, ref,
                Main.PLUGIN_DOWNLOAD_URL,
                Main.PLUGIN_CONTENT_DIR,
                Main.PLUGIN_RELEASE_POM_NAME,
                Main.PLUGIN_RELEASE_STORAGE);
        System.out.println("Plugin published for version " + name);
    }
}

package me.friwi.jcefmaven.impl.step.fetch;

import me.friwi.jcefmaven.CefBuildInfo;
import me.friwi.jcefmaven.EnumPlatform;
import org.cef.CefApp;

import java.io.InputStream;

/**
 * Class used to extract natives from classpath.
 *
 * @author Fritz Windisch
 */
public class PackageClasspathStreamer {
    private static final String LOCATION = "/jcef-natives-{platform}-{tag}.tar.gz";

    public static InputStream streamNatives(CefBuildInfo info, EnumPlatform platform) {
        return CefApp.class.getResourceAsStream(LOCATION
                .replace("{platform}", platform.getIdentifier())
                .replace("{tag}", info.getReleaseTag()));
    }
}

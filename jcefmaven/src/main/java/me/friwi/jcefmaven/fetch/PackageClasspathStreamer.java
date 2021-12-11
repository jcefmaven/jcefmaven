package me.friwi.jcefmaven.fetch;

import me.friwi.jcefmaven.platform.EnumPlatform;
import me.friwi.jcefmaven.version.CefBuildInfo;
import org.cef.CefApp;

import java.io.IOException;
import java.io.InputStream;

public class PackageClasspathStreamer {
    private static final String LOCATION = "/jcef-natives-{platform}-{tag}.tar.gz";

    public static InputStream streamNatives(CefBuildInfo info, EnumPlatform platform) {
        return CefApp.class.getResourceAsStream(LOCATION
                .replace("{platform}", platform.getIdentifier())
                .replace("{tag}", info.getReleaseTag()));
    }
}

package me.friwi.jcefmaven;

import me.friwi.jcefmaven.fetch.PackageDownloader;
import me.friwi.jcefmaven.init.CefInitializationException;
import me.friwi.jcefmaven.platform.EnumPlatform;
import me.friwi.jcefmaven.platform.UnsupportedPlatformException;
import me.friwi.jcefmaven.version.CefBuildInfo;
import org.cef.CefApp;

import java.io.IOException;

public class TestSimple {
    public static void main(String args[]) throws UnsupportedPlatformException, IOException, InterruptedException, CefInitializationException {
        CefAppBuilder builder = new CefAppBuilder();
        CefApp app = builder.build();
    }
}

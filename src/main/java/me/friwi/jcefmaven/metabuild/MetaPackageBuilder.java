package me.friwi.jcefmaven.metabuild;

import me.friwi.jcefmaven.Main;

import java.io.IOException;

/**
 * This class is used to build all maven api packages per version.
 *
 * @author Fritz Windisch
 */
public class MetaPackageBuilder {
    public static void buildMetaPackages(String version, String nativeVersion, String ref) throws IOException {
        //Build loader
        PackageBuilder.build(version, nativeVersion, ref,
                Main.LOADER_DOWNLOAD_URL,
                Main.LOADER_CONTENT_DIR,
                Main.LOADER_RELEASE_POM_NAME,
                Main.LOADER_RELEASE_STORAGE);
        //Build jcef meta package
        PackageBuilder.build(version, nativeVersion, ref,
                Main.JCEF_PACKAGE_DOWNLOAD_URL,
                Main.JCEF_PACKAGE_CONTENT_DIR,
                Main.JCEF_PACKAGE_RELEASE_POM_NAME,
                Main.JCEF_PACKAGE_RELEASE_STORAGE);
        //Build jcef-main meta package
        PackageBuilder.build(version, nativeVersion, ref,
                Main.JCEF_MAIN_PACKAGE_DOWNLOAD_URL,
                Main.JCEF_MAIN_PACKAGE_CONTENT_DIR,
                Main.JCEF_MAIN_PACKAGE_RELEASE_POM_NAME,
                Main.JCEF_MAIN_PACKAGE_RELEASE_STORAGE);
    }
}

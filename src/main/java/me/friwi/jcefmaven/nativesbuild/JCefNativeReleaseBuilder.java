package me.friwi.jcefmaven.nativesbuild;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.friwi.jcefmaven.Main;
import me.friwi.jcefmaven.util.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Assembles and deploys the native maven artifacts
 *
 * @author Fritz Windisch
 */
public class JCefNativeReleaseBuilder {
    private static String NATIVE_POM = null;

    public static void buildByJsonRelease(String name, JsonObject release) throws IOException {
        System.out.println("Building natives version for " + name);
        //Clean temp dir
        FileUtils.deleteDirectory(Main.TEMP_DIR);
        //Download assets
        JsonArray arr = release.getAsJsonArray("assets");
        for (JsonElement el : arr) {
            JsonObject asset = el.getAsJsonObject();
            FileDownloader.download(asset.get("browser_download_url").getAsString(), asset.get("name").getAsString());
        }

        //Build the native jars
        for (NativeBuildEnum nativeBuild : NativeBuildEnum.values()) {
            performBuild(name, nativeBuild);
        }
        //Update timestamp, as there are new artefacts available
        RepoTimestamp.updateTimestamp();
        //Clean temp dir
        FileUtils.deleteDirectory(Main.TEMP_DIR);
        System.out.println("Natives published for version " + name);
    }

    private static void performBuild(String name, NativeBuildEnum nativeBuild) throws IOException {
        File buildDir = new File(Main.TEMP_DIR, nativeBuild.getName());
        //Repackage contents
        String contentName = "jcef-natives-" + nativeBuild.getName() + ".jar";
        File create = new File(buildDir, contentName);
        System.out.println("Building " + contentName);
        buildDir.mkdirs();
        create.createNewFile();
        ZipInputStream in = new ZipInputStream(new FileInputStream(new File(Main.TEMP_DIR, nativeBuild.getAssetName())));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(create));
        byte[] buff = new byte[4096];
        ZipEntry entry;
        while ((entry = in.getNextEntry()) != null) {
            boolean license = entry.getName().equals(Main.NATIVES_PACKAGING_DIR + "/" + Main.LICENSE_FILE_NAME);
            if (license || (entry.getName().startsWith(nativeBuild.getRelevantContents()) && entry.getName().length() > nativeBuild.getRelevantContents().length())) {
                if (entry.getName().endsWith("jcef.jar") || entry.getName().endsWith("jcef-tests.jar")) {
                    in.closeEntry();
                    continue;
                }
                out.putNextEntry(new ZipEntry(license ? Main.LICENSE_FILE_NAME : entry.getName().substring(nativeBuild.getRelevantContents().length())));
                if (!entry.isDirectory()) {
                    int r;
                    while ((r = in.read(buff)) != -1) {
                        out.write(buff, 0, r);
                    }
                }
                out.closeEntry();
            }
            in.closeEntry();
        }
        in.close();
        out.finish();
        out.close();
        //Create a meta jar, that contains the created jar (so that the created jar is on the classpath for extraction)
        //in the repository
        File deployLocation = JCefNativesBuildPoller.getReleaseStorageLocation(name, nativeBuild.getName());
        deployLocation.getParentFile().mkdirs();
        deployLocation.createNewFile();
        out = new ZipOutputStream(new FileOutputStream(deployLocation));
        FileInputStream fis = new FileInputStream(create);
        out.putNextEntry(new ZipEntry(contentName));
        int r;
        while ((r = fis.read(buff)) != -1) {
            out.write(buff, 0, r);
        }
        fis.close();
        out.closeEntry();
        out.finish();
        out.close();
        //Create pom.xml
        if (NATIVE_POM == null) NATIVE_POM = InputStreamUtils.readInputStreamToString(
                JCefNativeReleaseBuilder.class.getResourceAsStream("/natives-pom.xml"),
                16 * 1024, StandardCharsets.UTF_8);
        String pom = NATIVE_POM.replace("{version}", name)
                .replace("{classifier}", nativeBuild.getName())
                .replace("{jogamp-classifier}", nativeBuild.getJogampName())
                .replace("{jogamp-version}", Main.JOGAMP_VERSION);
        File pomLocation = new File(deployLocation.getParentFile(), Main.NATIVES_RELEASE_POM_NAME
                .replace("{classifier}", "-" + nativeBuild.getName())
                .replace("{version}", name));
        pomLocation.createNewFile();
        PrintWriter writer = new PrintWriter(new FileOutputStream(pomLocation));
        writer.println(pom);
        writer.flush();
        writer.close();
        //Hash artefact and pom.xml
        FileHasher.createHashes(deployLocation);
        FileHasher.createHashes(pomLocation);
    }
}

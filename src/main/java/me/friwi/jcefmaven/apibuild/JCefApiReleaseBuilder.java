package me.friwi.jcefmaven.apibuild;

import me.friwi.jcefmaven.Main;
import me.friwi.jcefmaven.metabuild.MavenBuilder;
import me.friwi.jcefmaven.metabuild.MetaPackageBuilder;
import me.friwi.jcefmaven.nativesbuild.JCefNativeReleaseBuilder;
import me.friwi.jcefmaven.util.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class builds jcef-api artifacts.
 *
 * @author Fritz Windisch
 */
public class JCefApiReleaseBuilder {
    private static String API_POM = null;

    public static void buildApiRelease(String version, String ref, String nativeReleaseName) throws IOException {
        System.out.println("Building API " + version + " with ref " + ref + " and natives " + nativeReleaseName);
        //Clean temp dir
        FileUtils.deleteDirectory(Main.TEMP_DIR);
        //Download sources
        File sources = FileDownloader.download(Main.API_DOWNLOAD_URL.replace("{ref}", ref), "sources.zip");
        //Extract sources
        File targetDir = new File(Main.TEMP_DIR, Main.API_SOURCES_DIR);
        File resourcesDir = new File(Main.TEMP_DIR, Main.API_RESOURCES_DIR);
        String prefix = Main.API_SOURCES_PREFIX.replace("{ref}", ref);
        ZipInputStream in = new ZipInputStream(new FileInputStream(sources));
        byte[] buff = new byte[4096];
        ZipEntry entry;
        while ((entry = in.getNextEntry()) != null) {
            boolean license = entry.getName().equals(Main.API_ZIP_DIR.replace("{ref}", ref) + "/" + Main.LICENSE_FILE_NAME);
            if (license || entry.getName().startsWith(prefix)) {
                File f = new File(license ? resourcesDir : targetDir, license ? Main.LICENSE_FILE_NAME : entry.getName().substring(prefix.length()));
                if (entry.isDirectory()) {
                    f.mkdirs();
                } else {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                    FileOutputStream fos = new FileOutputStream(f);
                    int r;
                    while ((r = in.read(buff)) != -1) {
                        fos.write(buff, 0, r);
                    }
                    fos.flush();
                    fos.close();
                }
            }
            in.closeEntry();
        }
        in.close();
        //Create pom.xml
        if (API_POM == null) API_POM = InputStreamUtils.readInputStreamToString(
                JCefNativeReleaseBuilder.class.getResourceAsStream("/api-pom.xml"),
                16 * 1024, StandardCharsets.UTF_8);
        String pom = API_POM.replace("{version}", version)
                .replace("{ref}", ref)
                .replace("{jogamp-version}", Main.JOGAMP_VERSION);
        File pomLocation = new File(Main.TEMP_DIR, "pom.xml");
        pomLocation.createNewFile();
        PrintWriter writer = new PrintWriter(new FileOutputStream(pomLocation));
        writer.println(pom);
        writer.flush();
        writer.close();

        //Extract macos compile dependencies
        in = new ZipInputStream(JCefApiReleaseBuilder.class.getResourceAsStream("/macosx-compile-dependencies.zip"));
        File classesDir = new File(Main.TEMP_DIR, "target" + File.separator + "classes");
        while ((entry = in.getNextEntry()) != null) {
            File f = new File(classesDir, entry.getName());
            if (entry.isDirectory()) {
                f.mkdirs();
            } else {
                f.getParentFile().mkdirs();
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(f);
                int r;
                while ((r = in.read(buff)) != -1) {
                    fos.write(buff, 0, r);
                }
                fos.flush();
                fos.close();
            }
            in.closeEntry();
        }
        in.close();

        //Invoke maven build with sources and javadoc
        MavenBuilder.invokeMavenBuild(Main.TEMP_DIR);

        //Push artefacts to repo
        File buildDir = new File(Main.TEMP_DIR, "target");
        File jarExport = JCefApiBuildPoller.getReleaseStorageLocation(version, null);
        File javadocExport = JCefApiBuildPoller.getReleaseStorageLocation(version, "javadoc");
        File sourcesExport = JCefApiBuildPoller.getReleaseStorageLocation(version, "sources");
        File pomExport = new File(JCefApiBuildPoller.getReleaseStorageLocation(version, null).getParentFile(),
                Main.API_RELEASE_POM_NAME.replace("{version}", version));
        File jarLocal = new File(buildDir, jarExport.getName());
        File javadocLocal = new File(buildDir, javadocExport.getName());
        File sourcesLocal = new File(buildDir, sourcesExport.getName());
        File pomLocal = new File(Main.TEMP_DIR, "pom.xml");

        jarExport.getParentFile().mkdirs();
        Files.move(jarLocal.toPath(), jarExport.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.move(javadocLocal.toPath(), javadocExport.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.move(sourcesLocal.toPath(), sourcesExport.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.move(pomLocal.toPath(), pomExport.toPath(), StandardCopyOption.REPLACE_EXISTING);

        //Create hashes for new files
        FileHasher.createHashes(jarExport);
        FileHasher.createHashes(javadocExport);
        FileHasher.createHashes(sourcesExport);
        FileHasher.createHashes(pomExport);

        //Update timestamp, as there are new artefacts available
        RepoTimestamp.updateTimestamp();

        //Clean temp dir
        FileUtils.deleteDirectory(Main.TEMP_DIR);

        System.out.println("API published for version " + version);

        //Build meta packages (loader, maven plugin and parent packages)
        MetaPackageBuilder.buildMetaPackages(version, nativeReleaseName, ref);
    }
}

package me.friwi.jcefmaven.metabuild;

import me.friwi.jcefmaven.Main;
import me.friwi.jcefmaven.util.FileDownloader;
import me.friwi.jcefmaven.util.FileHasher;
import me.friwi.jcefmaven.util.FileUtils;
import me.friwi.jcefmaven.util.RepoTimestamp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Fetches, compiles and deploys a maven package
 *
 * @author Fritz Windisch
 */
public class PackageBuilder {
    public static void build(String version, String nativeVersion, String ref,
                             String downloadUrl, String contentDir, String releasePomName,
                             String releaseLocation) throws IOException {
        //Clear temp dir
        FileUtils.deleteDirectory(Main.TEMP_DIR);

        //Download sources
        File archive = new File(Main.TEMP_DIR, "sources.zip");
        FileDownloader.download(downloadUrl.replace("{ref}", ref), archive);

        //Extract
        FileUtils.extract(archive, Main.TEMP_DIR);

        //Replace variables
        File sourceDir = new File(Main.TEMP_DIR, contentDir.replace("{ref}", ref));
        Map<String, String> replacements = new HashMap<>();
        replacements.put("{version}", version);
        replacements.put("{native_version}", nativeVersion);
        replacements.put("{ref}", ref);
        FileUtils.replaceRecursive(sourceDir, replacements);

        //Invoke build
        MavenBuilder.invokeMavenBuild(sourceDir);

        //Push artefacts to repo
        File buildDir = new File(sourceDir, "target");
        File jarExport = getReleaseStorageLocation(releaseLocation, version, null);
        File javadocExport = getReleaseStorageLocation(releaseLocation, version, "javadoc");
        File sourcesExport = getReleaseStorageLocation(releaseLocation, version, "sources");
        File pomExport = new File(getReleaseStorageLocation(releaseLocation, version, null).getParentFile(),
                releasePomName.replace("{version}", version));
        File jarLocal = new File(buildDir, jarExport.getName());
        File javadocLocal = new File(buildDir, javadocExport.getName());
        File sourcesLocal = new File(buildDir, sourcesExport.getName());
        File pomLocal = new File(sourceDir, "pom.xml");

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

        //Clear temp dir
        FileUtils.deleteDirectory(Main.TEMP_DIR);
    }

    public static File getReleaseStorageLocation(String releaseLocation, String version, String classifier) {
        return new File(Main.CONFIGURATION.getWorkspaceDir(),
                releaseLocation
                        .replace("{version}", version)
                        .replace("{classifier}", (classifier == null || classifier.isEmpty()) ? "" : ("-" + classifier)));
    }
}

package me.friwi.jcefmaven.metabuild;

import me.friwi.jcefmaven.Main;

import java.io.File;
import java.io.IOException;

/**
 * This class is a small util to invoke a mvn package command.
 *
 * @author Fritz Windisch
 */
public class MavenBuilder {
    public static void invokeMavenBuild(File workDir) throws IOException {
        //Invoke build
        ProcessBuilder builder = new ProcessBuilder(Main.MAVEN_COMMAND, "package");
        builder.directory(workDir);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        Process process = builder.start();
        //Wait for maven finish
        try {
            int ret = process.waitFor();
            if (ret != 0) throw new RuntimeException("Maven task failed");
        } catch (InterruptedException e) {
            throw new RuntimeException("Could not wait for maven task", e);
        }
    }
}

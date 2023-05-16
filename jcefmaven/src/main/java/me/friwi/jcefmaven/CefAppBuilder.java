package me.friwi.jcefmaven;

import me.friwi.jcefmaven.impl.progress.ConsoleProgressHandler;
import me.friwi.jcefmaven.impl.step.check.CefInstallationChecker;
import me.friwi.jcefmaven.impl.step.extract.TarGzExtractor;
import me.friwi.jcefmaven.impl.step.fetch.PackageClasspathStreamer;
import me.friwi.jcefmaven.impl.step.fetch.PackageDownloader;
import me.friwi.jcefmaven.impl.step.init.CefInitializer;
import me.friwi.jcefmaven.impl.util.FileUtils;
import me.friwi.jcefmaven.impl.util.macos.UnquarantineUtil;
import org.cef.CefApp;
import org.cef.CefSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class used to configure the JCef environment. Specify
 * an installation directory, arguments to be passed to JCef
 * and configure the embedded {@link org.cef.CefSettings} to
 * your needs. When done, call {@link me.friwi.jcefmaven.CefAppBuilder#build()}
 * to create an {@link org.cef.CefApp} instance.
 * <p>
 * Example use:
 * <pre>
 * {@code
 * //Create a new CefAppBuilder instance
 * CefAppBuilder builder = new CefAppBuilder();
 *
 * //Configure the builder instance
 * builder.setInstallDir(new File("jcef-bundle")); //Default
 * builder.setProgressHandler(new ConsoleProgressHandler()); //Default
 * builder.addJCefArgs("--disable-gpu"); //Just an example
 * builder.getCefSettings().windowless_rendering_enabled = true; //Default - select OSR mode
 *
 * //Set an app handler. Do not use CefApp.addAppHandler(...), it will break your code on MacOSX!
 * builder.setAppHandler(new MavenCefAppHandlerAdapter(){...});
 *
 * //Build a CefApp instance using the configuration above
 * CefApp app = builder.build();
 * }
 * </pre>
 *
 * @author Fritz Windisch
 */
public class CefAppBuilder {
    private static final File DEFAULT_INSTALL_DIR = new File("jcef-bundle");
    private static final IProgressHandler DEFAULT_PROGRESS_HANDLER = new ConsoleProgressHandler();
    private static final List<String> DEFAULT_JCEF_ARGS = new LinkedList<>();
    private static final CefSettings DEFAULT_CEF_SETTINGS = new CefSettings();
    private final Object lock = new Object();
    private final List<String> jcefArgs;
    private final CefSettings cefSettings;
    private File installDir;
    private IProgressHandler progressHandler;
    private CefApp instance = null;
    private boolean building = false;
    private boolean installed = false;
    private final Set<String> mirrors;

    /**
     * Constructs a new CefAppBuilder instance.
     */
    public CefAppBuilder() {
        installDir = DEFAULT_INSTALL_DIR;
        progressHandler = DEFAULT_PROGRESS_HANDLER;
        jcefArgs = new LinkedList<>();
        jcefArgs.addAll(DEFAULT_JCEF_ARGS);
        cefSettings = DEFAULT_CEF_SETTINGS.clone();
        mirrors = new HashSet<>();
        mirrors.add("https://github.com/jcefmaven/jcefmaven/releases/download/{mvn_version}/jcef-natives-{platform}-{tag}.jar");
        mirrors.add("https://repo.maven.apache.org/maven2/me/friwi/jcef-natives-{platform}/{tag}/jcef-natives-{platform}-{tag}.jar");
    }

    /**
     * Sets the install directory to use. Defaults to "./jcef-bundle".
     *
     * @param installDir the directory to install to
     */
    public void setInstallDir(File installDir) {
        Objects.requireNonNull(installDir, "installDir cannot be null");
        this.installDir = installDir;
    }

    /**
     * Specify a progress handler to receive install progress updates.
     * Defaults to "new ConsoleProgressHandler()".
     *
     * @param progressHandler a progress handler to use
     */
    public void setProgressHandler(IProgressHandler progressHandler) {
        Objects.requireNonNull(progressHandler, "progressHandler cannot be null");
        this.progressHandler = progressHandler;
    }

    /**
     * Retrieves a mutable list of arguments to pass to the JCef library.
     * Arguments may contain spaces.
     * <p>
     * Due to installation using maven some arguments may be overwritten
     * again depending on your platform. Make sure to not specify arguments
     * that break the installation process (e.g. subprocess path, resources path...)!
     *
     * @return A mutable list of arguments to pass to the JCef library
     */
    public List<String> getJcefArgs() {
        return jcefArgs;
    }

    /**
     * Add one or multiple arguments to pass to the JCef library.
     * Arguments may contain spaces.
     * <p>
     * Due to installation using maven some arguments may be overwritten
     * again depending on your platform. Make sure to not specify arguments
     * that break the installation process (e.g. subprocess path, resources path...)!
     *
     * @param args the arguments to add
     */
    public void addJcefArgs(String... args) {
        Objects.requireNonNull(args, "args cannot be null");
        jcefArgs.addAll(Arrays.asList(args));
    }

    /**
     * Retrieve the embedded {@link org.cef.CefSettings} instance to change
     * configuration parameters.
     * <p>
     * Due to installation using maven some settings may be overwritten
     * again depending on your platform.
     *
     * @return the embedded {@link org.cef.CefSettings} instance
     */
    public CefSettings getCefSettings() {
        return cefSettings;
    }

    /**
     * Attach your own adapter to handle certain events in CEF.
     *
     * @param handlerAdapter the adapter to attach
     */
    public void setAppHandler(MavenCefAppHandlerAdapter handlerAdapter) {
        CefApp.addAppHandler(handlerAdapter);
    }

    /**
     * Get a copy of all mirrors that are currently in use. To add another mirror, use the setter.
     * Mirror urls can contain placeholders that are replaced when a fetch is attempted:
     * <br/>
     * {mvn_version}: The version of jcefmaven (e.g. 100.0.14.3) <br/>
     * {platform}: The desired platform for the download (e.g. linux-amd64) <br/>
     * {tag}: The desired version tag for the download (e.g. jcef-08efede+cef-100.0.14+g4e5ba66+chromium-100.0.4896.75)
     *
     * @return A copy of all mirrors that are currently in use. First element will be attempted first.
     */
    public Collection<String> getMirrors() {
        return new HashSet<>(mirrors);
    }

    /**
     * Set mirror urls that should be used when downloading jcef. First element will be attempted first.
     * Mirror urls can contain placeholders that are replaced when a fetch is attempted:
     * <br/>
     * {mvn_version}: The version of jcefmaven (e.g. 100.0.14.3) <br/>
     * {platform}: The desired platform for the download (e.g. linux-amd64) <br/>
     * {tag}: The desired version tag for the download (e.g. jcef-08efede+cef-100.0.14+g4e5ba66+chromium-100.0.4896.75)
     */
    public void setMirrors(Collection<String> mirrors) {
        Objects.requireNonNull(mirrors, "mirrors can not be null");
        this.mirrors.clear();
        this.mirrors.addAll(mirrors);
    }

    /**
     * Helper method to install the native libraries/resources. Useful for triggering an install ahead of actually
     * needing to create a CEF app instance.  This method is NOT thread safe and the caller must ensure only one thread
     * will call this method at a time.
     *
     * @return This builder instance
     * @throws IOException                  if an artifact could not be fetched or IO-actions on disk failed
     * @throws UnsupportedPlatformException if the platform is not supported
     */
    public CefAppBuilder install() throws IOException, UnsupportedPlatformException {
        // check if already installed
        if (this.installed) {
            return this;
        }
        this.progressHandler.handleProgress(EnumProgress.LOCATING, EnumProgress.NO_ESTIMATION);
        boolean installOk = CefInstallationChecker.checkInstallation(this.installDir);
        if (!installOk) {
            //Perform install
            //Clear install dir
            FileUtils.deleteDir(this.installDir);
            if (!this.installDir.mkdirs()) throw new IOException("Could not create installation directory");
            //Fetch a native input stream
            InputStream nativesIn = PackageClasspathStreamer.streamNatives(
                    CefBuildInfo.fromClasspath(), EnumPlatform.getCurrentPlatform());
            try {
                boolean downloading = false;
                if (nativesIn == null) {
                    this.progressHandler.handleProgress(EnumProgress.DOWNLOADING, EnumProgress.NO_ESTIMATION);
                    downloading = true;
                    File download = new File(this.installDir, "download.zip.temp");
                    PackageDownloader.downloadNatives(
                            CefBuildInfo.fromClasspath(), EnumPlatform.getCurrentPlatform(),
                            download, f -> {
                                this.progressHandler.handleProgress(EnumProgress.DOWNLOADING, f);
                            }, mirrors);
                    nativesIn = new ZipInputStream(new FileInputStream(download));
                    ZipEntry entry;
                    boolean found = false;
                    while ((entry = ((ZipInputStream) nativesIn).getNextEntry()) != null) {
                        if (entry.getName().endsWith(".tar.gz")) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new IOException("Downloaded artifact did not contain a .tar.gz archive");
                    }
                }
                //Extract a native bundle
                this.progressHandler.handleProgress(EnumProgress.EXTRACTING, EnumProgress.NO_ESTIMATION);
                TarGzExtractor.extractTarGZ(this.installDir, nativesIn);
                if (downloading) {
                    if (!new File(this.installDir, "download.zip.temp").delete()) {
                        throw new IOException("Could not remove downloaded temp file");
                    }
                }
            } finally {
                // make sure nativesIn is closed if any of the above fails
                if (nativesIn != null) {
                    nativesIn.close();
                }
            }
            //Install native bundle
            this.progressHandler.handleProgress(EnumProgress.INSTALL, EnumProgress.NO_ESTIMATION);
            //Remove quarantine on macosx
            if (EnumPlatform.getCurrentPlatform().getOs().isMacOSX()) {
                UnquarantineUtil.unquarantine(this.installDir);
            }
            //Lock installation
            if (!(new File(installDir, "install.lock").createNewFile())) {
                throw new IOException("Could not create install.lock to complete installation");
            }
        }
        this.installed = true;
        return this;
    }

    /**
     * Builds a {@link org.cef.CefApp} instance. When called multiple times,
     * will return the previously built instance. This method is thread-safe.
     *
     * @return a built {@link org.cef.CefApp} instance
     * @throws IOException                  if an artifact could not be fetched or IO-actions on disk failed
     * @throws UnsupportedPlatformException if the platform is not supported
     * @throws InterruptedException         if the installation process got interrupted
     * @throws CefInitializationException   if the initialization of JCef failed
     */
    public CefApp build() throws IOException, UnsupportedPlatformException, InterruptedException, CefInitializationException {
        //Check if we already have built an instance
        if (this.instance != null) {
            return this.instance;
        }
        //Check if we are in the process of building an instance
        synchronized (lock) {
            if (building) {
                //Check if instance was not created in the meantime
                //to prevent race conditions
                if (this.instance == null) {
                    //Wait until building completed on another thread
                    lock.wait();
                }
                return this.instance;
            }
            this.building = true;
        }
        this.install();
        this.progressHandler.handleProgress(EnumProgress.INITIALIZING, EnumProgress.NO_ESTIMATION);
        synchronized (lock) {
            //Setting the instance has to occur in the synchronized block
            //to prevent race conditions
            this.instance = CefInitializer.initialize(this.installDir, this.jcefArgs, this.cefSettings);
            //Add shutdown hook to attempt disposing our instance on jvm exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> this.instance.dispose()));
            //Notify progress handler
            this.progressHandler.handleProgress(EnumProgress.INITIALIZED, EnumProgress.NO_ESTIMATION);
            //Resume waiting threads
            lock.notifyAll();
        }
        return this.instance;
    }
}

package me.friwi.jcefmaven;

import me.friwi.jcefmaven.check.CefInstallationChecker;
import me.friwi.jcefmaven.extract.TarGzExtractor;
import me.friwi.jcefmaven.fetch.PackageClasspathStreamer;
import me.friwi.jcefmaven.fetch.PackageDownloader;
import me.friwi.jcefmaven.macos.UnquarantineUtil;
import me.friwi.jcefmaven.platform.EnumPlatform;
import me.friwi.jcefmaven.platform.UnsupportedPlatformException;
import me.friwi.jcefmaven.progress.ConsoleProgressHandler;
import me.friwi.jcefmaven.progress.EnumProgress;
import me.friwi.jcefmaven.progress.IProgressHandler;
import me.friwi.jcefmaven.utils.FileUtils;
import me.friwi.jcefmaven.version.CefBuildInfo;
import org.cef.CefApp;
import org.cef.CefSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CefAppBuilder {
    private static final File DEFAULT_INSTALL_DIR = new File("jcef-bundle");
    private static final IProgressHandler DEFAULT_PROGRESS_HANDLER = new ConsoleProgressHandler();
    private static final List<String> DEFAULT_JCEF_ARGS = new LinkedList<String>();
    private static final CefSettings DEFAULT_CEF_SETTINGS = new CefSettings();

    private File installDir;
    private IProgressHandler progressHandler;
    private List<String> jcefArgs;
    private CefSettings cefSettings;

    public CefAppBuilder() {
        installDir = DEFAULT_INSTALL_DIR;
        progressHandler = DEFAULT_PROGRESS_HANDLER;
        jcefArgs = new LinkedList<>();
        jcefArgs.addAll(DEFAULT_JCEF_ARGS);
        cefSettings = DEFAULT_CEF_SETTINGS.clone();
    }

    public void setInstallDir(File installDir) {
        Objects.requireNonNull(installDir, "installDir cannot be null");
        this.installDir = installDir;
    }

    public void setProgressHandler(IProgressHandler progressHandler) {
        Objects.requireNonNull(installDir, "progressHandler cannot be null");
        this.progressHandler = progressHandler;
    }

    public List<String> getJcefArgs() {
        return jcefArgs;
    }

    public void addJcefArg(String arg){
        Objects.requireNonNull(arg, "arg cannot be null");
        String args[] = arg.split(" ");
        jcefArgs.addAll(Arrays.asList(args));
    }

    public CefSettings getCefSettings() {
        return cefSettings;
    }

    public CefApp build() throws IOException, UnsupportedPlatformException {
        this.progressHandler.handleProgress(EnumProgress.LOCATING, EnumProgress.NO_ESTIMATION);
        boolean installOk = CefInstallationChecker.checkInstallation(this.installDir);
        if(!installOk){
            //Perform install
            //Clear install dir
            FileUtils.deleteDir(this.installDir);
            if(!this.installDir.mkdirs())throw new IOException("Could not create installation directory");
            //Fetch a native input stream
            InputStream nativesIn = PackageClasspathStreamer.streamNatives(
                    CefBuildInfo.fromClasspath(), EnumPlatform.getCurrentPlatform());
            boolean downloading = false;
            if(nativesIn==null){
                this.progressHandler.handleProgress(EnumProgress.DOWNLOADING, EnumProgress.NO_ESTIMATION);
                downloading = true;
                File download = new File(this.installDir, "download.zip.temp");
                PackageDownloader.downloadNatives(
                        CefBuildInfo.fromClasspath(), EnumPlatform.getCurrentPlatform(),
                        download, f->{
                            this.progressHandler.handleProgress(EnumProgress.DOWNLOADING, f);
                        });
                nativesIn = new ZipInputStream(new FileInputStream(download));
                ZipEntry entry;
                boolean found = false;
                while((entry=((ZipInputStream) nativesIn).getNextEntry())!=null){
                    if(entry.getName().endsWith(".tar.gz")){
                        found = true;
                        break;
                    }
                }
                if(!found){
                    throw new IOException("Downloaded artifact did not contain a .tar.gz archive");
                }
            }
            //Extract a native bundle
            this.progressHandler.handleProgress(EnumProgress.EXTRACTING, EnumProgress.NO_ESTIMATION);
            TarGzExtractor.extractTarGZ(this.installDir, nativesIn);
            if(downloading){
                if(!new File(this.installDir, "download.zip.temp").delete()){
                    throw new IOException("Could not remove downloaded temp file");
                }
            }
            //Install native bundle
            this.progressHandler.handleProgress(EnumProgress.INSTALL, EnumProgress.NO_ESTIMATION);
            //Remove quarantine on macosx
            if(EnumPlatform.getCurrentPlatform().getOs().isMacOSX()){
                UnquarantineUtil.unquarantine(this.installDir);
            }
            //Lock installation
            if(!(new File(installDir, "install.lock").createNewFile())){
                throw new IOException("Could not create install.lock to complete installation");
            }
        }
        this.progressHandler.handleProgress(EnumProgress.INITIALIZING, EnumProgress.NO_ESTIMATION);

        this.progressHandler.handleProgress(EnumProgress.INITIALIZED, EnumProgress.NO_ESTIMATION);
        return null;
    }
}

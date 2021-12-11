package me.friwi.jcefmaven.platform;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static me.friwi.jcefmaven.platform.EnumOS.*;
import static me.friwi.jcefmaven.platform.PlatformPatterns.*;

public enum EnumPlatform {
    //Order is important to try bigger matches first
    //(darwin before win, x86_64 before x86, arm64 before arm)
    MACOSX_AMD64(OS_MACOSX, ARCH_AMD64, MACOSX),
    MACOSX_ARM64(OS_MACOSX, ARCH_ARM64, MACOSX),
    LINUX_AMD64(OS_LINUX, ARCH_AMD64, LINUX),
    LINUX_I386(OS_LINUX, ARCH_I386, LINUX),
    LINUX_ARM64(OS_LINUX, ARCH_ARM64, LINUX),
    LINUX_ARM(OS_LINUX, ARCH_ARM, LINUX),
    WINDOWS_AMD64(OS_WINDOWS, ARCH_AMD64, WINDOWS),
    WINDOWS_I386(OS_WINDOWS, ARCH_I386, WINDOWS),
    WINDOWS_ARM64(OS_WINDOWS, ARCH_ARM64, WINDOWS);

    public static final String PROPERTY_OS_NAME = "os.name";
    public static final String PROPERTY_OS_ARCH = "os.arch";

    private static final Logger LOGGER = Logger.getLogger( EnumPlatform.class.getName() );

    private static EnumPlatform DETECTED_PLATFORM = null;

    private final String[] osMatch, archMatch;
    private final String identifier;
    private final EnumOS os;

    EnumPlatform(String[] osMatch, String[] archMatch, EnumOS os) {
        Objects.requireNonNull(osMatch, "osMatch cannot be null");
        Objects.requireNonNull(archMatch, "archMatch cannot be null");
        Objects.requireNonNull(os, "os cannot be null");
        this.osMatch = osMatch;
        this.archMatch = archMatch;
        this.identifier = name().toLowerCase(Locale.ENGLISH).replace("_", "-");
        this.os = os;
    }

    private boolean matches(String osName, String osArch){
        Objects.requireNonNull(osName, "osName cannot be null");
        Objects.requireNonNull(osArch, "osArch cannot be null");
        //Search for a correct OS match
        boolean m = false;
        for(String os : osMatch){
            if(osName.toLowerCase(Locale.ENGLISH).contains(os)){
                m = true; //Found one
                break;
            }
        }
        if(!m){
            return false; //OS mismatch
        }
        //Search for a correct architecture match
        for(String arch : archMatch){
            if(osArch.toLowerCase(Locale.ENGLISH).equals(arch)){
                return true; //Also found matching arch
            }
        }
        return false; //Found no matching arch
    }

    public static EnumPlatform getCurrentPlatform() throws UnsupportedPlatformException{
        if(DETECTED_PLATFORM !=null)return DETECTED_PLATFORM;
        String osName = System.getProperty(PROPERTY_OS_NAME);
        String osArch = System.getProperty(PROPERTY_OS_ARCH);
        //Look for a platform match
        for(EnumPlatform platform : values()){
            if(platform.matches(osName, osArch)){
                DETECTED_PLATFORM = platform;
                return platform;
            }
        }
        //No platform matched
        String supported = "";
        for(EnumPlatform platform : values()){
            supported += platform.name()+"("+
                    PROPERTY_OS_NAME+": " + Arrays.toString(platform.osMatch)+", "+
                    PROPERTY_OS_ARCH+": " + Arrays.toString(platform.archMatch)+")\n";
        }
        LOGGER.log(Level.SEVERE, "Can not detect your current platform. Is it supported?\n"+
                "If you think that this is in error, please open an issue " +
                "providing your "+PROPERTY_OS_NAME+" and "+PROPERTY_OS_ARCH+" from below!\n" +
                "\n" +
                "Your platform specs:\n" +
                PROPERTY_OS_NAME+": \""+osName+"\"\n" +
                PROPERTY_OS_ARCH+": \""+osArch+"\"\n" +
                "\n" +
                "Supported platforms:\n" +
                supported);
        throw new UnsupportedPlatformException(osName, osArch);
    }

    public String getIdentifier(){
        return identifier;
    }

    public EnumOS getOs() {
        return os;
    }
}

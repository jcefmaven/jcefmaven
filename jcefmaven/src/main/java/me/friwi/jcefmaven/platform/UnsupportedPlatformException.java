package me.friwi.jcefmaven.platform;

public class UnsupportedPlatformException extends Exception {
    private String osName, osArch;

    public UnsupportedPlatformException(String osName, String osArch) {
        super("Could not determine platform for "+
                EnumPlatform.PROPERTY_OS_NAME+"="+osName+" and "+
                EnumPlatform.PROPERTY_OS_ARCH+"="+osArch);
        this.osName = osName;
        this.osArch = osArch;
    }

    public String getOsName() {
        return osName;
    }

    public String getOsArch() {
        return osArch;
    }
}

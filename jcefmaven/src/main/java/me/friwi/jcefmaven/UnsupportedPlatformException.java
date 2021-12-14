package me.friwi.jcefmaven;

/**
 * Exception indicating that the current operating system and architecture
 * combination is not supported.
 *
 * @author Fritz Windisch
 */
public class UnsupportedPlatformException extends Exception {
    private final String osName;
    private final String osArch;

    public UnsupportedPlatformException(String osName, String osArch) {
        super("Could not determine platform for " +
                EnumPlatform.PROPERTY_OS_NAME + "=" + osName + " and " +
                EnumPlatform.PROPERTY_OS_ARCH + "=" + osArch);
        this.osName = osName;
        this.osArch = osArch;
    }

    /**
     * Fetches the operating system name.
     *
     * @return the operating system name
     */
    public String getOsName() {
        return osName;
    }

    /**
     * Fetches the system architecture.
     *
     * @return the system architecture.
     */
    public String getOsArch() {
        return osArch;
    }
}

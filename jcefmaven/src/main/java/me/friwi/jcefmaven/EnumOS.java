package me.friwi.jcefmaven;

/**
 * Enum representing all supported operating systems.
 * Fetch the current OS using: <pre>{@code EnumPlatform.getCurrentPlatform().getOs()}</pre>
 *
 * @author Fritz Windisch
 */
public enum EnumOS {
    MACOSX,
    LINUX,
    WINDOWS;

    public boolean isMacOSX() {
        return this == MACOSX;
    }

    public boolean isLinux() {
        return this == LINUX;
    }

    public boolean isWindows() {
        return this == WINDOWS;
    }
}

package me.friwi.jcefmaven.platform;

public enum EnumOS {
    MACOSX,
    LINUX,
    WINDOWS;

    public boolean isMacOSX(){
        return this==MACOSX;
    }

    public boolean isLinux(){
        return this==LINUX;
    }

    public boolean isWindows(){
        return this==WINDOWS;
    }
}

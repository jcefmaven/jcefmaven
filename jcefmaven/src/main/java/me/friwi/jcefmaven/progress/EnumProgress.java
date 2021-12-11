package me.friwi.jcefmaven.progress;

public enum EnumProgress {
    UNINITIALIZED,
    LOCATING,
    DOWNLOADING,
    EXTRACTING,
    INSTALL,
    INITIALIZING,
    INITIALIZED;

    public static final float NO_ESTIMATION = -1f;
}

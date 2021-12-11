package me.friwi.jcefmaven.platform;

public class PlatformPatterns {
    protected static String[] OS_MACOSX = new String[]{"mac", "darwin"};
    protected static String[] OS_LINUX = new String[]{"nux"};
    protected static String[] OS_WINDOWS = new String[]{"win"};

    protected static String[] ARCH_AMD64 = new String[]{"amd64", "x86_64"};
    protected static String[] ARCH_I386 = new String[]{"x86", "i386", "i486", "i586", "i686", "i786"};
    protected static String[] ARCH_ARM64 = new String[]{"arm64", "aarch64"};
    protected static String[] ARCH_ARM = new String[]{"arm"};
}

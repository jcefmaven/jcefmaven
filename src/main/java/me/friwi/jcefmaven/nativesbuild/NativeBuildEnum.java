package me.friwi.jcefmaven.nativesbuild;

/**
 * Enum with all available native build types and some additional data required to assemble them
 *
 * @author Fritz Windisch
 */
public enum NativeBuildEnum {
    LINUX32("linux32", "linux32.zip", "linux-i586", "java-cef-build-bin/bin/lib/linux32/"),
    LINUX64("linux64", "linux64.zip", "linux-amd64", "java-cef-build-bin/bin/lib/linux64/"),
    MACOSX64("macosx64", "macosx64.zip", "macosx-universal", "java-cef-build-bin/bin/"),
    WIN32("win32", "win32.zip", "windows-i586", "java-cef-build-bin/bin/lib/win32/"),
    WIN64("win64", "win64.zip", "windows-amd64", "java-cef-build-bin/bin/lib/win64/");

    private String name;
    private String assetName;
    private String jogampName;
    private String relevantContents;

    NativeBuildEnum(String name, String assetName, String jogampName, String relevantContents) {
        this.name = name;
        this.assetName = assetName;
        this.jogampName = jogampName;
        this.relevantContents = relevantContents;
    }

    public String getName() {
        return name;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getJogampName() {
        return jogampName;
    }

    public String getRelevantContents() {
        return relevantContents;
    }
}

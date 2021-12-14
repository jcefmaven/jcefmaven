package me.friwi.jcefmaven;

/**
 * Enum representing all major steps in the setup process for JCef.
 *
 * @author Fritz Windisch
 */
public enum EnumProgress {
    /**
     * <p>Installation is being located:</p>
     * <p>- is there already an installation present?</p>
     * <p>- if not: is the native bundle on classpath?</p>
     * <p></p>
     * <p>If the installation is present, skip to {@link #INITIALIZING}.</p>
     * <p>If the native bundle is present, skip to {@link #EXTRACTING}.</p>
     * <p>Else go to {@link #DOWNLOADING}.</p>
     */
    LOCATING,
    /**
     * Downloading the native bundle from GitHub or central repository
     * to the installation directory.
     */
    DOWNLOADING,
    /**
     * Extract the downloaded/located native bundle to the installation directory.
     */
    EXTRACTING,
    /**
     * Perform steps on the extracted files to make them fully functional.
     * Mark installation as complete.
     */
    INSTALL,
    /**
     * Initialize JCef for the corresponding platform.
     */
    INITIALIZING,
    /**
     * JCef initialization complete.
     */
    INITIALIZED;

    /**
     * Magic value used to indicate that there is no progress estimation for the current
     * installation step.
     */
    public static final float NO_ESTIMATION = -1f;
}

package net.dillon.qualityofqueso.platform;

/**
 * Valid checked mods to see if they have been loaded.
 */
public enum PlatformMod {
    YACL("yet_another_config_lib_v3"),
    VIAFABRICPLUS("viafabricplus");

    private final String id;

    PlatformMod(final String id) {
        this.id = id;
    }

    /**
     * @return the id for the checked mod.
     */
    public String getId() {
        return id;
    }
}
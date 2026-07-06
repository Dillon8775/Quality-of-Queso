package net.dillon.qualityofqueso.platform;

/**
 * Determines the version type, which determines where the logo is displayed on the options screen.
 */
public enum VersionType {
    RELEASE(50),
    PATCH(53),
    PATCH_2(55);

    private final int widthModifier;

    VersionType(int widthModifier) {
        this.widthModifier = widthModifier;
    }

    public int getWidthModifier() {
        return this.widthModifier;
    }
}
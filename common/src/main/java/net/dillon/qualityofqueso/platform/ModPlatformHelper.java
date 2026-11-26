package net.dillon.qualityofqueso.platform;

import net.dillon.dillonlib.platform.Loadable;
import net.dillon.qualityofqueso.helper.ModConstants;

public abstract class ModPlatformHelper implements Loadable {

    @Override
    public String modId() {
        return ModConstants.MOD_ID;
    }

    /**
     * Adds all mod ids to a list, for the ItemArgument.
     */
    public abstract void addModIds();
}
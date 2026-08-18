package net.dillon.qualityofqueso.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

/**
 * An abstract Quality of Queso {@link DebugScreenEntry}.
 */
public abstract class ModDebugEntry implements DebugScreenEntry {

    /**
     * @return Quality of Queso debug entries should always be allowed.
     */
    @Override
    public boolean isAllowed(boolean reducedDebugInfo) {
        return modEnabled(Minecraft.getInstance());
    }
}
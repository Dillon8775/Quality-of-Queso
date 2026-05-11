package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;

/**
 * An abstract Quality of Queso {@link DebugScreenEntry}.
 */
public abstract class QoQScreenEntry implements DebugScreenEntry {

    /**
     * @return Quality of Queso debug entries should always be allowed.
     */
    @Override
    public boolean isAllowed(boolean reducedDebugInfo) {
        return true;
    }

    /**
     * @return Always add the Quality of Queso debug entry to the mod's category.
     */
    @Override
    public DebugEntryCategory category() {
        return ModHudEntries.QOQ;
    }
}
package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;

/**
 * An abstract QoQ {@link DebugScreenEntry}, which is always in the QoQ category.
 */
public abstract class QoQScreenEntry implements DebugScreenEntry {

    @Override
    public boolean isAllowed(boolean reducedDebugInfo) {
        return true;
    }

    @Override
    public DebugEntryCategory category() {
        return ModHudEntries.QOQ;
    }
}
package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;

/**
 * An abstract {@link DebugScreenEntry}.
 */
public abstract class ModDebugEntry implements DebugScreenEntry {

    /**
     * @return these debug entries should always be allowed.
     */
    @Override
    public boolean isAllowed(boolean reducedDebugInfo) {
        return true;
    }

    /**
     * @return always add this debug entry to the mod's category.
     */
    @Override
    public DebugEntryCategory category() {
        return ModDebugScreenEntries.MOD_CATEGORY;
    }
}
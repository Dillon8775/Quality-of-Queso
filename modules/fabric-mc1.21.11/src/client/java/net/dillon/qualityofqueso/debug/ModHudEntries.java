package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.util.Identifier;

/**
 * All debug hud entries.
 */
public class ModHudEntries {
    public static final Identifier REAL_LIFE_TIME = DebugHudEntries.register(Identifier.of("qualityofqueso", "real_life_time"), new RealLifeTimeHudEntry());
    public static final Identifier IN_GAME_TIME = DebugHudEntries.register(Identifier.of("qualityofqueso", "in_game_time"), new InGameTimeHudEntry());

    public static void initializeDebugHudEntries() {
    }
}
package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.resources.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * All debug hud entries.
 */
@OnlyIn(Dist.CLIENT)
public class ModHudEntries {
    public static final Identifier REAL_LIFE_TIME = DebugScreenEntries.register(Identifier.fromNamespaceAndPath("qualityofqueso", "real_life_time"), new RealLifeTimeHudEntry());
    public static final Identifier IN_GAME_TIME = DebugScreenEntries.register(Identifier.fromNamespaceAndPath("qualityofqueso", "in_game_time"), new InGameTimeHudEntry());

    public static void initializeDebugHudEntries() {
    }
}
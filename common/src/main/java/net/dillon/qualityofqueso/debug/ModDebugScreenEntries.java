package net.dillon.qualityofqueso.debug;

import net.dillon.dillonlib.client.component.DebugBuilder;
import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.client.gui.components.debug.DebugGroup;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

import static net.dillon.dillonlib.client.component.DebugBuilder.basicDebugCategory;
import static net.dillon.dillonlib.factory.ClientFactories.createDebugEntryKey;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * All debug hud entries.
 */
public class ModDebugScreenEntries {
    public static final ResourceKey<DebugScreenEntry> DATE = key("date");
    public static final ResourceKey<DebugScreenEntry> IN_GAME_TIME = key("in_game_time");
    public static final ResourceKey<DebugScreenEntry> REAL_LIFE_TIME = key("real_life_time");
    public static final ResourceKey<DebugScreenEntry> ALARMS_AND_TIMERS = key("alarms_and_timers");
    public static final ResourceKey<DebugScreenEntry> SIMPLE_COORDINATES = key("simple_coordinates");
    public static final ResourceKey<DebugScreenEntry> ACCURATE_FACING = key("accurate_facing");
    public static final ResourceKey<DebugScreenEntry> ROTATION = key("rotation");
    public static final ResourceKey<DebugScreenEntry> PLAY_TIME = key("play_time");
    public static final ResourceKey<DebugScreenEntry> POTION_EFFECTS = key("potion_effects");

    protected static final DebugGroup DATE_AND_TIME_GROUP = DebugBuilder.leftAlignedDebugGroup(Component.translatable("qualityofqueso.debug.date_and_time"), () -> client().accessibility().debugTextAlignment);
    protected static final DebugGroup ALARMS_AND_TIMERS_GROUP = DebugBuilder.alignedDebugGroup(Component.translatable("qualityofqueso.debug.alarms_and_timers"), () -> client().accessibility().debugTextAlignment);
    protected static final DebugGroup POTION_EFFECTS_GROUP = DebugBuilder.alignedDebugGroup(Component.translatable("qualityofqueso.debug.potion_effects"), () -> client().accessibility().debugTextAlignment);

    protected static final DebugEntryCategory MOD_CATEGORY = basicDebugCategory(Component.translatable("qualityofqueso"));

    /**
     * @return a debug entry key.
     */
    private static ResourceKey<DebugScreenEntry> key(String name) {
        return createDebugEntryKey(qoqIdentifier(name));
    }

    /**
     * Creates the mod's debug profile.
     */
    public static void createProfile() {
        DebugBuilder.createCustomProfile(
                Map.of(
                        REAL_LIFE_TIME, DebugScreenEntryStatus.ALWAYS_ON,
                        ALARMS_AND_TIMERS, DebugScreenEntryStatus.ALWAYS_ON,
                        PLAY_TIME, DebugScreenEntryStatus.IN_OVERLAY,
                        POTION_EFFECTS, DebugScreenEntryStatus.IN_OVERLAY
                )
        );
    }
}
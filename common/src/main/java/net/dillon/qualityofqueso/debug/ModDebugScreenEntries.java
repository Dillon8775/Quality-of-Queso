package net.dillon.qualityofqueso.debug;

import net.dillon.dillonlib.client.component.DebugBuilder;
import net.dillon.dillonlib.client.component.LibDebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.client.gui.components.debug.DebugGroup;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.network.chat.Component;

import java.util.List;

import static net.dillon.dillonlib.client.component.DebugBuilder.basicDebugCategory;
import static net.dillon.dillonlib.factory.ClientFactories.createLibDebugEntryKey;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.option.ClientOptionInstances.client;

/**
 * All debug hud entries.
 */
public class ModDebugScreenEntries {
    public static final LibDebugScreenEntry SIMPLE_COORDINATES = create("simple_coordinates");
    public static final LibDebugScreenEntry SIMPLE_FPS = create("simple_fps");
    public static final LibDebugScreenEntry ACCURATE_FACING = create("accurate_facing");
    public static final LibDebugScreenEntry ROTATION = create("rotation");
    public static final LibDebugScreenEntry PLAY_TIME = create("play_time", DebugScreenEntryStatus.IN_OVERLAY);
    public static final LibDebugScreenEntry POTION_EFFECTS = create("potion_effects", DebugScreenEntryStatus.IN_OVERLAY);
    public static final LibDebugScreenEntry ALARMS_AND_TIMERS = create("alarms_and_timers", DebugScreenEntryStatus.ALWAYS_ON);
    public static final LibDebugScreenEntry LOCAL_TIME = create("local_time", DebugScreenEntryStatus.ALWAYS_ON);
    public static final LibDebugScreenEntry DATE = create("date");
    public static final LibDebugScreenEntry IN_GAME_TIME = create("in_game_time");

    protected static final DebugGroup DATE_AND_TIME_GROUP = DebugBuilder.leftAlignedDebugGroup(Component.translatable("qualityofqueso.debug.date_and_time"), () -> client().misc().debugTextAlignment);
    protected static final DebugGroup ALARMS_AND_TIMERS_GROUP = DebugBuilder.alignedDebugGroup(Component.translatable("qualityofqueso.debug.alarms_and_timers"), () -> client().misc().debugTextAlignment);
    protected static final DebugGroup POTION_EFFECTS_GROUP = DebugBuilder.alignedDebugGroup(Component.translatable("qualityofqueso.debug.potion_effects"), () -> client().misc().debugTextAlignment);

    protected static final DebugEntryCategory MOD_CATEGORY = basicDebugCategory(Component.translatable("qualityofqueso"));

    /**
     * @return a debug entry key with a default status.
     */
    private static LibDebugScreenEntry create(String name, DebugScreenEntryStatus defaultStatus) {
        return createLibDebugEntryKey(qoqIdentifier(name), defaultStatus);
    }

    /**
     * @return a debug entry key.
     */
    private static LibDebugScreenEntry create(String name) {
        return create(name, DebugScreenEntryStatus.NEVER);
    }

    /**
     * Creates the mod's debug profile.
     */
    public static void createProfile() {
        DebugBuilder.createCustomProfile(
                List.of(
                        LOCAL_TIME,
                        ALARMS_AND_TIMERS,
                        PLAY_TIME,
                        POTION_EFFECTS
                )
        );
    }
}
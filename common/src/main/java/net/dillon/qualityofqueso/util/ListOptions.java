package net.dillon.qualityofqueso.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.util.ModOptionUtil.*;

/**
 * Options displayed in different Quality of Queso menus.
 */
public class ListOptions {

    public static OptionInstance<Boolean> overrideClientTime() {
        return createClientBooleanOption("qualityofqueso.options.override_client_time", true, client().visualTime().overrideClientTime,
                (options, value) -> options.visualTime().overrideClientTime = value
        );
    }

    public static OptionInstance<Boolean> displayVisualClock() {
        return createClientBooleanOption("qualityofqueso.options.display_visual_clock", true, client().visualTime().displayVisualClock,
                (options, value) -> options.visualTime().displayVisualClock = value
        );
    }

    public static OptionInstance<Integer> visualTime() {
        return new OptionInstance<>("qualityofqueso.options.visual_time",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.visual_time.tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(formatMinecraftTime(value * 5))),
                new OptionInstance.IntRange(0, 287), client().visualTime().visualTime, value -> client().visualTime().visualTime = value);
    }

    public static OptionInstance<Integer> visualTimeSpeed() {
        return createIntegerOption(
                "qualityofqueso.options.visual_time_speed",
                (optionText, value) -> {
                    if (value == 0) {
                        return Options.genericValueLabel(optionText, Component.literal("Freeze"));
                    } else {
                        return Options.genericValueLabel(optionText, Component.literal(value + "%"));
                    }
                },
                0,
                100,
                client().visualTime().visualTimeSpeed,
                (options, value) -> options.visualTime().visualTimeSpeed = value
        );
    }

    public static OptionInstance<Boolean> syncLocalTime() {
        return createClientBooleanOption("qualityofqueso.options.sync_local_time", true, client().visualTime().syncLocalTime,
                (options, value) -> options.visualTime().syncLocalTime = value
        );
    }

    public static OptionInstance<Integer> armorStatusXPosition() {
        return createIntegerOption(
                "qualityofqueso.options.armor_status_x_position",
                ModOptionUtil::posFormatted,
                -1200,
                1200,
                client().hud().armorStatusPosition[0],
                (options, value) -> options.hud().armorStatusPosition[0] = value
        );
    }

    public static OptionInstance<Integer> armorStatusYPosition() {
        return createIntegerOption(
                "qualityofqueso.options.armor_status_y_position",
                ModOptionUtil::invertedPosFormat,
                -1200,
                25,
                client().hud().armorStatusPosition[1],
                (options, value) -> options.hud().armorStatusPosition[1] = value
        );
    }

    public static OptionInstance<Integer> itemCounterXPosition() {
        return createIntegerOption(
                "qualityofqueso.options.item_counter_x_position",
                ModOptionUtil::posFormatted,
                -1200,
                1200,
                client().itemCounter().itemCounterPosition[0],
                (options, value) -> options.itemCounter().itemCounterPosition[0] = value
        );
    }

    public static OptionInstance<Integer> itemCounterYPosition() {
        return createIntegerOption(
                "qualityofqueso.options.item_counter_y_position",
                ModOptionUtil::invertedPosFormat,
                -1200,
                25,
                client().itemCounter().itemCounterPosition[1],
                (options, value) -> options.itemCounter().itemCounterPosition[1] = value
        );
    }

    public static OptionInstance<Integer> otherElementsYPosition() {
        return createIntegerOption(
                "qualityofqueso.options.other_elements_y_position",
                ModOptionUtil::invertedPosFormat,
                -1200,
                25,
                client().hud().otherElementsY,
                (options, value) -> options.hud().otherElementsY = value
        );
    }

    public static OptionInstance<Integer> visualClockXPosition() {
        return createIntegerOption(
                "qualityofqueso.options.visual_clock_x_position",
                ModOptionUtil::posFormatted,
                -1200,
                25,
                client().visualTime().visualClockPosition[0],
                (options, value) -> options.visualTime().visualClockPosition[0] = value
        );
    }

    public static OptionInstance<Integer> visualClockYPosition() {
        return createIntegerOption(
                "qualityofqueso.options.visual_clock_y_position",
                ModOptionUtil::invertedPosFormat,
                -1200,
                25,
                client().visualTime().visualClockPosition[1],
                (options, value) -> options.visualTime().visualClockPosition[1] = value
        );
    }

    public static OptionInstance<Integer> itemFrameSearchGlowDuration() {
        return createIntegerOption(
                "qualityofqueso.options.item_frame_search_glow_duration",
                (optionText, value) -> {
                    if (value == 0) {
                        return Options.genericValueLabel(optionText, Component.literal("Indefinite").withStyle(ChatFormatting.RED));
                    } else if (value < 60) {
                        return Options.genericValueLabel(optionText, Component.literal(value + "s"));
                    } else {
                        int minutes = value / 60;
                        int seconds = value % 60;
                        if (seconds == 0) {
                            return Options.genericValueLabel(optionText, Component.literal(minutes + "m"));
                        } else {
                            return Options.genericValueLabel(optionText, Component.literal(minutes + "m " + seconds + "s"));
                        }
                    }
                },
                0,
                180,
                client().misc().itemFrameSearchGlowDuration,
                (options, value) -> options.misc().itemFrameSearchGlowDuration = value
        );
    }

    public static OptionInstance<Integer> itemFrameSearchRadius() {
        return new OptionInstance<>("qualityofqueso.options.item_frame_search_radius",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.item_frame_search_radius.tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + " blocks")),
                new OptionInstance.IntRange(10, 256).xmap(value -> value * 2, value -> value / 2, true), client().misc().itemFrameSearchRadius, value -> client().misc().itemFrameSearchRadius = value);
    }
}
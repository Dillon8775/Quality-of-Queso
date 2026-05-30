package net.dillon.qualityofqueso.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.util.ModOptionUtil.*;

/**
 * Options displayed in different Quality of Queso menus.
 */
public class ListOptions {

    public static OptionInstance<Boolean> overrideClientTime() {
        return createClientBooleanOption("override_client_time", true, clientOptionsInstance().getVisualTimeOptions().overrideClientTime,
                (options, value) -> options.getVisualTimeOptions().overrideClientTime = value
        );
    }

    public static OptionInstance<Integer> visualTime() {
        return new OptionInstance<>("qualityofqueso.options.visual_time",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.visual_time.tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(formatMinecraftTime(value * 5))),
                new OptionInstance.IntRange(0, 287), clientOptionsInstance().getVisualTimeOptions().visualTime, value -> clientOptionsInstance().getVisualTimeOptions().visualTime = value);
    }

    public static OptionInstance<Integer> visualTimeSpeed() {
        return createIntegerOption(
                "visual_time_speed",
                (optionText, value) -> {
                    if (value == 0) {
                        return Options.genericValueLabel(optionText, Component.literal("Freeze"));
                    } else {
                        return Options.genericValueLabel(optionText, Component.literal(value + "%"));
                    }
                },
                0,
                100,
                clientOptionsInstance().getVisualTimeOptions().visualTimeSpeed,
                (options, value) -> options.getVisualTimeOptions().visualTimeSpeed = value
        );
    }

    public static OptionInstance<Boolean> syncLocalTime() {
        return createClientBooleanOption("sync_local_time", true, clientOptionsInstance().getVisualTimeOptions().syncLocalTime,
                (options, value) -> options.getVisualTimeOptions().syncLocalTime = value
        );
    }

    public static OptionInstance<Integer> armorStatusXPosition() {
        return createIntegerOption(
                "armor_status_x_position",
                ModOptionUtil::posFormatted,
                -600,
                300,
                clientOptionsInstance().getHudOptions().armorStatusPosition[0],
                (options, value) -> options.getHudOptions().armorStatusPosition[0] = value
        );
    }

    public static OptionInstance<Integer> armorStatusYPosition() {
        return createIntegerOption(
                "armor_status_y_position",
                ModOptionUtil::invertedPosFormat,
                -525,
                25,
                clientOptionsInstance().getHudOptions().armorStatusPosition[1],
                (options, value) -> options.getHudOptions().armorStatusPosition[1] = value
        );
    }

    public static OptionInstance<Integer> itemCounterXPosition() {
        return createIntegerOption(
                "item_counter_x_position",
                ModOptionUtil::posFormatted,
                -400,
                600,
                clientOptionsInstance().getItemCounterOptions().itemCounterPosition[0],
                (options, value) -> options.getItemCounterOptions().itemCounterPosition[0] = value
        );
    }

    public static OptionInstance<Integer> itemCounterYPosition() {
        return createIntegerOption(
                "item_counter_y_position",
                ModOptionUtil::invertedPosFormat,
                -525,
                25,
                clientOptionsInstance().getItemCounterOptions().itemCounterPosition[1],
                (options, value) -> options.getItemCounterOptions().itemCounterPosition[1] = value
        );
    }

    public static OptionInstance<Integer> otherElementsYPosition() {
        return createIntegerOption(
                "other_elements_y_position",
                ModOptionUtil::invertedPosFormat,
                -525,
                25,
                clientOptionsInstance().getHudOptions().otherElementsY,
                (options, value) -> options.getHudOptions().otherElementsY = value
        );
    }

    public static OptionInstance<Integer> itemFrameSearchGlowDuration() {
        return createIntegerOption(
                "item_frame_search_glow_duration",
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
                clientOptionsInstance().getMiscOptions().itemFrameSearchGlowDuration,
                (options, value) -> options.getMiscOptions().itemFrameSearchGlowDuration = value
        );
    }

    public static OptionInstance<Integer> itemFrameSearchRadius() {
        return new OptionInstance<>("qualityofqueso.options.item_frame_search_radius",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.item_frame_search_radius.tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + " blocks")),
                new OptionInstance.IntRange(10, 256).xmap(value -> value * 2, value -> value / 2, true), clientOptionsInstance().getMiscOptions().itemFrameSearchRadius, value -> clientOptionsInstance().getMiscOptions().itemFrameSearchRadius = value);
    }
}
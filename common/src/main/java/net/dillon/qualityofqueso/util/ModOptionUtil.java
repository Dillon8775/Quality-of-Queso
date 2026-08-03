package net.dillon.qualityofqueso.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.kuma.api.ManagedKeyMapping;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static net.dillon.dillonlib.client.ModernWidgetOptions.createSimpleBooleanOption;
import static net.dillon.dillonlib.client.ModernWidgetOptions.createSimpleIntegerOption;
import static net.dillon.qualityofqueso.helper.MethodHelper.kumaKey;

/**
 * Utility class for option lists.
 */
public class ModOptionUtil {

    /**
     * @return a server-side option.
     */
    public static Component serverSideOption(Component translation) {
        Component serverSideTranslation = Component.translatable("qualityofqueso.option.server_side").withStyle(ChatFormatting.RED);
        return translation.copy().append("\n\n").copy().append(serverSideTranslation);
    }

    /**
     * @return a normal format.
     */
    protected static Component posFormatted(Component optionText, int value) {
        String v = value == 0 ? Component.translatable("qualityofqueso.options.hud_element.default").getString() : String.valueOf(value);
        if (value > 0) {
            v = "+" + v;
        }
        return optionText.copy().append(" (" + v + ")");
    }

    /**
     * @return an inverted format.
     */
    protected static Component invertedPosFormat(Component optionText, int value) {
        String v = value == 0 ? Component.translatable("qualityofqueso.options.hud_element.default").getString() : String.valueOf(value);
        String absV = String.valueOf(Math.abs(value));
        if (value != 0) {
            v = (value > 0 ? "-" : "+") + absV;
        }
        return optionText.copy().append(" (" + v + ")");
    }

    /**
     * @return the visual time speed to ticks.
     */
    public static int visualTimeStepToTicks(int step) {
        int minutes = step * 5;
        return (int)Math.round(minutes * (24000.0 / 1440.0));
    }

    /**
     * Formats the minecraft time.
     */
    protected static String formatMinecraftTime(int minecraftMinutes) {
        int totalMinutes = (minecraftMinutes + 360) % 1440;

        int hour24 = totalMinutes / 60;
        int minute = totalMinutes % 60;

        String suffix = hour24 >= 12 ? "PM" : "AM";
        int hour12 = hour24 % 12;
        if (hour12 == 0) {
            hour12 = 12;
        }

        return String.format("%d:%02d %s", hour12, minute, suffix);
    }

    /**
     * @return a simple client instance boolean {@link OptionInstance}.
     */
    protected static OptionInstance<Boolean> createClientBooleanOption(String translation, boolean toggleText, boolean currentValue, BiConsumer<ModClientOptions, Boolean> consumer, Object... obj) {
        return createSimpleBooleanOption(
                translation,
                toggleText,
                currentValue,
                ModClientOptions.INSTANCE,
                consumer,
                obj
        );
    }

    /**
     * @return a client-instance integer {@link OptionInstance}.
     */
    protected static OptionInstance<Integer> createIntegerOption(
            String translation,
            BiFunction<Component, Integer, Component> display,
            int minValue,
            int maxValue,
            int currentValue,
            BiConsumer<ModClientOptions, Integer> consumer
    ) {
        return createSimpleIntegerOption(
                translation,
                display,
                new OptionInstance.IntRange(minValue, maxValue),
                currentValue,
                ModClientOptions.INSTANCE,
                consumer
        );
    }

    /**
     * @return the {@link Kuma} bound key as a string, with the mouse key boolean.
     */
    public static String kumaKeyMappingAsString(ManagedKeyMapping keyMapping, boolean mouseKey) {
        return parseKeyAsString(kumaKey(keyMapping).toString().toUpperCase(), mouseKey);
    }

    /**
     * @return the {@link Kuma} key modifiers (ex. CTRL + ALT), without the base key.
     */
    public static String kumaKeyMappingModifiersAsString(ManagedKeyMapping keyMapping) {
        List<String> modifiers = keyMapping.getBinding().modifiers().asList().stream()
                .map(ModOptionUtil::modifierName)
                .toList();
        return String.join(" + ", modifiers);
    }

    /**
     * @return the full {@link Kuma} keybind (ex. {@code CTRL + C}).
     */
    public static String fullKumaKeyMappingAsString(ManagedKeyMapping keyMapping) {
        String modifiers = kumaKeyMappingModifiersAsString(keyMapping);
        String keyName = kumaKey(keyMapping).getDisplayName().getString().toUpperCase();

        for (InputConstants.Key customModifier : keyMapping.getBinding().modifiers().getCustomModifiers()) {
            String customModifierName = customModifier.getDisplayName().getString().toUpperCase();
            modifiers = modifiers.isEmpty() ? customModifierName : modifiers + " + " + customModifierName;
        }

        return modifiers.isEmpty() ? keyName : modifiers + " + " + keyName;
    }

    /**
     * @return the modifier name for a {@link KeyModifier}.
     */
    private static String modifierName(KeyModifier modifier) {
        return switch (modifier) {
            case SHIFT -> "SHIFT";
            case CONTROL -> "CTRL";
            case ALT -> "ALT";
            default -> modifier.name();
        };
    }

    /**
     * @return the key name (ex. B or F), with mouseKey boolean.
     */
    protected static String parseKeyAsString(String translationKey, boolean mouseKey) {
        return translationKey.substring(mouseKey ? 4 : 13).toUpperCase();
    }
}
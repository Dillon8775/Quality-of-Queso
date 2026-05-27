package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.option.BaseOptions;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.ModCommonOptions;
import net.dillon.qualityofqueso.option.UniversalOptions;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static net.dillon.qualityofqueso.helper.MethodHelper.key;

/**
 * Utility class for option lists.
 */
public class ModOptionUtil {
    protected static final OptionInstance.CaptionBasedToString<Boolean> ON_OFF_TEXT = (component, bl) -> bl
            ? ModTexts.ON
            : ModTexts.OFF;
    protected static final OptionInstance.CaptionBasedToString<Boolean> YES_NO_TEXT = (component, bl) -> bl
            ? ModTexts.YES
            : ModTexts.NO;

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
     * @return the bound key as a string, with the mouse key boolean.
     */
    public static String keyMappingAsString(KeyMapping keyMapping, boolean mouseKey) {
        return parseKeyAsString(key(keyMapping).toString().toUpperCase(), mouseKey);
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
     * @return a simple boolean {@link OptionInstance}.
     */
    private static <T> OptionInstance<Boolean> createSimpleBooleanOption(
            String translation,
            boolean toggleText,
            boolean currentValue,
            BaseOptions<T> instance,
            BiConsumer<T, Boolean> consumer,
            Object... obj) {
        return OptionInstance.createBoolean(
                "qualityofqueso.options." + translation,
                OptionInstance.cachedConstantTooltip(
                        Component.translatable("qualityofqueso.options." + translation + ".tooltip", obj)
                ),
                toggleText ? ON_OFF_TEXT : YES_NO_TEXT,
                currentValue,
                value -> instance.update(options -> consumer.accept(options, value))
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
     * @return an integer option {@link OptionInstance}.
     */
    private static <T> OptionInstance<Integer> createSimpleIntegerOption(
            String translation,
            BiFunction<Component, Integer, Component> display,
            OptionInstance.IntRange factory,
            int currentValue,
            BaseOptions<T> instance,
            BiConsumer<T, Integer> consumer
    ) {
        return new OptionInstance<>(
                "qualityofqueso.options." + translation,
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options." + translation + ".tooltip")),
                display::apply,
                factory,
                currentValue,
                value -> instance.update(options -> consumer.accept(options, value))
        );
    }

    /**
     * @return the key name (ex. B or F), with mouseKey boolean.
     */
    protected static String parseKeyAsString(String translationKey, boolean mouseKey) {
        return translationKey.substring(mouseKey ? 4 : 13).toUpperCase();
    }

    /**
     * @return a simple common instance boolean {@link OptionInstance}..
     */
    @Deprecated
    private static OptionInstance<Boolean> createCommonBooleanOption(String translation, boolean toggleText, boolean currentValue, BiConsumer<ModCommonOptions, Boolean> consumer, Object... obj) {
        return createSimpleBooleanOption(
                translation,
                toggleText,
                currentValue,
                ModCommonOptions.INSTANCE,
                consumer,
                obj
        );
    }

    /**
     * @return a simple universal-instance boolean {@link OptionInstance}.
     */
    @Deprecated
    private static OptionInstance<Boolean> createUniversalBooleanOption(String translation, boolean toggleText, boolean currentValue, BiConsumer<UniversalOptions, Boolean> consumer, Object... obj) {
        return createSimpleBooleanOption(
                translation,
                toggleText,
                currentValue,
                UniversalOptions.INSTANCE,
                consumer,
                obj
        );
    }

    /**
     * @return a special double {@link OptionInstance}.
     */
    @Deprecated
    private static <T> OptionInstance<Double> createDoubleOption(
            String translation,
            String displayText,
            double min,
            double max,
            double step,
            double currentValue,
            BaseOptions<T> instance,
            BiConsumer<T, Double> consumer
    ) {
        return new OptionInstance<>("qualityofqueso.options." + translation,
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options." + translation + ".tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + displayText)),
                OptionInstance.UnitDouble.INSTANCE.xmap(
                        slider -> {
                            double value = min + slider * (max - min);
                            return Math.round(value * step) / step;
                        },
                        value -> (value - min) / (max - min)
                ),
                currentValue,
                value -> instance.update(options -> consumer.accept(options, value)));
    }

    /**
     * @return an integer option {@link OptionInstance}.
     */
    protected static <T> OptionInstance<Integer> createIntegerOption(
            String translation,
            BiFunction<Component, Integer, Component> display,
            OptionInstance.IntRange factory,
            int currentValue,
            BaseOptions<T> instance,
            BiConsumer<T, Integer> consumer
    ) {
        return new OptionInstance<>(
                "qualityofqueso.options." + translation,
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options." + translation + ".tooltip")),
                display::apply,
                factory,
                currentValue,
                value -> instance.update(options -> consumer.accept(options, value))
        );
    }
}
package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import net.dillon.dillonlib.client.component.DebugBuilder;
import net.dillon.dillonlib.client.component.LibDebugScreenEntry;
import net.dillon.qualityofqueso.debug.ModDebugScreenEntries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.network.chat.Component;

import static net.dillon.dillonlib.task.ClientTasks.getMinecraft;

/**
 * The debug text category for the {@link ConfigurationScreen}.
 */
public class DebugTextCategory {

    protected static ConfigCategory create() {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("qualityofqueso.options.title.debug_text"))
                .tooltip(Component.translatable("qualityofqueso.options.debug_text.tooltip"))
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.debug_text.player"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.debug_text.player.description")))
                                .option(
                                        debugEntryOption(
                                                Component.translatable("qualityofqueso.options.simple_coordinates"),
                                                OptionDescription.of(Component.translatable("qualityofqueso.options.simple_coordinates.description")),
                                                ModDebugScreenEntries.SIMPLE_COORDINATES
                                        )
                                )
                                .option(
                                        debugEntryOption(
                                                Component.translatable("qualityofqueso.options.accurate_facing"),
                                                OptionDescription.of(Component.translatable("qualityofqueso.options.accurate_facing.description")),
                                                ModDebugScreenEntries.ACCURATE_FACING
                                        )
                                )
                                .option(
                                        debugEntryOption(
                                                Component.translatable("qualityofqueso.options.rotation"),
                                                OptionDescription.of(Component.translatable("qualityofqueso.options.rotation.description")),
                                                ModDebugScreenEntries.ROTATION
                                        )
                                )
                                .option(
                                        debugEntryOption(
                                                Component.translatable("qualityofqueso.options.potion_effects"),
                                                OptionDescription.of(Component.translatable("qualityofqueso.options.potion_effects.description")),
                                                ModDebugScreenEntries.POTION_EFFECTS
                                        )
                                )
                                .option(
                                        debugEntryOption(
                                                Component.translatable("qualityofqueso.options.play_time"),
                                                OptionDescription.of(Component.translatable("qualityofqueso.options.play_time.description")),
                                                ModDebugScreenEntries.PLAY_TIME
                                        )
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.debug_text.time"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.debug_text.time.description")))
                                .option(
                                        debugEntryOption(
                                                Component.translatable("qualityofqueso.options.alarms_and_timers"),
                                                OptionDescription.of(Component.translatable("qualityofqueso.options.alarms_and_timers.debug_description")),
                                                ModDebugScreenEntries.ALARMS_AND_TIMERS
                                        )
                                )
                                .option(
                                        debugEntryOption(
                                                Component.translatable("qualityofqueso.options.local_time"),
                                                OptionDescription.of(Component.translatable("qualityofqueso.options.local_time.description")),
                                                ModDebugScreenEntries.LOCAL_TIME
                                        )
                                )
                                .option(
                                        debugEntryOption(
                                                Component.translatable("qualityofqueso.options.date"),
                                                OptionDescription.of(Component.translatable("qualityofqueso.options.date.description")),
                                                ModDebugScreenEntries.DATE
                                        )
                                )
                                .option(
                                        debugEntryOption(
                                                Component.translatable("qualityofqueso.options.in_game_time"),
                                                OptionDescription.of(Component.translatable("qualityofqueso.options.in_game_time.description")),
                                                ModDebugScreenEntries.IN_GAME_TIME
                                        )
                                )
                                .build()
                )
                .build();
    }

    /**
     * @return a basic debug entry option.
     */
    private static Option<DebugScreenEntryStatus> debugEntryOption(
            Component name,
            OptionDescription description,
            LibDebugScreenEntry value
    ) {
        Minecraft minecraft = getMinecraft();
        return Option.<DebugScreenEntryStatus>createBuilder()
                .name(name)
                .description(description)
                .binding(value.status(), () -> minecraft.debugEntries.getStatus(value.entry()), v -> minecraft.debugEntries.setStatus(value.entry(), v))
                .controller(o -> EnumControllerBuilder.create(o)
                        .enumClass(DebugScreenEntryStatus.class)
                        .formatValue(v -> Component.literal(DebugBuilder.debugEntryStatus(v))))
                .build();
    }
}
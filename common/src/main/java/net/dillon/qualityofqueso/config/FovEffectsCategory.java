package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.dillon.qualityofqueso.option.eum.fov_effects.Bows;
import net.dillon.qualityofqueso.option.eum.fov_effects.PotionEffects;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.universalOptionsInstance;

/**
 * The Fov effects options category for the {@link ConfigurationScreen}.
 */
public class FovEffectsCategory {

    protected static ConfigCategory create() {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("qualityofqueso.options.title.fov_effects"))
                .tooltip(Component.translatable("qualityofqueso.options.fov_effects.tooltip"))
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.title.fov_effects"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fov_effects.tooltip")))
                                .option(
                                        Option.<Integer>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.fov_effects.sprinting"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fov_effects.sprinting.description")))
                                                .binding(100, () -> clientOptionsInstance().getFovEffectOptions().sprinting, v -> clientOptionsInstance().getFovEffectOptions().sprinting = v)
                                                .controller(o -> IntegerSliderControllerBuilder.create(o)
                                                        .range(99, 200)
                                                        .step(1)
                                                        .formatValue(v -> v < 100 ? Component.literal("§7OFF") : Component.literal(v + "%"))
                                                )
                                                .available(universalOptionsInstance().getFunctions().applyFovEffects)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.fov_effects.flying"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fov_effects.flying.description")))
                                                .binding(true, () -> clientOptionsInstance().getFovEffectOptions().flying, v -> clientOptionsInstance().getFovEffectOptions().flying = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .available(universalOptionsInstance().getFunctions().applyFovEffects)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.fov_effects.fluids"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fov_effects.fluids.description")))
                                                .binding(true, () -> clientOptionsInstance().getFovEffectOptions().fluids, v -> clientOptionsInstance().getFovEffectOptions().fluids = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .available(universalOptionsInstance().getFunctions().applyFovEffects)
                                                .build()
                                )
                                .option(
                                        Option.<PotionEffects>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.fov_effects.potions"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fov_effects.potions.description")))
                                                .binding(PotionEffects.ENABLED, () -> clientOptionsInstance().getFovEffectOptions().potions, v -> clientOptionsInstance().getFovEffectOptions().potions = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(PotionEffects.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .available(universalOptionsInstance().getFunctions().applyFovEffects)
                                                .build()
                                )
                                .option(
                                        Option.<Bows>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.fov_effects.bows"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fov_effects.bows.description")))
                                                .binding(Bows.ENABLED, () -> clientOptionsInstance().getFovEffectOptions().bows, v -> clientOptionsInstance().getFovEffectOptions().bows = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(Bows.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .available(universalOptionsInstance().getFunctions().applyFovEffects)
                                                .build()
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.fov_effects.controls"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fov_effects.controls.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.fov_effects.disable_fov_effects"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fov_effects.disable_fov_effects.description")))
                                                .binding(false, () -> !universalOptionsInstance().getFunctions().applyFovEffects, v -> universalOptionsInstance().getFunctions().applyFovEffects = !v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .flag(OptionFlag.GAME_RESTART)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
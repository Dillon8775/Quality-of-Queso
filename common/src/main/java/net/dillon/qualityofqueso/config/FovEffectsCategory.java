package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.*;
import net.dillon.qualityofqueso.option.eum.fov_effects.Bows;
import net.dillon.qualityofqueso.option.eum.fov_effects.PotionEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.mixinOptionsInstance;

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
                                        Option.<Double>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.fov_effects.generic_fov_percent_change"))
                                                .description(OptionDescription.of(Component.translatable("options.fovEffectScale.tooltip")))
                                                .binding(1.0D, () -> Minecraft.getInstance().options.fovEffectScale().get(), v -> {
                                                    Minecraft.getInstance().options.fovEffectScale().set(v);
                                                    Minecraft.getInstance().options.save();
                                                })
                                                .controller(o -> DoubleSliderControllerBuilder.create(o)
                                                        .range(0.0D, 1.0D)
                                                        .step(0.01D)
                                                        .formatValue(v -> v == 0
                                                                ? Component.literal("§7OFF")
                                                                : Component.literal((int) (v * 100) + "%"))
                                                )
                                                .build()
                                )
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
                                                .available(mixinOptionsInstance().fovEffectsMixin)
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
                                                .available(mixinOptionsInstance().fovEffectsMixin)
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
                                                .available(mixinOptionsInstance().fovEffectsMixin)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.fov_effects.flying"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fov_effects.flying.description")))
                                                .binding(true, () -> clientOptionsInstance().getFovEffectOptions().flying, v -> clientOptionsInstance().getFovEffectOptions().flying = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .available(mixinOptionsInstance().fovEffectsMixin)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.fov_effects.fluids"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fov_effects.fluids.description")))
                                                .binding(true, () -> clientOptionsInstance().getFovEffectOptions().fluids, v -> clientOptionsInstance().getFovEffectOptions().fluids = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .available(mixinOptionsInstance().fovEffectsMixin)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.fov_effects.lock_fov"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fov_effects.lock_fov.description")))
                                                .binding(false, () -> clientOptionsInstance().getFovEffectOptions().lockFov, v -> clientOptionsInstance().getFovEffectOptions().lockFov = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .available(mixinOptionsInstance().fovEffectsMixin)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
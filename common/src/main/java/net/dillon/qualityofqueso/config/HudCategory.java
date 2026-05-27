package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.dillon.qualityofqueso.option.eum.hud.ArmorStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.dillon.qualityofqueso.config.ConfigurationScreen.fixedSizeImage;
import static net.dillon.qualityofqueso.config.ConfigurationScreen.fixedSizeWebpImage;
import static net.dillon.qualityofqueso.helper.ModHelper.*;

/**
 * The Hud options category for the {@link ConfigurationScreen}.
 */
public class HudCategory {

    protected static ConfigCategory create() {
        Option<Boolean> armorHotbar = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.armor_hotbar"))
                .description(value -> {
                    var builder = OptionDescription.createBuilder()
                            .text(Component.translatable("qualityofqueso.options.armor_hotbar.description"));

                    ResourceLocation location = value ? ofQoQ("textures/gui/sprites/options/hud/armor_hotbar.png") : ofQoQ("textures/gui/sprites/options/hud/no_armor_hotbar.png");
                    return builder.customImage(fixedSizeImage(location, 82, 21)).build();
                })
                .binding(true, () -> clientOptionsInstance().getHudOptions().armorHotbar, v -> clientOptionsInstance().getHudOptions().armorHotbar = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> highlightArmor = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.highlight_armor"))
                .description(value -> {
                    var builder = OptionDescription.createBuilder()
                            .text(Component.translatable("qualityofqueso.options.highlight_armor.description"));

                    ResourceLocation location = value ? ofQoQ("textures/gui/sprites/options/hud/highlight_armor.png") : ofQoQ("textures/gui/sprites/options/hud/armor_hotbar.png");
                    return builder.customImage(fixedSizeImage(location, 82, 21)).build();
                })
                .binding(true, () -> clientOptionsInstance().getHudOptions().highlightArmor, v -> clientOptionsInstance().getHudOptions().highlightArmor = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> emptySlots = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.empty_slots"))
                .description(value -> {
                    var builder = OptionDescription.createBuilder()
                            .text(Component.translatable("qualityofqueso.options.empty_slots.description"));

                    ResourceLocation location = value ? ofQoQ("textures/gui/sprites/options/hud/empty_slots.png") : ofQoQ("textures/gui/sprites/options/hud/no_empty_slots.png");
                    return builder.customImage(fixedSizeImage(location, 82, 21)).build();
                })
                .binding(true, () -> clientOptionsInstance().getHudOptions().emptySlots, v -> clientOptionsInstance().getHudOptions().emptySlots = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Double> animationTime = Option.<Double>createBuilder()
                .name(Component.translatable("qualityofqueso.options.animation_time"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.animation_time.description")))
                .binding(1.5D, () -> clientOptionsInstance().getHudOptions().animationTime, v -> clientOptionsInstance().getHudOptions().animationTime = v)
                .controller(o -> DoubleSliderControllerBuilder.create(o)
                        .range(0.25D, 3.0D)
                        .step(0.01D)
                        .formatValue(v -> Component.literal(roundBig(v) + " second(s)"))
                )
                .build();

        return ConfigCategory.createBuilder()
                .name(Component.translatable("qualityofqueso.options.title.hud"))
                .tooltip(Component.translatable("qualityofqueso.options.hud.tooltip"))
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.armor_status"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.armor_status.tooltip")))
                                .option(
                                        Option.<ArmorStatus>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.armor_status"))
                                                .description(
                                                        OptionDescription.createBuilder()
                                                                .text(Component.translatable("qualityofqueso.options.armor_status.description"))
                                                                .customImage(fixedSizeWebpImage(ofQoQ("textures/gui/sprites/options/hud/armor_status.webp"), 142))
                                                                .build()
                                                )
                                                .binding(ArmorStatus.ALWAYS, () -> clientOptionsInstance().getHudOptions().armorStatus, v -> clientOptionsInstance().getHudOptions().armorStatus = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(ArmorStatus.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        armorHotbar
                                )
                                .option(
                                        highlightArmor
                                )
                                .option(
                                        emptySlots
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.animations"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.animations.tooltip")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.animations"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.animations.description")))
                                                .binding(true, () -> clientOptionsInstance().getHudOptions().animations, v -> clientOptionsInstance().getHudOptions().animations = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        animationTime
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.hud.appearance"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.hud.appearance.tooltip")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.colored_highlighting"))
                                                .description(
                                                        OptionDescription.createBuilder()
                                                                .text(Component.translatable("qualityofqueso.options.colored_highlighting.description"))
                                                                .customImage(fixedSizeImage(ofQoQ("textures/gui/sprites/options/hud/colored_highlighting.png"), 29, 28))
                                                                .build())
                                                .binding(true, () -> clientOptionsInstance().getHudOptions().coloredHighlighting, v -> clientOptionsInstance().getHudOptions().coloredHighlighting = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .available(false)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.warning_indicators"))
                                                .description(OptionDescription.createBuilder()
                                                        .text(Component.translatable("qualityofqueso.options.warning_indicators.description"))
                                                        .customImage(fixedSizeImage(ofQoQ("textures/gui/sprites/options/hud/warning_indicators.png"), 29, 28))
                                                        .build())
                                                .binding(true, () -> clientOptionsInstance().getHudOptions().warningIndicators, v -> clientOptionsInstance().getHudOptions().warningIndicators = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .available(false)
                                                .build()
                                )
                                .option(
                                        Option.<Double>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.display_time"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.display_time.description")))
                                                .binding(4.0D, () -> clientOptionsInstance().getHudOptions().displayTime, v -> clientOptionsInstance().getHudOptions().displayTime = v)
                                                .controller(o -> DoubleSliderControllerBuilder.create(o)
                                                        .range(2.0, 8.0D)
                                                        .step(0.1D)
                                                        .formatValue(v -> Component.literal(round(v) + " second(s)"))
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}

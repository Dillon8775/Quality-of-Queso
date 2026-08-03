package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.dillon.qualityofqueso.option.eum.hud.ArmorStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static net.dillon.dillonlib.util.Arithmetics.round;
import static net.dillon.dillonlib.util.Arithmetics.roundBig;
import static net.dillon.qualityofqueso.config.ConfigurationScreen.fixedSizeImage;
import static net.dillon.qualityofqueso.config.ConfigurationScreen.fixedSizeWebpImage;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;

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

                    Identifier location = value ? ofQoQ("options/hud/armor_hotbar") : ofQoQ("options/hud/no_armor_hotbar");
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

                    Identifier location = value ? ofQoQ("options/hud/highlight_armor") : ofQoQ("options/hud/armor_hotbar");
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

                    Identifier location = value ? ofQoQ("options/hud/empty_slots") : ofQoQ("options/hud/no_empty_slots");
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
                                                .addListener((opt, event) -> {
                                                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                                                        boolean bl = !opt.pendingValue().off();
                                                        armorHotbar.setAvailable(bl);
                                                        highlightArmor.setAvailable(bl);
                                                        emptySlots.setAvailable(bl);
                                                    }
                                                })
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
                                                .addListener((opt, event) -> {
                                                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                                                        animationTime.setAvailable(opt.pendingValue());
                                                    }
                                                })
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
                                                                .customImage(fixedSizeImage(ofQoQ("options/hud/colored_highlighting"), 29, 28))
                                                                .build())
                                                .binding(true, () -> clientOptionsInstance().getHudOptions().coloredHighlighting, v -> clientOptionsInstance().getHudOptions().coloredHighlighting = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.warning_indicators"))
                                                .description(OptionDescription.createBuilder()
                                                        .text(Component.translatable("qualityofqueso.options.warning_indicators.description"))
                                                        .customImage(fixedSizeImage(ofQoQ("options/hud/warning_indicators"), 29, 28))
                                                        .build())
                                                .binding(true, () -> clientOptionsInstance().getHudOptions().warningIndicators, v -> clientOptionsInstance().getHudOptions().warningIndicators = v)
                                                .controller(BooleanControllerBuilder::create)
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

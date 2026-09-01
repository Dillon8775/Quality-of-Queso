package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.dillon.qualityofqueso.option.eum.hud.ArmorStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static net.dillon.dillonlib.util.Arithmetics.roundToHundredths;
import static net.dillon.dillonlib.util.Arithmetics.roundToTenths;
import static net.dillon.qualityofqueso.config.ConfigurationScreen.fixedSizeImage;
import static net.dillon.qualityofqueso.config.ConfigurationScreen.fixedSizeWebpImage;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

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

                    Identifier location = value ? qoqIdentifier("options/hud/armor_hotbar") : qoqIdentifier("options/hud/no_armor_hotbar");
                    return builder.customImage(fixedSizeImage(location, 82, 21)).build();
                })
                .binding(true, () -> client().hud().armorHotbar, v -> client().hud().armorHotbar = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> highlightArmor = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.highlight_armor"))
                .description(value -> {
                    var builder = OptionDescription.createBuilder()
                            .text(Component.translatable("qualityofqueso.options.highlight_armor.description"));

                    Identifier location = value ? qoqIdentifier("options/hud/highlight_armor") : qoqIdentifier("options/hud/armor_hotbar");
                    return builder.customImage(fixedSizeImage(location, 82, 21)).build();
                })
                .binding(true, () -> client().hud().highlightArmor, v -> client().hud().highlightArmor = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> emptySlots = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.empty_slots"))
                .description(value -> {
                    var builder = OptionDescription.createBuilder()
                            .text(Component.translatable("qualityofqueso.options.empty_slots.description"));

                    Identifier location = value ? qoqIdentifier("options/hud/empty_slots") : qoqIdentifier("options/hud/no_empty_slots");
                    return builder.customImage(fixedSizeImage(location, 82, 21)).build();
                })
                .binding(true, () -> client().hud().emptySlots, v -> client().hud().emptySlots = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Double> animationTime = Option.<Double>createBuilder()
                .name(Component.translatable("qualityofqueso.options.animation_time"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.animation_time.description")))
                .binding(1.5D, () -> client().hud().animationTime, v -> client().hud().animationTime = v)
                .controller(o -> DoubleSliderControllerBuilder.create(o)
                        .range(0.25D, 3.0D)
                        .step(0.01D)
                        .formatValue(v -> Component.literal(roundToHundredths(v) + " second(s)"))
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
                                                                .customImage(fixedSizeWebpImage(qoqIdentifier("textures/gui/sprites/options/hud/armor_status.webp"), 142))
                                                                .build()
                                                )
                                                .binding(ArmorStatus.ALWAYS, () -> client().hud().armorStatus, v -> client().hud().armorStatus = v)
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
                                                .binding(true, () -> client().hud().animations, v -> client().hud().animations = v)
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
                                                                .customImage(fixedSizeImage(qoqIdentifier("options/hud/colored_highlighting"), 29, 28))
                                                                .build())
                                                .binding(true, () -> client().hud().coloredHighlighting, v -> client().hud().coloredHighlighting = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.warning_indicators"))
                                                .description(OptionDescription.createBuilder()
                                                        .text(Component.translatable("qualityofqueso.options.warning_indicators.description"))
                                                        .customImage(fixedSizeImage(qoqIdentifier("options/hud/warning_indicators"), 29, 28))
                                                        .build())
                                                .binding(true, () -> client().hud().warningIndicators, v -> client().hud().warningIndicators = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Double>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.display_time"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.display_time.description")))
                                                .binding(4.0D, () -> client().hud().displayTime, v -> client().hud().displayTime = v)
                                                .controller(o -> DoubleSliderControllerBuilder.create(o)
                                                        .range(2.0, 8.0D)
                                                        .step(0.1D)
                                                        .formatValue(v -> Component.literal(roundToTenths(v) + " second(s)"))
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}

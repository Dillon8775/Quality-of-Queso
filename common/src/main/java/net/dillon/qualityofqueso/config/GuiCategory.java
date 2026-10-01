package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.impl.controller.TickBoxControllerBuilderImpl;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.config.ConfigurationScreen.fixedSizeImage;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.mixins;
import static net.dillon.qualityofqueso.util.ModOptionUtil.fabricOption;

/**
 * The Gui options category for the {@link ConfigurationScreen}.
 */
public class GuiCategory {

    protected static ConfigCategory create() {
        Option<Boolean> forceAntiRageQuit = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.force_anti_rage_quit"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.force_anti_rage_quit.description")))
                .binding(false, () -> client().misc().forceAntiRageQuit, v -> client().misc().forceAntiRageQuit = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        boolean canUseEnhancedTooltips = QualityOfQuesoPlatforms.getPlatform().platform().fabric() && mixins().itemStackMixin;

        Option<Boolean> showDamageValue = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.show_damage_value"))
                .description(
                        OptionDescription.createBuilder()
                                .text(fabricOption(Component.translatable("qualityofqueso.options.show_damage_value.description")))
                                .customImage(fixedSizeImage(qoqIdentifier("options/enhanced_durability_tooltips/value"), 152, 56))
                                .build()
                )
                .binding(true, () -> client().enhancedDurabilityTooltips().showDamageValue, v -> client().enhancedDurabilityTooltips().showDamageValue = v)
                .controller(TickBoxControllerBuilderImpl::new)
                .available(canUseEnhancedTooltips)
                .build();

        Option<Boolean> showPercentage = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.show_percentage"))
                .description(
                        OptionDescription.createBuilder()
                                .text(fabricOption(Component.translatable("qualityofqueso.options.show_percentage.description")))
                                .customImage(fixedSizeImage(qoqIdentifier("options/enhanced_durability_tooltips/percentage"), 104, 56))
                                .build()
                )
                .binding(false, () -> client().enhancedDurabilityTooltips().showPercentage, v -> client().enhancedDurabilityTooltips().showPercentage = v)
                .controller(TickBoxControllerBuilderImpl::new)
                .available(canUseEnhancedTooltips)
                .build();

        Option<Boolean> showDot = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.show_dot"))
                .description(
                        OptionDescription.createBuilder()
                                .text(fabricOption(Component.translatable("qualityofqueso.options.show_dot.description")))
                                .customImage(fixedSizeImage(qoqIdentifier("options/enhanced_durability_tooltips/dot"), 102, 34))
                                .build()
                )
                .binding(false, () -> client().enhancedDurabilityTooltips().showDot, v -> client().enhancedDurabilityTooltips().showDot = v)
                .controller(TickBoxControllerBuilderImpl::new)
                .available(canUseEnhancedTooltips)
                .build();

        return ConfigCategory.createBuilder()
                .name(Component.translatable("qualityofqueso.options.title.gui"))
                .tooltip(Component.translatable("qualityofqueso.options.gui.tooltip"))
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.gui.fine_tuning"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.gui.fine_tuning.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.enchantment_helper"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.enchantment_helper.description")))
                                                .description(
                                                        OptionDescription.createBuilder()
                                                                .text(Component.translatable("qualityofqueso.options.enchantment_helper.description"))
                                                                .customImage(fixedSizeImage(qoqIdentifier("options/gui/enchantment_helper"), 116, 88))
                                                                .build()
                                                )
                                                .binding(true, () -> client().misc().enchantmentHelper, v -> client().misc().enchantmentHelper = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.enhanced_cursor"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.enhanced_cursor.description")))
                                                .binding(true, () -> client().misc().enhancedCursor, v -> client().misc().enhancedCursor = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.quick_gui_exit"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.quick_gui_exit.description")))
                                                .binding(true, () -> client().misc().quickGuiExit, v -> client().misc().quickGuiExit = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.misc.recipe_book"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.misc.recipe_book.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.no_recipe_book_shift"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.no_recipe_book_shift.description")))
                                                .binding(false, () -> client().misc().noRecipeBookShift, v -> client().misc().noRecipeBookShift = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.auto_close_recipe_book"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.auto_close_recipe_book.description")))
                                                .binding(true, () -> client().misc().autoCloseRecipeBook, v -> client().misc().autoCloseRecipeBook = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.misc.anti_rage"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.misc.anti_rage.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.anti_rage_quit"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.anti_rage_quit.description")))
                                                .binding(false, () -> client().misc().antiRageQuit, v -> client().misc().antiRageQuit = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .addListener((opt, event) -> {
                                                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                                                        forceAntiRageQuit.setAvailable(opt.pendingValue());
                                                    }
                                                })
                                                .build()
                                )
                                .option(
                                        forceAntiRageQuit
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.misc.durability_tooltips"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.misc.durability_tooltips.description")))
                                .collapsed(!canUseEnhancedTooltips)
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.enhanced_durability_tooltips"))
                                                .description(OptionDescription.of(fabricOption(Component.translatable("qualityofqueso.options.enhanced_durability_tooltips.description"))))
                                                .binding(true, () -> client().enhancedDurabilityTooltips().enableEnhancedDurabilityTooltips, v -> client().enhancedDurabilityTooltips().enableEnhancedDurabilityTooltips = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .addListener((opt, event) -> {
                                                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                                                        boolean bl = !mixins().itemStackMixin && opt.pendingValue();
                                                        showDamageValue.setAvailable(bl);
                                                        showPercentage.setAvailable(bl);
                                                        showDot.setAvailable(bl);
                                                    }
                                                })
                                                .available(canUseEnhancedTooltips)
                                                .build()
                                )
                                .option(
                                        showDamageValue
                                )
                                .option(
                                        showPercentage
                                )
                                .option(
                                        showDot
                                )
                                .build()
                )
                .build();
    }
}
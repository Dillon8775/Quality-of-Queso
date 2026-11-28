package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.blay09.mods.balm.Balm;
import net.dillon.dillonlib.client.util.DebugTextAlignment;
import net.dillon.dillonlib.platform.info.Release;
import net.dillon.qualityofqueso.option.CommonOptionInstances;
import net.dillon.qualityofqueso.option.eum.general.Theme;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;
import net.dillon.qualityofqueso.util.ModOptionUtil;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.option.ClientOptionInstances.client;
import static net.dillon.qualityofqueso.option.ClientOptionInstances.mixins;

/**
 * The accessibility options category for the {@link ConfigurationScreen}.
 */
public class MiscellaneousCategory {

    protected static ConfigCategory create() {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("qualityofqueso.options.title.miscellaneous"))
                .tooltip(Component.translatable("qualityofqueso.options.miscellaneous.tooltip"))
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.misc.user_accessible"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.user_accessible.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.use_legacy_textures"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.use_legacy_textures.description")))
                                                .binding(false, () -> client().misc().useLegacyTextures, v -> client().misc().useLegacyTextures = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.reposition_bulk_craft_button"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.reposition_bulk_craft_button.description")))
                                                .binding(true, () -> client().misc().repositionBulkCraftButton, v -> client().misc().repositionBulkCraftButton = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.perpendicular_quick_moving"))
                                                .description(OptionDescription.of(
                                                        ModOptionUtil.serverSideOption(
                                                                Component.translatable("qualityofqueso.options.perpendicular_quick_moving.description")
                                                        )
                                                ))
                                                .binding(false, () -> client().misc().perpendicularQuickMoving, v -> client().misc().perpendicularQuickMoving = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.prevent_e_from_typing"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.prevent_e_from_typing.description")))
                                                .binding(true, () -> client().misc().preventEFromTyping, v -> client().misc().preventEFromTyping = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.search_inventory"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.search_inventory.description")))
                                                .binding(true, () -> client().misc().searchInventory, v -> client().misc().searchInventory = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("options.operatorItemsTab"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.operator_items_tab.description")))
                                                .binding(true, () -> client().misc().operatorItemsTab, v -> client().misc().operatorItemsTab = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.optimize_item_argument"))
                                                .description(OptionDescription.of(
                                                        ModOptionUtil.serverSideOption(
                                                                Component.translatable("qualityofqueso.options.optimize_item_argument.description")
                                                        )
                                                ))
                                                .binding(false, () -> CommonOptionInstances.common().optimizeItemArgument, v -> CommonOptionInstances.common().optimizeItemArgument = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .available(mixins().itemArgumentMixin)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.beta_warning"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.beta_warning.description")))
                                                .binding(true, () -> client().misc().betaWarning, v -> client().misc().betaWarning = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .available(QualityOfQuesoPlatforms.getPlatform().release() != Release.STABLE)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.server_warnings"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.server_warnings.description")))
                                                .binding(true, () -> client().misc().serverWarnings, v -> client().misc().serverWarnings = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.ignore_fabric_tags"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.ignore_fabric_tags.description")))
                                                .binding(false, () -> client().misc().ignoreFabricTags, v -> client().misc().ignoreFabricTags = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .available(Balm.platform().name().equals("fabric"))
                                                .build()
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.misc.appearance"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.appearance.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.red_armor_tint"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.red_armor_tint.description")))
                                                .binding(false, () -> client().misc().redArmorTint, v -> client().misc().redArmorTint = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .available(mixins().redArmorTintMixin)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.darker_overlay"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.darker_overlay.description")))
                                                .binding(false, () -> client().misc().darkerOverlay, v -> client().misc().darkerOverlay = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .available(client().general().theme == Theme.VANILLA)
                                                .build()
                                )
                                .option(
                                        Option.<DebugTextAlignment>createBuilder()
                                                .name(Component.translatable("dillonlib.option.debug_text_alignment"))
                                                .description(OptionDescription.of(Component.translatable("dillonlib.option.debug_text_alignment.description")))
                                                .binding(DebugTextAlignment.AUTO, () -> client().misc().debugTextAlignment, v -> client().misc().debugTextAlignment = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(DebugTextAlignment.class)
                                                        .formatValue(v -> Component.translatable(v.getSerializedName())))
                                                .flag(OptionFlag.GAME_RESTART)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
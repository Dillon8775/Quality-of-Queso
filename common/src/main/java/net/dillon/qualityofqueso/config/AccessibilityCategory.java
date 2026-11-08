package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.blay09.mods.balm.Balm;
import net.dillon.dillonlib.platform.info.Release;
import net.dillon.qualityofqueso.option.eum.general.Theme;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;
import net.dillon.qualityofqueso.util.ModOptionUtil;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.option.OptionInstances.*;

/**
 * The accessibility options category for the {@link ConfigurationScreen}.
 */
public class AccessibilityCategory {

    protected static ConfigCategory create() {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("qualityofqueso.options.title.accessibility"))
                .tooltip(Component.translatable("qualityofqueso.options.accessibility.tooltip"))
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.accessibility.user_accessible"))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.use_legacy_textures"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.use_legacy_textures.description")))
                                                .binding(false, () -> client().accessibility().useLegacyTextures, v -> client().accessibility().useLegacyTextures = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.reposition_bulk_craft_button"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.reposition_bulk_craft_button.description")))
                                                .binding(true, () -> client().accessibility().repositionBulkCraftButton, v -> client().accessibility().repositionBulkCraftButton = v)
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
                                                .binding(false, () -> client().accessibility().perpendicularQuickMoving, v -> client().accessibility().perpendicularQuickMoving = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.prevent_e_from_typing"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.prevent_e_from_typing.description")))
                                                .binding(true, () -> client().accessibility().preventEFromTyping, v -> client().accessibility().preventEFromTyping = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.search_inventory"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.search_inventory.description")))
                                                .binding(true, () -> client().accessibility().searchInventory, v -> client().accessibility().searchInventory = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("options.operatorItemsTab"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.operator_items_tab.description")))
                                                .binding(true, () -> client().accessibility().operatorItemsTab, v -> client().accessibility().operatorItemsTab = v)
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
                                                .binding(false, () -> common().optimizeItemArgument, v -> common().optimizeItemArgument = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .available(mixins().itemArgumentMixin)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.beta_warning"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.beta_warning.description")))
                                                .binding(true, () -> client().accessibility().betaWarning, v -> client().accessibility().betaWarning = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .available(mixins().titleScreenMixin && QualityOfQuesoPlatforms.getPlatform().release() != Release.STABLE)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.server_warnings"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.server_warnings.description")))
                                                .binding(true, () -> client().accessibility().serverWarnings, v -> client().accessibility().serverWarnings = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.ignore_fabric_tags"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.ignore_fabric_tags.description")))
                                                .binding(false, () -> client().accessibility().ignoreFabricTags, v -> client().accessibility().ignoreFabricTags = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .available(Balm.platform().name().equals("fabric"))
                                                .build()
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.accessibility.appearance"))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.darker_overlay"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.darker_overlay.description")))
                                                .binding(false, () -> client().accessibility().darkerOverlay, v -> client().accessibility().darkerOverlay = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .available(client().general().theme == Theme.VANILLA)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.dark_disc"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.dark_disc.description")))
                                                .binding(true, () -> client().accessibility().darkDisc, v -> client().accessibility().darkDisc = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
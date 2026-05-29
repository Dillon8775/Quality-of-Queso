package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.blay09.mods.balm.api.Balm;
import net.dillon.qualityofqueso.option.eum.accessibility.Theme;
import net.dillon.qualityofqueso.option.eum.misc.ViewLastKnownEnderChestButton;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

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
                                                .binding(false, () -> clientOptionsInstance().getAccessibilityOptions().useLegacyTextures, v -> clientOptionsInstance().getAccessibilityOptions().useLegacyTextures = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.perpendicular_quick_moving"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.perpendicular_quick_moving.description")))
                                                .binding(false, () -> clientOptionsInstance().getAccessibilityOptions().perpendicularQuickMoving, v -> clientOptionsInstance().getAccessibilityOptions().perpendicularQuickMoving = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.prevent_e_from_typing"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.prevent_e_from_typing.description")))
                                                .binding(true, () -> clientOptionsInstance().getAccessibilityOptions().preventEFromTyping, v -> clientOptionsInstance().getAccessibilityOptions().preventEFromTyping = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.search_inventory"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.search_inventory.description")))
                                                .binding(true, () -> clientOptionsInstance().getAccessibilityOptions().searchInventory, v -> clientOptionsInstance().getAccessibilityOptions().searchInventory = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.ignore_fabric_tags"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.ignore_fabric_tags.description")))
                                                .binding(false, () -> clientOptionsInstance().getAccessibilityOptions().ignoreFabricTags, v -> clientOptionsInstance().getAccessibilityOptions().ignoreFabricTags = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .available(Balm.getPlatform().equals("fabric"))
                                                .build()
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.accessibility.appearance"))
                                .option(
                                        Option.<ViewLastKnownEnderChestButton>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.echest_button"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.echest_button.description")))
                                                .binding(ViewLastKnownEnderChestButton.QOQ_MENU, () -> clientOptionsInstance().getAccessibilityOptions().eChestButton, v -> clientOptionsInstance().getAccessibilityOptions().eChestButton = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(ViewLastKnownEnderChestButton.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.darker_overlay"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.darker_overlay.description")))
                                                .binding(false, () -> clientOptionsInstance().getAccessibilityOptions().darkerOverlay, v -> clientOptionsInstance().getAccessibilityOptions().darkerOverlay = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .available(clientOptionsInstance().getGeneralOptions().theme == Theme.VANILLA)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.dark_disc"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.dark_disc.description")))
                                                .binding(true, () -> clientOptionsInstance().getAccessibilityOptions().darkDisc, v -> clientOptionsInstance().getAccessibilityOptions().darkDisc = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
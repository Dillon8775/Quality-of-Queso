package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.dillon.qualityofqueso.option.eum.general.MenuButton;
import net.dillon.qualityofqueso.option.eum.general.Theme;
import net.dillon.qualityofqueso.option.eum.general.Tooltips;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.config.ConfigurationScreen.fixedSizeImage;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.universal;

/**
 * The general options category for the {@link ConfigurationScreen}.
 */
public class GeneralCategory {

    protected static ConfigCategory create() {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("qualityofqueso.options.title.general"))
                .tooltip(Component.translatable("qualityofqueso.options.general.tooltip"))
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.title.general"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.general.tooltip")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.enable_mod"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.enable_mod.description")))
                                                .binding(true, () -> client().general().enableMod, v -> client().general().enableMod = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Theme>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.theme"))
                                                .description(value -> {
                                                    var builder = OptionDescription.createBuilder()
                                                            .text(Component.translatable("qualityofqueso.options.theme.description"));

                                                    return switch (value) {
                                                        case VANILLA -> builder.customImage(fixedSizeImage(qoqIdentifier("options/theme/vanilla"), 36, 36)).build();
                                                        case DARK -> builder.customImage(fixedSizeImage(qoqIdentifier("options/theme/dark"), 36, 36)).build();
                                                        case TRUE_DARK -> builder.customImage(fixedSizeImage(qoqIdentifier("options/theme/true_dark"), 136, 36)).build();
                                                        case TRANSPARENT -> builder.customImage(fixedSizeImage(qoqIdentifier("options/theme/transparent"), 36, 36)).build();
                                                    };
                                                })
                                                .binding(Theme.VANILLA, () -> client().general().theme, v -> client().general().theme = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(Theme.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<MenuButton>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.menu_button"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.menu_button.description")))
                                                .binding(MenuButton.EVERYWHERE, () -> universal().menuButton, v -> universal().menuButton = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(MenuButton.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<Tooltips>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.tooltips"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.tooltips.description")))
                                                .binding(Tooltips.DEFAULT, () -> client().general().tooltips, v -> client().general().tooltips = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(Tooltips.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.multi_server_configs"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.multi_server_configs.description")))
                                                .binding(false, () -> universal().multiServerConfigs, v -> universal().multiServerConfigs = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
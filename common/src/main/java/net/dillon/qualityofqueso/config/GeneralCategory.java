package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.dillon.qualityofqueso.option.eum.accessibility.MenuButton;
import net.dillon.qualityofqueso.option.eum.accessibility.Theme;
import net.dillon.qualityofqueso.option.eum.accessibility.Tooltips;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.config.ConfigurationScreen.fixedSizeImage;
import static net.dillon.qualityofqueso.helper.ModHelper.*;

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
                                                .binding(true, () -> clientOptionsInstance().getGeneralOptions().enableMod, v -> clientOptionsInstance().getGeneralOptions().enableMod = v)
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
                                                        case VANILLA -> builder.customImage(fixedSizeImage(ofQoQ("options/theme/vanilla"), 36, 36)).build();
                                                        case DARK -> builder.customImage(fixedSizeImage(ofQoQ("options/theme/dark"), 36, 36)).build();
                                                        case TRANSPARENT -> builder.customImage(fixedSizeImage(ofQoQ("options/theme/transparent"), 36, 36)).build();
                                                    };
                                                })
                                                .binding(Theme.VANILLA, () -> clientOptionsInstance().getGeneralOptions().theme, v -> clientOptionsInstance().getGeneralOptions().theme = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(Theme.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<MenuButton>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.menu_button"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.menu_button.description")))
                                                .binding(MenuButton.EVERYWHERE, () -> universalOptionsInstance().getUniversal().menuButton, v -> universalOptionsInstance().getUniversal().menuButton = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(MenuButton.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<Tooltips>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.tooltips"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.tooltips.description")))
                                                .binding(Tooltips.DEFAULT, () -> clientOptionsInstance().getGeneralOptions().tooltips, v -> clientOptionsInstance().getGeneralOptions().tooltips = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(Tooltips.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.multi_server_configs"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.multi_server_configs.description")))
                                                .binding(false, () -> universalOptionsInstance().getUniversal().multiServerConfigs, v -> universalOptionsInstance().getUniversal().multiServerConfigs = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
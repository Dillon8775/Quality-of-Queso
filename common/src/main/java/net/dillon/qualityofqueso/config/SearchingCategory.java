package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.*;
import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.option.eum.searching.QuickSearch;
import net.dillon.qualityofqueso.option.eum.searching.SearchBarColor;
import net.dillon.qualityofqueso.option.eum.searching.SearchBarPosition;
import net.dillon.qualityofqueso.util.ModConstants;
import net.dillon.qualityofqueso.util.ModOptionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.awt.*;

import static net.dillon.qualityofqueso.config.ConfigurationScreen.fixedSizeImage;
import static net.dillon.qualityofqueso.helper.ModHelper.*;

/**
 * The searching options category for the {@link ConfigurationScreen}.
 */
public class SearchingCategory {

    protected static ConfigCategory create() {
        Option<Color> searchBarTextColorOption = Option.<Color>createBuilder()
                .name(Component.translatable("qualityofqueso.options.search_bar_text_color"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.search_bar_text_color.description")))
                .binding(
                        new Color(ModConstants.DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR, true),
                        () -> new Color(clientOptionsInstance().getSearchingOptions().searchBarTextColor, true),
                        v -> clientOptionsInstance().getSearchingOptions().searchBarTextColor = v.getRGB()
                )
                .controller(ColorControllerBuilder::create)
                .available(false)
                .build();

        Option<Boolean> underlineTextOption = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.underline_text"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.underline_text.description")))
                .binding(false, () -> clientOptionsInstance().getSearchingOptions().underlineText, v -> clientOptionsInstance().getSearchingOptions().underlineText = v)
                .controller(TickBoxControllerBuilder::create)
                .available(false)
                .build();

        Option<Integer> itemFrameSearchRadius = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.item_frame_search_radius"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.item_frame_search_radius.tooltip")))
                .binding(150, () -> clientOptionsInstance().getMiscOptions().itemFrameSearchRadius, v -> clientOptionsInstance().getMiscOptions().itemFrameSearchRadius = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(20, 512)
                        .step(10)
                        .formatValue(v -> Component.literal(v + " blocks"))
                )
                .build();

        Option<Integer> itemFrameSearchGlowDuration = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.item_frame_search_glow_duration"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.item_frame_search_glow_duration.tooltip")))
                .binding(0, () -> clientOptionsInstance().getMiscOptions().itemFrameSearchGlowDuration, v -> clientOptionsInstance().getMiscOptions().itemFrameSearchGlowDuration = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(0, 180)
                        .step(1)
                        .formatValue(v -> {
                            if (v == 0) {
                                return Component.literal("Indefinite").withStyle(ChatFormatting.RED);
                            } else if (v < 60) {
                                return Component.literal(v + "s");
                            } else {
                                int minutes = v / 60;
                                int seconds = v % 60;
                                if (seconds == 0) {
                                    return Component.literal(minutes + "m");
                                } else {
                                    return Component.literal(minutes + "m " + seconds + "s");
                                }
                            }
                        })
                )
                .build();

        return ConfigCategory.createBuilder()
                .name(Component.translatable("qualityofqueso.options.title.searching"))
                .tooltip(Component.translatable("qualityofqueso.options.searching.tooltip"))
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.searching"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.searching.tooltip")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.container_searching"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.container_searching.description")))
                                                .binding(true, () -> clientOptionsInstance().getSearchingOptions().containerSearching, v -> clientOptionsInstance().getSearchingOptions().containerSearching = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.inventory_searching"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.inventory_searching.description")))
                                                .binding(true, () -> clientOptionsInstance().getSearchingOptions().inventorySearching, v -> clientOptionsInstance().getSearchingOptions().inventorySearching = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.searching.appearance"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.searching.appearance.description")))
                                .option(
                                        Option.<SearchBarPosition>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.search_bar_position"))
                                                .description(value -> {
                                                    var builder = OptionDescription.createBuilder()
                                                            .text(Component.translatable("qualityofqueso.options.search_bar_position.description"));

                                                    return switch (value) {
                                                        case OVERLAY -> builder.customImage(fixedSizeImage(ofQoQ("options/searching/overlay"), 160, 90)).build();
                                                        case TOP -> builder.customImage(fixedSizeImage(ofQoQ("options/searching/top"), 160, 90)).build();
                                                    };
                                                })
                                                .binding(SearchBarPosition.OVERLAY, () -> clientOptionsInstance().getSearchingOptions().searchBarPosition, v -> clientOptionsInstance().getSearchingOptions().searchBarPosition = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(SearchBarPosition.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<SearchBarColor>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.search_bar_color"))
                                                .description(value -> {
                                                    var builder = OptionDescription.createBuilder()
                                                            .text(Component.translatable("qualityofqueso.options.search_bar_color.description"));

                                                    return switch (value) {
                                                        case DEFAULT -> builder.customImage(fixedSizeImage(ofQoQ("options/searching/vanilla"), 117, 111)).build();
                                                        case BLACK -> builder.customImage(fixedSizeImage(ofQoQ("options/searching/black"), 117, 111)).build();
                                                    };
                                                })
                                                .binding(SearchBarColor.DEFAULT, () -> clientOptionsInstance().getSearchingOptions().searchBarColor, v -> clientOptionsInstance().getSearchingOptions().searchBarColor = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(SearchBarColor.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        searchBarTextColorOption
                                ).option(
                                        underlineTextOption
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.item_frame_searching"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.item_frame_searching.description",
                                        ModOptionUtil.keyMappingAsString(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI, false))))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.item_frame_searching"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.item_frame_searching.description",
                                                        ModOptionUtil.keyMappingAsString(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI, false))))
                                                .binding(true, () -> commonOptionsInstance().itemFrameSearching, v -> commonOptionsInstance().itemFrameSearching = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        itemFrameSearchRadius
                                )
                                .option(
                                        itemFrameSearchGlowDuration
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.searching.misc"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.searching.misc.description")))
                                .option(
                                        Option.<QuickSearch>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.quick_search"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.quick_search.description")))
                                                .binding(QuickSearch.ENABLED, () -> clientOptionsInstance().getSearchingOptions().quickSearch, v -> clientOptionsInstance().getSearchingOptions().quickSearch = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(QuickSearch.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.save_search_text"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.save_search_text.description")))
                                                .binding(false, () -> clientOptionsInstance().getSearchingOptions().saveSearchText, v -> clientOptionsInstance().getSearchingOptions().saveSearchText = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.dillon.qualityofqueso.option.eum.general.Theme;
import net.dillon.qualityofqueso.option.eum.searching.QuickSearch;
import net.dillon.qualityofqueso.option.eum.searching.SearchBarColor;
import net.dillon.qualityofqueso.option.eum.searching.SearchBarPosition;
import net.dillon.qualityofqueso.util.ModOptionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.awt.*;

import static net.dillon.qualityofqueso.config.ConfigurationScreen.fixedSizeImage;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.common;

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
                        () -> new Color(client().searching().searchBarTextColor, true),
                        v -> client().searching().searchBarTextColor = v.getRGB()
                )
                .controller(ColorControllerBuilder::create)
                .available(client().searching().searchBarColor.transparent())
                .build();

        Option<Boolean> underlineTextOption = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.underline_text"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.underline_text.description")))
                .binding(false, () -> client().searching().underlineText, v -> client().searching().underlineText = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Integer> itemFrameSearchRadius = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.item_frame_search_radius"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.item_frame_search_radius.tooltip")))
                .binding(150, () -> client().misc().itemFrameSearchRadius, v -> client().misc().itemFrameSearchRadius = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(20, 512)
                        .step(10)
                        .formatValue(v -> Component.literal(v + " blocks"))
                )
                .build();

        Option<Integer> itemFrameSearchGlowDuration = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.item_frame_search_glow_duration"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.item_frame_search_glow_duration.tooltip")))
                .binding(0, () -> client().misc().itemFrameSearchGlowDuration, v -> client().misc().itemFrameSearchGlowDuration = v)
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
                                                .binding(true, () -> client().searching().containerSearching, v -> client().searching().containerSearching = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.inventory_searching"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.inventory_searching.description")))
                                                .binding(true, () -> client().searching().inventorySearching, v -> client().searching().inventorySearching = v)
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
                                                        case OVERLAY -> builder.customImage(fixedSizeImage(qoqIdentifier("options/searching/overlay"), 160, 90)).build();
                                                        case TOP -> builder.customImage(fixedSizeImage(qoqIdentifier("options/searching/top"), 160, 90)).build();
                                                    };
                                                })
                                                .binding(SearchBarPosition.OVERLAY, () -> client().searching().searchBarPosition, v -> client().searching().searchBarPosition = v)
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
                                                        case DEFAULT -> builder.customImage(fixedSizeImage(qoqIdentifier("options/searching/default"), 117, 111)).build();
                                                        case VANILLA -> builder.customImage(fixedSizeImage(qoqIdentifier("options/searching/vanilla"), 117, 111)).build();
                                                        case BLACK -> builder.customImage(fixedSizeImage(qoqIdentifier("options/searching/black"), 117, 111)).build();
                                                    };
                                                })
                                                .binding(SearchBarColor.DEFAULT, () -> client().searching().searchBarColor, v -> client().searching().searchBarColor = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(SearchBarColor.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .addListener((opt, event) -> {
                                                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                                                        boolean bl = !opt.pendingValue().black() && (opt.pendingValue().transparent() || client().general().theme == Theme.TRANSPARENT);
                                                        searchBarTextColorOption.setAvailable(bl);
                                                        underlineTextOption.setAvailable(bl);
                                                    }
                                                })
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
                                .description(OptionDescription.of(
                                        ModOptionUtil.serverSideOption(
                                                Component.translatable("qualityofqueso.options.item_frame_searching.description",
                                                        ModOptionUtil.kumaKeyMappingAsString(ModKeyMappings.OPEN_ITEM_FRAME_SEARCH_GUI, false))
                                        )
                                ))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.item_frame_searching"))
                                                .description(OptionDescription.of(
                                                        ModOptionUtil.serverSideOption(
                                                                Component.translatable("qualityofqueso.options.item_frame_searching.description",
                                                                        ModOptionUtil.kumaKeyMappingAsString(ModKeyMappings.OPEN_ITEM_FRAME_SEARCH_GUI, false))
                                                        )
                                                ))
                                                .binding(true, () -> common().itemFrameSearching, v -> common().itemFrameSearching = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .addListener((opt, event) -> {
                                                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                                                        itemFrameSearchRadius.setAvailable(opt.pendingValue());
                                                        itemFrameSearchGlowDuration.setAvailable(opt.pendingValue());
                                                    }
                                                })
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
                                                .binding(QuickSearch.ENABLED, () -> client().searching().quickSearch, v -> client().searching().quickSearch = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(QuickSearch.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.save_search_text"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.save_search_text.description")))
                                                .binding(false, () -> client().searching().saveSearchText, v -> client().searching().saveSearchText = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.option.eum.accessibility.QoQButtons;
import net.dillon.qualityofqueso.option.eum.effects.Bows;
import net.dillon.qualityofqueso.option.eum.effects.PotionEffects;
import net.dillon.qualityofqueso.option.eum.hud.ArmorStatus;
import net.dillon.qualityofqueso.option.eum.hud.ItemCount;
import net.dillon.qualityofqueso.option.eum.management.*;
import net.dillon.qualityofqueso.option.eum.misc.ElytraAlarm;
import net.dillon.qualityofqueso.option.eum.searching.QuickSearch;
import net.dillon.qualityofqueso.option.instance.ModClientOptions;
import net.dillon.qualityofqueso.util.ModTexts;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

import static net.dillon.qualityofqueso.util.AccessorUtil.key;
import static net.dillon.qualityofqueso.util.ModUtil.*;

/**
 * Options displayed on ModOptionsScreen.
 */
public class ModListOptions {
    public static final OptionInstance.CaptionBasedToString<Boolean> ON_OFF_TEXT = (component, bl) -> bl
            ? ModTexts.ON
            : ModTexts.OFF;
    public static final OptionInstance.CaptionBasedToString<Boolean> YES_NO_TEXT = (component, bl) -> bl
            ? ModTexts.YES
            : ModTexts.NO;

    // SEARCHING OPTIONS
    public static OptionInstance<Boolean> containerSearching() {
        return OptionInstance.createBoolean("qualityofqueso.options.container_searching", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.container_searching.tooltip")),
                ON_OFF_TEXT, options().searching.containerSearching, value -> options().searching.containerSearching = value);
    }

    public static OptionInstance<Boolean> inventorySearching() {
        return OptionInstance.createBoolean("qualityofqueso.options.inventory_searching", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.inventory_searching.tooltip")),
                ON_OFF_TEXT, options().searching.inventorySearching, value -> options().searching.inventorySearching = value);
    }

    public static OptionInstance<QuickSearch> quickSearch() {
        return new OptionInstance<>(
                "qualityofqueso.options.quick_search",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case RECIPE_BOOK -> text = Component.translatable("qualityofqueso.options.quick_search.recipe_book.tooltip");
                        case SEARCH_BAR -> text = Component.translatable("qualityofqueso.options.quick_search.search_bar.tooltip");
                    }
                    return Tooltip.create(Component.translatable("qualityofqueso.options.quick_search.tooltip").copy().append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(QuickSearch.values()), QuickSearch.Codec),
                options().searching.quickSearch,
                value -> options().searching.quickSearch = value);
    }

    public static OptionInstance<Boolean> saveSearchText() {
        return OptionInstance.createBoolean("qualityofqueso.options.save_search_text", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.save_search_text.tooltip")),
                YES_NO_TEXT, options().searching.saveSearchText, value -> options().searching.saveSearchText = value);
    }

    public static OptionInstance<Boolean> transparentSearchBar() {
        return OptionInstance.createBoolean("qualityofqueso.options.transparent_search_bar", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.transparent_search_bar.tooltip")),
                ON_OFF_TEXT, options().searching.transparentSearchBar, value -> options().searching.transparentSearchBar = value);
    }
    // end of searching options

    /* ===== */
    public static OptionInstance<ButtonLayout> buttonLayout() {
        return new OptionInstance<>(
                "qualityofqueso.options.button_layout",
                option -> {
                    return switch (option) {
                        case VERTICAL -> Tooltip.create(Component.translatable("qualityofqueso.options.button_layout.vertical.tooltip"));
                        case HORIZONTAL -> Tooltip.create(Component.translatable("qualityofqueso.options.button_layout.horizontal.tooltip"));
                    };
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(ButtonLayout.values()), ButtonLayout.Codec),
                options().management.buttonLayout,
                value -> options().management.buttonLayout = value);
    }

    // MANAGEMENT
    public static OptionInstance<Transferring> transferring() {
        return new OptionInstance<>(
                "qualityofqueso.options.transferring",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case SHORTCUT_KEY_OR_BUTTON ->
                                text = Component.translatable("qualityofqueso.options.transferring.shortcut_key_or_button.tooltip");
                        case SHORTCUT_KEY_ONLY ->
                                text = Component.translatable("qualityofqueso.options.transferring.shortcut_key_only.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.create(Component.translatable("qualityofqueso.options.transferring.tooltip", keyMappingAsString(ModKeybinds.MOVE_INVENTORY), keyMappingAsString(ModKeybinds.MOVE_CONTAINER)).append(appended).append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(Transferring.values()), Transferring.Codec),
                options().management.transferring,
                value -> options().management.transferring = value);
    }

    public static OptionInstance<ContainerSorting> containerSorting() {
        return new OptionInstance<>(
                "qualityofqueso.options.container_sorting",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case SHORTCUT_KEY_OR_BUTTON ->
                                text = Component.translatable("qualityofqueso.options.container_sorting.shortcut_key_or_button.tooltip");
                        case SHORTCUT_KEY_ONLY ->
                                text = Component.translatable("qualityofqueso.options.container_sorting.shortcut_key_only.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.create(Component.translatable("qualityofqueso.options.container_sorting.tooltip", keyMappingAsString(ModKeybinds.SORT_CONTAINER)).append(appended).append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(ContainerSorting.values()), ContainerSorting.Codec),
                options().management.containerSorting,
                value -> options().management.containerSorting = value);
    }

    public static OptionInstance<Boolean> containerFiltering() {
        return OptionInstance.createBoolean("qualityofqueso.options.container_filtering", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.container_filtering.tooltip")),
                ON_OFF_TEXT, options().management.containerFiltering, value -> options().management.containerFiltering = value);
    }

    public static OptionInstance<QuickDrop> quickDrop() {
        return new OptionInstance<>(
                "qualityofqueso.options.quick_dropping",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case SHORTCUT_KEY_OR_BUTTON -> text = Component.translatable("qualityofqueso.options.quick_dropping.shortcut_key_or_button.tooltip");
                        case SHORTCUT_KEY_ONLY -> text = Component.translatable("qualityofqueso.options.quick_dropping.shortcut_key_only.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.create(Component.translatable("qualityofqueso.options.quick_dropping.tooltip").append(appended).append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(QuickDrop.values()), QuickDrop.Codec),
                options().management.quickDrop,
                value -> options().management.quickDrop = value);
    }

    public static OptionInstance<Swapping> swapping() {
        return new OptionInstance<>(
                "qualityofqueso.options.swapping",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case SHORTCUT_KEY_OR_BUTTON -> text = Component.translatable("qualityofqueso.options.swapping.shortcut_key_or_button.tooltip");
                        case SHORTCUT_KEY_ONLY -> text = Component.translatable("qualityofqueso.options.swapping.shortcut_key_only.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.create(Component.translatable("qualityofqueso.options.swapping.tooltip", keyMappingAsString(ModKeybinds.SWAP_ITEMS)).append(appended).append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(Swapping.values()), Swapping.Codec),
                options().management.swapping,
                value -> options().management.swapping = value);
    }

    public static OptionInstance<Boolean> dragSorting() {
        return OptionInstance.createBoolean("qualityofqueso.options.drag_sorting", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.drag_sorting.tooltip")),
                ON_OFF_TEXT, options().management.dragSorting, value -> options().management.dragSorting = value);
    }

    public static OptionInstance<Boolean> tagSorting() {
        return OptionInstance.createBoolean("qualityofqueso.options.tag_sorting", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.tag_sorting.tooltip")),
                ON_OFF_TEXT, options().management.tagSorting, value -> options().management.tagSorting = value);
    }

    public static OptionInstance<DisplayIncludeHotbar> displayIncludeHotbar() {
        return new OptionInstance<>(
                "qualityofqueso.options.display_include_hotbar",
                option -> {
                    return switch (option) {
                        case ALWAYS -> Tooltip.create(Component.translatable("qualityofqueso.options.display_include_hotbar.always.tooltip"));
                        case CONTAINER_SCREENS_ONLY -> Tooltip.create(Component.translatable("qualityofqueso.options.display_include_hotbar.container_screens_only.tooltip"));
                    };
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(DisplayIncludeHotbar.values()), DisplayIncludeHotbar.Codec),
                options().buttonDisplayOptions.displayIncludeHotbar,
                value -> options().buttonDisplayOptions.displayIncludeHotbar = value);
    }

    public static OptionInstance<DisplayFillWhatsPresent> displayFillWhatsPresent() {
        return new OptionInstance<>(
                "qualityofqueso.options.display_fill_whats_present",
                option -> {
                    return switch (option) {
                        case ALWAYS -> Tooltip.create(Component.translatable("qualityofqueso.options.display_fill_whats_present.always.tooltip"));
                        case FILTERED_CONTAINERS_ONLY -> Tooltip.create(Component.translatable("qualityofqueso.options.display_fill_whats_present.filtered_containers_only.tooltip"));
                    };
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(DisplayFillWhatsPresent.values()), DisplayFillWhatsPresent.Codec),
                options().buttonDisplayOptions.displayFillWhatsPresent,
                value -> options().buttonDisplayOptions.displayFillWhatsPresent = value);
    }

    public static OptionInstance<Boolean> displaySearchTransportables() {
        return OptionInstance.createBoolean("qualityofqueso.options.display_search_transportables", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.display_search_transportables.tooltip")),
                ON_OFF_TEXT, options().buttonDisplayOptions.displaySearchTransportables, value -> options().buttonDisplayOptions.displaySearchTransportables = value);
    }

    public static OptionInstance<Boolean> displayAlwaysQuickMove() {
        return OptionInstance.createBoolean("qualityofqueso.options.display_always_quick_move", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.display_always_quick_move.tooltip")),
                ON_OFF_TEXT, options().buttonDisplayOptions.displayAlwaysQuickMove, value -> options().buttonDisplayOptions.displayAlwaysQuickMove = value);
    }
    // end of inventory management options

    /* ===== */

    // ITEM FRAME SEARCHING
    public static OptionInstance<Boolean> itemFrameSearching() {
        return OptionInstance.createBoolean("qualityofqueso.options.item_frame_searching", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.item_frame_searching.tooltip",
                        keyMappingAsString(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI))),
                ON_OFF_TEXT, coptions().itemFrameSearching, value -> coptions().itemFrameSearching = value);
    }

    public static OptionInstance<Integer> itemFrameSearchGlowDuration() {
        return new OptionInstance<>("qualityofqueso.options.item_frame_search_glow_duration",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.item_frame_search_glow_duration.tooltip")),
                (optionText, value) -> {
                    if (value == 0) {
                        return Options.genericValueLabel(optionText, Component.literal("Indefinite").withStyle(ChatFormatting.RED));
                    } else if (value < 60) {
                        return Options.genericValueLabel(optionText, Component.literal(value + "s"));
                    } else {
                        int minutes = value / 60;
                        int seconds = value % 60;
                        if (seconds == 0) {
                            return Options.genericValueLabel(optionText, Component.literal(minutes + "m"));
                        } else {
                            return Options.genericValueLabel(optionText, Component.literal(minutes + "m " + seconds + "s"));
                        }
                    }
                },
                new OptionInstance.IntRange(0, 180), options().misc.itemFrameSearchGlowDuration, value -> options().misc.itemFrameSearchGlowDuration = value);
    }

    public static OptionInstance<Integer> itemFrameSearchRadius() {
        return new OptionInstance<>("qualityofqueso.options.item_frame_search_radius",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.item_frame_search_radius.tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + "m")),
                new OptionInstance.IntRange(25, 500), options().misc.itemFrameSearchRadius, value -> options().misc.itemFrameSearchRadius = value);
    }
    // end of item frame searching options

    /* ===== */

    // HUD
    public static OptionInstance<ArmorStatus> armorStatus() {
        return new OptionInstance<>(
                "qualityofqueso.options.armor_status",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case ON -> text = Component.translatable("qualityofqueso.options.armor_status.on.tooltip");
                        case ON_UPDATE -> text = Component.translatable("qualityofqueso.options.armor_status.on_update.tooltip");
                    }
                    return Tooltip.create(Component.translatable("qualityofqueso.options.armor_status.tooltip").copy().append(text != ModTexts.BLANK ? "\n\n" : "").append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(ArmorStatus.values()), ArmorStatus.Codec),
                options().hud.armorStatus,
                value -> {
                    options().hud.armorStatus = value;
                    ModClientOptions.CLIENT.save();
                    resetArmorHudState();
                });
    }

    public static OptionInstance<Boolean> armorHotbar() {
        return OptionInstance.createBoolean("qualityofqueso.options.armor_hotbar", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.armor_hotbar.tooltip")),
                ON_OFF_TEXT, options().hud.armorHotbar, value -> {
            options().hud.armorHotbar = value;
            ModClientOptions.CLIENT.save();
            resetArmorHudState();
        });
    }

    public static OptionInstance<Boolean> coloredHighlighting() {
        return OptionInstance.createBoolean("qualityofqueso.options.colored_highlighting", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.colored_highlighting.tooltip")),
                ON_OFF_TEXT, options().hud.coloredHighlighting, value -> options().hud.coloredHighlighting = value);
    }

    public static OptionInstance<Boolean> warningIndicators() {
        return OptionInstance.createBoolean("qualityofqueso.options.warning_indicators", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.warning_indicators.tooltip")),
                ON_OFF_TEXT, options().hud.warningIndicators, value -> options().hud.warningIndicators = value);
    }

    // ITEM COUNT
    public static OptionInstance<ItemCount> itemCount() {
        return new OptionInstance<>(
                "qualityofqueso.options.item_count",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case TOTAL -> text = Component.translatable("qualityofqueso.options.item_count.total.tooltip");
                        case STACKS -> text = Component.translatable("qualityofqueso.options.item_count.stacks.tooltip");
                    }
                    return Tooltip.create(Component.translatable("qualityofqueso.options.item_count.tooltip").append(text != ModTexts.BLANK ? "\n\n" : "").append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(ItemCount.values()), ItemCount.Codec),
                options().hud.itemCount,
                value -> options().hud.itemCount = value);
    }

    public static OptionInstance<Boolean> displayOnThrow() {
        return OptionInstance.createBoolean("qualityofqueso.options.display_on_throw", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.display_on_throw.tooltip")),
                YES_NO_TEXT, options().hud.displayOnThrow, value -> options().hud.displayOnThrow = value);
    }

    public static OptionInstance<Boolean> displayOnPickup() {
        return OptionInstance.createBoolean("qualityofqueso.options.display_on_pickup", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.display_on_pickup.tooltip")),
                YES_NO_TEXT, options().hud.displayOnPickup, value -> options().hud.displayOnPickup = value);
    }

    public static OptionInstance<Boolean> countContainers() {
        return OptionInstance.createBoolean("qualityofqueso.options.count_containers", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.count_containers.tooltip")),
                YES_NO_TEXT, options().hud.countContainers, value -> options().hud.countContainers = value);
    }

    public static OptionInstance<Boolean> showArrowCount() {
        return OptionInstance.createBoolean("qualityofqueso.options.show_arrow_count", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.show_arrow_count.tooltip")),
                YES_NO_TEXT, options().hud.showArrowCount, value -> options().hud.showArrowCount = value);
    }

    public static OptionInstance<Boolean> countAllArrows() {
        return OptionInstance.createBoolean("qualityofqueso.options.count_all_arrows", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.count_all_arrows.tooltip")),
                YES_NO_TEXT, options().hud.countAllArrows, value -> options().hud.countAllArrows = value);
    }
    // end of hud/item count options

    /* ===== */

    // MISC
    public static OptionInstance<ElytraAlarm> elytraAlarm() {
        return new OptionInstance<>(
                "qualityofqueso.options.elytra_alarm",
                option -> {
                    Component text;
                    switch (option) {
                        case INDICATOR_ONLY -> text = Component.translatable("qualityofqueso.options.elytra_alarm.indicator_only.tooltip");
                        default -> text = ModTexts.BLANK;
                    }
                    return Tooltip.create(Component.translatable("qualityofqueso.options.elytra_alarm.tooltip", options().elytraAlarm.minFallDistance).append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(ElytraAlarm.values()), ElytraAlarm.Codec),
                options().elytraAlarm.elytraAlarm,
                value -> options().elytraAlarm.elytraAlarm = value);
    }

    public static OptionInstance<Integer> minElytraFallDistance() {
        return new OptionInstance<>("qualityofqueso.options.min_elytra_fall_distance",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.min_elytra_fall_distance.tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + " blocks")),
                new OptionInstance.IntRange(10, 100), options().elytraAlarm.minFallDistance, value -> options().elytraAlarm.minFallDistance = value);
    }

    public static OptionInstance<Boolean> mobHitDing() {
        return OptionInstance.createBoolean("qualityofqueso.options.mob_hit_ding", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.mob_hit_ding.tooltip", options().misc.minMobHitDingDistance)),
                ON_OFF_TEXT, options().misc.mobHitDing, value -> options().misc.mobHitDing = value);
    }

    public static OptionInstance<Integer> minMobHitDingDistance() {
        return new OptionInstance<>("qualityofqueso.options.min_mob_hit_ding_distance",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.min_mob_hit_ding_distance.tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + " blocks")),
                new OptionInstance.IntRange(10, 50), options().misc.minMobHitDingDistance, value -> options().misc.minMobHitDingDistance = value);
    }

    public static OptionInstance<Boolean> quickEquip() {
        return OptionInstance.createBoolean("qualityofqueso.options.quick_equip", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.quick_equip.tooltip",
                        keyMappingAsString(ModKeybinds.QUICK_EQUIP))),
                ON_OFF_TEXT, options().misc.quickEquip, value -> options().misc.quickEquip = value);
    }

    public static OptionInstance<Boolean> enchantingHelper() {
        return OptionInstance.createBoolean("qualityofqueso.options.enchanting_helper", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.enchanting_helper.tooltip")),
                ON_OFF_TEXT, options().misc.enchantingHelper, value -> options().misc.enchantingHelper = value);
    }

    public static OptionInstance<Boolean> quickGuiExit() {
        return OptionInstance.createBoolean("qualityofqueso.options.quick_gui_exit", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.quick_gui_exit.tooltip")),
                ON_OFF_TEXT, options().misc.quickGuiExit, value -> options().misc.quickGuiExit = value);
    }

    public static OptionInstance<Boolean> allFog() {
        return OptionInstance.createBoolean("qualityofqueso.options.all_fog", OptionInstance.cachedConstantTooltip(
                        Component.translatable("qualityofqueso.options.all_fog.tooltip")),
                ON_OFF_TEXT, options().fog.allFog, value -> options().fog.allFog = value);
    }

    public static OptionInstance<Boolean> overworldFog() {
        return OptionInstance.createBoolean("qualityofqueso.options.overworld_fog", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.overworld_fog.tooltip")),
                ON_OFF_TEXT, options().fog.overworldFog, value -> options().fog.overworldFog = value);
    }

    public static OptionInstance<Boolean> netherFog() {
        return OptionInstance.createBoolean("qualityofqueso.options.nether_fog", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.nether_fog.tooltip")),
                ON_OFF_TEXT, options().fog.netherFog, value -> options().fog.netherFog = value);
    }

    public static OptionInstance<Integer> netherFogIntensity() {
        return new OptionInstance<>("qualityofqueso.options.nether_fog_intensity",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.nether_fog_intensity.tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + "%")),
                new OptionInstance.IntRange(5, 100), options().fog.netherFogIntensity, value -> options().fog.netherFogIntensity = value);
    }

    public static OptionInstance<Boolean> preventRageQuitting() {
        return OptionInstance.createBoolean("qualityofqueso.options.prevent_rage_quitting", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.prevent_rage_quitting.tooltip")),
                YES_NO_TEXT, options().misc.preventRageQuitting, value -> options().misc.preventRageQuitting = value);
    }

    public static OptionInstance<Boolean> alwaysPreventRageQuitting() {
        return OptionInstance.createBoolean("qualityofqueso.options.always_prevent_rage_quitting", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.always_prevent_rage_quitting.tooltip")),
                YES_NO_TEXT, options().misc.alwaysPreventRageQuitting, value -> options().misc.alwaysPreventRageQuitting = value);
    }

    public static OptionInstance<Boolean> fortniteBattlePass() {
        return OptionInstance.createBoolean("qualityofqueso.options.fortnite_battle_pass", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.fortnite_battle_pass.tooltip")),
                ON_OFF_TEXT, options().misc.fortniteBattlePass, value -> {
            options().misc.fortniteBattlePass = value;
            ModClientOptions.CLIENT.save();
        });
    }
    // end of misc options

    /* ===== */

    // ENABLE MOD
    public static OptionInstance<Boolean> enableQoQ() {
        return OptionInstance.createBoolean("qualityofqueso.options.enable_mod", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.enable_mod.tooltip")),
                YES_NO_TEXT, options().accessibility.enableMod, value -> options().accessibility.enableMod = value);
    }
    // end of enable mod

    /* ===== */

    // ACCESSIBILITY
    public static OptionInstance<Boolean> helpfulTooltips() {
        return OptionInstance.createBoolean("qualityofqueso.options.helpful_tooltips", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.helpful_tooltips.tooltip")),
                ON_OFF_TEXT, options().accessibility.helpfulTooltips, value -> options().accessibility.helpfulTooltips = value);
    }

    public static OptionInstance<Boolean> preventEFromTyping() {
        return OptionInstance.createBoolean("qualityofqueso.options.prevent_e_from_typing", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.prevent_e_from_typing.tooltip")),
                YES_NO_TEXT, options().accessibility.preventEFromTyping, value -> options().accessibility.preventEFromTyping = value);
    }

    public static OptionInstance<Boolean> searchInventory() {
        return OptionInstance.createBoolean("qualityofqueso.options.search_inventory", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.search_inventory.tooltip")),
                YES_NO_TEXT, options().accessibility.searchInventory, value -> options().accessibility.searchInventory = value);
    }

    public static OptionInstance<Boolean> showButtonShortcuts() {
        return OptionInstance.createBoolean("qualityofqueso.options.show_button_shortcuts", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.show_button_shortcuts.tooltip")),
                YES_NO_TEXT, options().accessibility.showButtonShortcuts, value -> options().accessibility.showButtonShortcuts = value);
    }

    public static OptionInstance<Boolean> autoCloseRecipeBook() {
        return OptionInstance.createBoolean("qualityofqueso.options.auto_close_recipe_book", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.auto_close_recipe_book.tooltip", options().accessibility.autoCloseRecipeBook)),
                ON_OFF_TEXT, options().accessibility.autoCloseRecipeBook, value -> options().accessibility.autoCloseRecipeBook = value);
    }

    public static OptionInstance<Boolean> useOldSearchBarTexture() {
        return OptionInstance.createBoolean("qualityofqueso.options.use_old_search_bar_texture", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.use_old_search_bar_texture.tooltip")),
                YES_NO_TEXT, options().accessibility.useOldSearchBarTexture, value -> options().accessibility.useOldSearchBarTexture = value);
    }

    public static OptionInstance<Boolean> onlyCountMatchingItems() {
        return OptionInstance.createBoolean("qualityofqueso.options.only_count_matching_items", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.only_count_matching_items.tooltip")),
                YES_NO_TEXT, options().accessibility.onlyCountMatchingItems, value -> options().accessibility.onlyCountMatchingItems = value);
    }

    public static OptionInstance<Boolean> displayTotalWithStacks() {
        return OptionInstance.createBoolean("qualityofqueso.options.display_total_with_stacks", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.display_total_with_stacks.tooltip")),
                YES_NO_TEXT, options().accessibility.displayTotalWithStacks, value -> options().accessibility.displayTotalWithStacks = value);
    }

    public static OptionInstance<Boolean> perpendicularQuickMoving() {
        return OptionInstance.createBoolean("qualityofqueso.options.perpendicular_quick_moving", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.perpendicular_quick_moving.tooltip")),
                ON_OFF_TEXT, options().accessibility.perpendicularQuickMoving, value -> options().accessibility.perpendicularQuickMoving = value);
    }

    public static OptionInstance<Integer> elytraAlarmSoundDelay() {
        return new OptionInstance<>("qualityofqueso.options.elytra_alarm_sound_delay",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.elytra_alarm_sound_delay.tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + " tick(s)")),
                new OptionInstance.IntRange(1, 120), options().elytraAlarm.soundDelayTicks, value -> options().elytraAlarm.soundDelayTicks = value);
    }

    public static OptionInstance<Boolean> ignoreFabricTags() {
        return OptionInstance.createBoolean("qualityofqueso.options.ignore_fabric_tags", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.ignore_fabric_tags.tooltip")),
                ON_OFF_TEXT, options().accessibility.ignoreFabricTags, value -> options().accessibility.ignoreFabricTags = value);
    }

    public static OptionInstance<ButtonSounds> buttonSounds() {
        return new OptionInstance<>(
                "qualityofqueso.options.button_sounds",
                option -> {
                    return switch (option) {
                        case ALL -> Tooltip.create(Component.translatable("qualityofqueso.options.button_sounds.all.tooltip"));
                        case BUNDLE_ONLY -> Tooltip.create(Component.translatable("qualityofqueso.options.button_sounds.bundle_only.tooltip"));
                        case CLICK_ONLY -> Tooltip.create(Component.translatable("qualityofqueso.options.button_sounds.click_only.tooltip"));
                        case OFF -> Tooltip.create(Component.translatable("qualityofqueso.options.button_sounds.off.tooltip"));
                    };
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(ButtonSounds.values()), ButtonSounds.Codec),
                options().management.buttonSounds,
                value -> options().management.buttonSounds = value);
    }

    public static OptionInstance<QoQButtons> qoqButtons() {
        return new OptionInstance<>(
                "qualityofqueso.options.qoq_buttons",
                option -> {
                    return switch (option) {
                        case EVERYWHERE -> Tooltip.create(Component.translatable("qualityofqueso.options.qoq_buttons.everywhere.tooltip"));
                        case BOTTOM_LEFT -> Tooltip.create(Component.translatable("qualityofqueso.options.qoq_buttons.bottom_left.tooltip"));
                        case BOTTOM_RIGHT -> Tooltip.create(Component.translatable("qualityofqueso.options.qoq_buttons.bottom_right.tooltip"));
                        case TITLE_ONLY -> Tooltip.create(Component.translatable("qualityofqueso.options.qoq_buttons.title_only.tooltip"));
                        case OFF -> Tooltip.create(Component.translatable("qualityofqueso.options.qoq_buttons.off.tooltip"));
                    };
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(QoQButtons.values()), QoQButtons.Codec),
                options().accessibility.qoqButtons,
                value -> options().accessibility.qoqButtons = value);
    }

    public static OptionInstance<Boolean> multiServerConfigs() {
        return OptionInstance.createBoolean("qualityofqueso.options.multi_server_configs", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.multi_server_configs.tooltip")),
                ON_OFF_TEXT, uoptions().main.multiServerConfigs, value -> {
            uoptions().main.multiServerConfigs = value;
            ModUtil.saveAll(Minecraft.getInstance());
        });
    }
    // end of accessibility options

    /* ===== */

    // FOV EFFECTS
    public static OptionInstance<Boolean> sprinting() {
        return OptionInstance.createBoolean("qualityofqueso.options.fov_effects.sprinting", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.fov_effects.sprinting.tooltip")),
                ON_OFF_TEXT, options().fovEffects.sprinting, value -> options().fovEffects.sprinting = value);
    }

    public static OptionInstance<Boolean> flying() {
        return OptionInstance.createBoolean("qualityofqueso.options.fov_effects.flying", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.fov_effects.flying.tooltip")),
                ON_OFF_TEXT, options().fovEffects.flying, value -> options().fovEffects.flying = value);
    }

    public static OptionInstance<PotionEffects> potionEffects() {
        return new OptionInstance<>(
                "qualityofqueso.options.fov_effects.potion_effects",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case NON_BEACON -> text = Component.translatable("qualityofqueso.options.fov_effects.potion_effects.non_beacon.tooltip");
                    }
                    return Tooltip.create(Component.translatable("qualityofqueso.options.fov_effects.potion_effects.tooltip").copy().append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(PotionEffects.values()), PotionEffects.Codec),
                options().fovEffects.potionEffects,
                value -> options().fovEffects.potionEffects = value);
    }

    public static OptionInstance<Boolean> fluids() {
        return OptionInstance.createBoolean("qualityofqueso.options.fov_effects.fluids", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.fov_effects.fluids.tooltip")),
                ON_OFF_TEXT, options().fovEffects.fluids, value -> options().fovEffects.fluids = value);
    }

    public static OptionInstance<Bows> bows() {
        return new OptionInstance<>(
                "qualityofqueso.options.fov_effects.bows",
                option -> {
                    Component text;
                    switch (option) {
                        case QUICK_PULL -> text = Component.translatable("qualityofqueso.options.fov_effects.bows.quick_pull.tooltip");
                        case OFF -> text = Component.translatable("qualityofqueso.options.fov_effects.bows.off.tooltip");
                        default -> text = Component.translatable("qualityofqueso.options.fov_effects.bows.on.tooltip");
                    }
                    return Tooltip.create(Component.translatable("qualityofqueso.options.fov_effects.bows.tooltip").copy().append("\n\n").append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(Bows.values()), Bows.Codec),
                options().fovEffects.bows,
                value -> options().fovEffects.bows = value);
    }
    // end of fov effect options

    /* ===== */

    // HELPER METHODS
    /**
     * @return the bound key as a string.
     */
    private static String keyMappingAsString(KeyMapping keyMapping) {
        return parseKeyAsString(key(keyMapping).toString().toUpperCase());
    }

    /**
     * @return the key name (ex. B or F).
     */
    private static String parseKeyAsString(String translationKey) {
        return translationKey.substring(13).toUpperCase();
    }
}
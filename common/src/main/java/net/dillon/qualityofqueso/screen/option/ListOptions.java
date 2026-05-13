package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.eum.accessibility.MenuButton;
import net.dillon.qualityofqueso.option.eum.accessibility.Tooltips;
import net.dillon.qualityofqueso.option.eum.accessibility.WidgetTheme;
import net.dillon.qualityofqueso.option.eum.effects.Bows;
import net.dillon.qualityofqueso.option.eum.effects.PotionEffects;
import net.dillon.qualityofqueso.option.eum.hud.ArmorStatus;
import net.dillon.qualityofqueso.option.eum.hud.ItemCounter;
import net.dillon.qualityofqueso.option.eum.management.*;
import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.GlobalSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.Sorting;
import net.dillon.qualityofqueso.option.eum.misc.EChestButton;
import net.dillon.qualityofqueso.option.eum.misc.ElytraAlarm;
import net.dillon.qualityofqueso.option.eum.searching.QuickSearch;
import net.dillon.qualityofqueso.option.eum.searching.SearchBarColor;
import net.dillon.qualityofqueso.option.eum.searching.SearchBarPosition;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.keybind.ModKeybinds.getDropKey;
import static net.dillon.qualityofqueso.screen.option.ListOptionUtil.*;

/**
 * Options displayed in the Quality of Queso main menu.
 */
public class ListOptions {

    protected static OptionInstance<Boolean> containerSearching() {
        return createClientBooleanOption("container_searching", true, options().searching.containerSearching,
                (options, value) -> options.searching.containerSearching = value);
    }

    protected static OptionInstance<Boolean> inventorySearching() {
        return createClientBooleanOption("inventory_searching", true, options().searching.inventorySearching,
                (options, value) -> options.searching.inventorySearching = value);
    }

    protected static OptionInstance<Boolean> underlineText() {
        return createClientBooleanOption("underline_text", false, options().searching.underlineText,
                (options, value) -> options.searching.underlineText = value);
    }

    protected static OptionInstance<Boolean> saveSearchText() {
        return createClientBooleanOption("save_search_text", false, options().searching.saveSearchText,
                (options, value) -> options.searching.saveSearchText = value
        );
    }

    protected static OptionInstance<Boolean> singularMoving() {
        return createClientBooleanOption("singular_moving", true, options().management.singularMoving,
                (options, value) -> options.management.singularMoving = value
        );
    }

    protected static OptionInstance<Boolean> filtering() {
        return createClientBooleanOption("filtering", true, options().management.filtering,
                (options, value) -> options.management.filtering = value
        );
    }

    protected static OptionInstance<Boolean> dragSorting() {
        return createClientBooleanOption("drag_sorting", true, options().management.dragSorting,
                (options, value) -> options.management.dragSorting = value
        );
    }

    protected static OptionInstance<Boolean> dragMoving() {
        return createClientBooleanOption("drag_moving", true, options().management.dragMoving,
                (options, value) -> options.management.dragMoving = value
        );
    }

    protected static OptionInstance<Boolean> useGlobalSortingMode() {
        return createClientBooleanOption("use_global_sorting_mode", false, options().sorting.useGlobalSortingMode,
                (options, value) -> options.sorting.useGlobalSortingMode = value
        );
    }

    protected static OptionInstance<Boolean> lockSlots() {
        return createClientBooleanOption("lock_slots", true, options().lockedSlots.enableLockedSlots,
                (options, value) -> options.lockedSlots.enableLockedSlots = value
        );
    }

    protected static OptionInstance<Boolean> hardLockSlots() {
        return createClientBooleanOption("hard_lock_slots", false, options().lockedSlots.hardLockSlots,
                (options, value) -> options.lockedSlots.hardLockSlots = value
        );
    }

    protected static OptionInstance<Boolean> lockSound() {
        return createClientBooleanOption("lock_sound", true, options().lockedSlots.lockSound,
                (options, value) -> options.lockedSlots.lockSound = value
        );
    }

    protected static OptionInstance<Boolean> playSounds() {
        return createClientBooleanOption("play_sounds", false, options().management.playSounds,
                (options, value) -> options.management.playSounds = value
        );
    }

    protected static OptionInstance<Boolean> displaySearchTransportables() {
        return createClientBooleanOption("search_transportables", true, options().buttonDisplayOptions.displaySearchTransportables,
                (options, value) -> options.buttonDisplayOptions.displaySearchTransportables = value
        );
    }

    protected static OptionInstance<Boolean> displayAlwaysQuickMove() {
        return createClientBooleanOption("always_quick_move", true, options().buttonDisplayOptions.displayAlwaysQuickMove,
                (options, value) -> options.buttonDisplayOptions.displayAlwaysQuickMove = value
        );
    }

    protected static OptionInstance<Boolean> displayFillStacks() {
        return createClientBooleanOption("fill_stacks", true, options().buttonDisplayOptions.displayFillStacks,
                (options, value) -> options.buttonDisplayOptions.displayFillStacks = value
        );
    }

    protected static OptionInstance<Boolean> displayTradeAll() {
        return createClientBooleanOption("trade_all", true, options().buttonDisplayOptions.displayTradeAll,
                (options, value) -> options.buttonDisplayOptions.displayTradeAll = value
        );
    }

    protected static OptionInstance<Boolean> displayCraftAll() {
        return createClientBooleanOption("craft_all", true, options().buttonDisplayOptions.displayCraftAll,
                (options, value) -> options.buttonDisplayOptions.displayCraftAll = value
        );
    }

    protected static OptionInstance<Boolean> itemFrameSearching() {
        return createCommonBooleanOption("item_frame_searching", true, coptions().itemFrameSearching,
                (options, value) -> options.itemFrameSearching = value,
                keyMappingAsString(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI, false));
    }

    protected static OptionInstance<Boolean> coloredHighlighting() {
        return createClientBooleanOption("colored_highlighting", true, options().hud.coloredHighlighting,
                (options, value) -> options.hud.coloredHighlighting = value
        );
    }

    protected static OptionInstance<Boolean> warningIndicators() {
        return createClientBooleanOption("warning_indicators", true, options().hud.warningIndicators,
                (options, value) -> options.hud.warningIndicators = value
        );
    }

    protected static OptionInstance<Boolean> armorHotbar() {
        return createClientBooleanOption("armor_hotbar", true, options().hud.armorHotbar,
                (options, value) -> {
                    options.hud.armorHotbar = value;
                    resetArmorHudState();
                });
    }

    protected static OptionInstance<Boolean> emptySlots() {
        return createClientBooleanOption("empty_slots", true, options().hud.emptySlots,
                (options, value) -> {
                    options.hud.emptySlots = value;
                    resetArmorHudState();
                });
    }

    protected static OptionInstance<Boolean> highlightArmor() {
        return createClientBooleanOption("highlight_armor", false, options().hud.highlightArmor,
                (options, value) -> {
                    options.hud.highlightArmor = value;
                    resetArmorHudState();
                });
    }

    protected static OptionInstance<Boolean> animations() {
        return createClientBooleanOption("animations", true, options().hud.animations,
                (options, value) -> options.hud.animations = value
        );
    }

    protected static OptionInstance<Boolean> displayOnThrow() {
        return createClientBooleanOption("display_on_throw", false, options().itemCounter.displayOnThrow,
                (options, value) -> options.itemCounter.displayOnThrow = value
        );
    }

    protected static OptionInstance<Boolean> displayOnPickup() {
        return createClientBooleanOption("display_on_pickup", false, options().itemCounter.displayOnPickup,
                (options, value) -> options.itemCounter.displayOnPickup = value
        );
    }

    protected static OptionInstance<Boolean> countContainers() {
        return createClientBooleanOption("count_containers", false, options().itemCounter.countContainers,
                (options, value) -> options.itemCounter.countContainers = value
        );
    }

    protected static OptionInstance<Boolean> countEnderChest() {
        return createClientBooleanOption("count_ender_chest", false, options().itemCounter.countEnderChest,
                (options, value) -> options.itemCounter.countEnderChest = value
        );
    }

    protected static OptionInstance<Boolean> arrowCounter() {
        return createClientBooleanOption("arrow_counter", true, options().itemCounter.arrowCounter,
                (options, value) -> options.itemCounter.arrowCounter = value
        );
    }

    protected static OptionInstance<Boolean> countAllArrows() {
        return createClientBooleanOption("count_all_arrows", false, options().itemCounter.countAllArrows,
                (options, value) -> options.itemCounter.countAllArrows = value
        );
    }

    protected static OptionInstance<Boolean> onlyShowArrowCounter() {
        return createClientBooleanOption("only_show_arrow_counter", false, options().itemCounter.onlyShowArrowCounter,
                (options, value) -> options.itemCounter.onlyShowArrowCounter = value
        );
    }

    protected static OptionInstance<Boolean> alwaysShowArrowCounter() {
        return createClientBooleanOption("always_show_arrow_counter", false, options().itemCounter.alwaysShowArrowCounter,
                (options, value) -> options.itemCounter.alwaysShowArrowCounter = value
        );
    }

    protected static OptionInstance<Boolean> onlyCountMatchingItems() {
        return createClientBooleanOption("only_count_matching_items", false, options().itemCounter.onlyCountMatchingItems,
                (options, value) -> options.itemCounter.onlyCountMatchingItems = value
        );
    }

    protected static OptionInstance<Boolean> displayTotalWithStacks() {
        return createClientBooleanOption("display_total_with_stacks", false, options().itemCounter.displayTotalWithStacks,
                (options, value) -> options.itemCounter.displayTotalWithStacks = value
        );
    }

    protected static OptionInstance<Boolean> mobHitDing() {
        return createClientBooleanOption("mob_hit_ding", true, options().misc.mobHitDing,
                (options, value) -> options.misc.mobHitDing = value
        );
    }

    protected static OptionInstance<Boolean> quickEquip() {
        return createClientBooleanOption("quick_equip", true, options().misc.quickEquip,
                (options, value) -> options.misc.quickEquip = value,
                keyMappingAsString(ModKeybinds.QUICK_EQUIP, false)
        );
    }

    protected static OptionInstance<Boolean> enchantmentHelper() {
        return createClientBooleanOption("enchantment_helper", true, options().misc.enchantmentHelper,
                (options, value) -> options.misc.enchantmentHelper = value
        );
    }

    protected static OptionInstance<Boolean> quickGuiExit() {
        return createClientBooleanOption("quick_gui_exit", true, options().misc.quickGuiExit,
                (options, value) -> options.misc.quickGuiExit = value
        );
    }

    protected static OptionInstance<Boolean> armorDing() {
        return createClientBooleanOption("armor_ding", true, options().misc.armorDing,
                (options, value) -> options.misc.armorDing = value
        );
    }

    protected static OptionInstance<Boolean> allFog() {
        return createClientBooleanOption("all_fog", true, options().fog.allFog,
                (options, value) ->  options.fog.allFog = value
        );
    }

    protected static OptionInstance<Boolean> overworldFog() {
        return createClientBooleanOption("overworld_fog", true, options().fog.overworldFog,
                (options, value) -> options.fog.overworldFog = value
        );
    }

    protected static OptionInstance<Boolean> netherFog() {
        return createClientBooleanOption("nether_fog", true, options().fog.netherFog,
                (options, value) -> options.fog.netherFog = value
        );
    }

    protected static OptionInstance<Boolean> overrideClientTime() {
        return createClientBooleanOption("override_client_time", true, options().visualTime.overrideClientTime,
                (options, value) -> options.visualTime.overrideClientTime = value
        );
    }

    protected static OptionInstance<Boolean> matchWithIRLTIme() {
        return createClientBooleanOption("match_with_irl_time", false, options().visualTime.matchWithIrlTime,
                (options, value) -> options.visualTime.matchWithIrlTime = value
        );
    }

    protected static OptionInstance<Boolean> preventRageQuitting() {
        return createClientBooleanOption("prevent_rage_quitting", false, options().misc.preventRageQuitting,
                (options, value) -> options.misc.preventRageQuitting = value
        );
    }

    protected static OptionInstance<Boolean> alwaysPreventRageQuitting() {
        return createClientBooleanOption("always_prevent_rage_quitting", false, options().misc.alwaysPreventRageQuitting,
                (options, value) -> options.misc.alwaysPreventRageQuitting = value
        );
    }

    protected static OptionInstance<Boolean> redArmorTint() {
        return createClientBooleanOption("red_armor_tint", true, options().misc.redArmorTint,
                (options, value) -> options.misc.redArmorTint = value
        );
    }

    protected static OptionInstance<Boolean> fortniteBattlePass() {
        return createClientBooleanOption("fortnite_battle_pass", true, options().misc.fortniteBattlePass,
                (options, value) -> options.misc.fortniteBattlePass = value
        );
    }

    public static OptionInstance<Boolean> enableMod() {
        return createClientBooleanOption("enable_mod", false, options().accessibility.enableMod,
                (options, value) -> options.accessibility.enableMod = value
        );
    }

    protected static OptionInstance<Boolean> preventEFromTyping() {
        return createClientBooleanOption("prevent_e_from_typing", false, options().accessibility.preventEFromTyping,
                (options, value) -> options.accessibility.preventEFromTyping = value
        );
    }

    protected static OptionInstance<Boolean> searchInventory() {
        return createClientBooleanOption("search_inventory", false, options().accessibility.searchInventory,
                (options, value) -> options.accessibility.searchInventory = value);
    }

    protected static OptionInstance<Boolean> showLock() {
        return createClientBooleanOption("show_lock", false, options().lockedSlots.showLock,
                (options, value) -> options.lockedSlots.showLock = value
        );
    }

    protected static OptionInstance<Boolean> autoCloseRecipeBook() {
        return createClientBooleanOption("auto_close_recipe_book", true, options().accessibility.autoCloseRecipeBook,
                (options, value) -> options.accessibility.autoCloseRecipeBook = value
        );
    }

    protected static OptionInstance<Boolean> ignoreFabricTags() {
        return createClientBooleanOption("ignore_fabric_tags", true, options().accessibility.ignoreFabricTags,
                (options, value) -> options.accessibility.ignoreFabricTags = value
        );
    }

    protected static OptionInstance<Boolean> darkerOverlay() {
        return createClientBooleanOption("darker_overlay", true, options().accessibility.darkerOverlay,
                (options, value) -> options.accessibility.darkerOverlay = value
        );
    }

    protected static OptionInstance<Boolean> darkDisc() {
        return createClientBooleanOption("dark_disc", true, options().accessibility.darkDisc,
                (options, value) -> options.accessibility.darkDisc = value
        );
    }

    protected static OptionInstance<Boolean> perpendicularQuickMoving() {
        return createClientBooleanOption("perpendicular_quick_moving", true, options().accessibility.perpendicularQuickMoving,
                (options, value) -> options.accessibility.perpendicularQuickMoving = value
        );
    }

    protected static OptionInstance<Boolean> flying() {
        return createClientBooleanOption("fov_effects.flying", true, options().fovEffects.flying,
                (options, value) -> options.fovEffects.flying = value
        );
    }

    protected static OptionInstance<Boolean> fluids() {
        return createClientBooleanOption("fov_effects.fluids", true, options().fovEffects.fluids,
                (options, value) -> options.fovEffects.fluids = value);
    }

    protected static OptionInstance<Boolean> inGameTime() {
        return createClientBooleanOption("in_game_time", true, options().debugHuds.inGameTime,
                (options, value) -> options.debugHuds.inGameTime = value);
    }

    protected static OptionInstance<Boolean> realLifeTime() {
        return createClientBooleanOption("real_life_time", true, options().debugHuds.realLifeTime,
                (options, value) -> options.debugHuds.realLifeTime = value);
    }

    protected static OptionInstance<Boolean> date() {
        return createClientBooleanOption("date", true, options().debugHuds.date,
                (options, value) -> options.debugHuds.date = value);
    }

    protected static OptionInstance<Boolean> simpleCoordinates() {
        return createClientBooleanOption("simple_coordinates", true, options().debugHuds.simpleCoordinates,
                (options, value) -> options.debugHuds.simpleCoordinates = value);
    }

    protected static OptionInstance<Boolean> accurateFacing() {
        return createClientBooleanOption("accurate_facing", true, options().debugHuds.accurateFacing,
                (options, value) -> options.debugHuds.accurateFacing = value);
    }

    protected static OptionInstance<Boolean> fastestFlight() {
        return createClientBooleanOption("fastest_flight", true, options().debugHuds.fastestFlight,
                (options, value) -> options.debugHuds.fastestFlight = value);
    }

    protected static OptionInstance<Boolean> multiServerConfigs() {
        return createUniversalBooleanOption(
                "multi_server_configs",
                true,
                uoptions().main.multiServerConfigs,
                (options, value) -> {
                    options.main.multiServerConfigs = value;
                    ModHelper.saveAndApplyConfigs(Minecraft.getInstance());
                }
        );
    }

    protected static OptionInstance<Double> displayTime() {
        return createDoubleOption("display_time",
                " second(s)",
                2.0,
                8.0,
                10.0,
                options().hud.displayTime,
                ModClientOptions.INSTANCE,
                (options, value) -> options.hud.displayTime = value
        );
    }

    protected static OptionInstance<Double> animationTime() {
        return createDoubleOption("animation_time",
                " second(s)",
                0.25,
                3.0,
                100.0,
                options().hud.animationTime,
                ModClientOptions.INSTANCE,
                (options, value) -> options.hud.animationTime = value
        );
    }

    protected static OptionInstance<Integer> sprinting() {
        return createIntegerOption("fov_effects.sprinting",
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(
                        value < 100
                                ? "§7OFF"
                                : value + "%")),
                99,
                200,
                options().fovEffects.sprinting,
                (options, value) -> options.fovEffects.sprinting = value
        );
    }

    protected static OptionInstance<Integer> minElytraFallDistance() {
        return createIntegerOption("min_elytra_fall_distance",
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + " blocks")),
                10,
                100,
                options().elytraAlarm.minFallDistance,
                (options, value) -> options.elytraAlarm.minFallDistance = value
        );
    }

    protected static OptionInstance<Integer> minMobHitDingDistance() {
        return createIntegerOption(
                "min_mob_hit_ding_distance",
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + " blocks")),
                10,
                50,
                options().misc.minMobHitDingDistance,
                (options, value) -> options.misc.minMobHitDingDistance = value
        );
    }

    protected static OptionInstance<Integer> overworldFogIntensity() {
        return createIntegerOption(
                "overworld_fog_intensity",
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + "%")),
                25,
                150,
                options().fog.overworldFogIntensity,
                (options, value) -> options.fog.overworldFogIntensity = value
        );
    }

    protected static OptionInstance<Integer> netherFogIntensity() {
        return createIntegerOption(
                "nether_fog_intensity",
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + "%")),
                5,
                100,
                options().fog.netherFogIntensity,
                (options, value) -> options.fog.netherFogIntensity = value
        );
    }

    public static OptionInstance<Integer> visualTime() {
        return new OptionInstance<>("qualityofqueso.options.visual_time",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.visual_time.tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(formatMinecraftTime(value * 5))),
                new OptionInstance.IntRange(0, 287), options().visualTime.visualTime, value -> options().visualTime.visualTime = value);
    }

    protected static OptionInstance<Integer> visualTimeSpeed() {
        return createIntegerOption(
                "visual_time_speed",
                (optionText, value) -> {
                    if (value == 0) {
                        return Options.genericValueLabel(optionText, Component.literal("Freeze"));
                    } else {
                        return Options.genericValueLabel(optionText, Component.literal(value + "%"));
                    }
                },
                0,
                100,
                options().visualTime.visualTimeSpeed,
                (options, value) -> options.visualTime.visualTimeSpeed = value
        );
    }

    protected static OptionInstance<Integer> elytraAlarmSoundDelay() {
        return createIntegerOption(
                "elytra_alarm_sound_delay",
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + " tick(s)")),
                1,
                120,
                options().elytraAlarm.soundDelayTicks,
                (options, value) -> options.elytraAlarm.soundDelayTicks = value
        );
    }

    public static OptionInstance<Integer> itemFrameSearchGlowDuration() {
        return createIntegerOption(
                "item_frame_search_glow_duration",
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
                0,
                180,
                options().misc.itemFrameSearchGlowDuration,
                (options, value) -> options.misc.itemFrameSearchGlowDuration = value
        );
    }

    public static OptionInstance<Integer> itemFrameSearchRadius() {
        return new OptionInstance<>("qualityofqueso.options.item_frame_search_radius",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.item_frame_search_radius.tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + " blocks")),
                new OptionInstance.IntRange(10, 256).xmap(value -> value * 2, value -> value / 2), options().misc.itemFrameSearchRadius, value -> options().misc.itemFrameSearchRadius = value);
    }

    protected static OptionInstance<QuickSearch> quickSearch() {
        return createEnumOption(
                "quick_search",
                option -> switch (option) {
                    case RECIPE_BOOK -> Tooltip.create(Component.translatable("qualityofqueso.options.quick_search.recipe_book.tooltip"));
                    case SEARCH_BAR -> Tooltip.create(Component.translatable("qualityofqueso.options.quick_search.search_bar.tooltip"));
                    case OFF -> Tooltip.create(Component.translatable("qualityofqueso.options.quick_search.off.tooltip"));
                    default -> Tooltip.create(Component.translatable("qualityofqueso.options.quick_search.tooltip"));
                },
                QuickSearch.values(),
                QuickSearch.CODEC,
                options().searching.quickSearch,
                (options, value) -> options.searching.quickSearch = value
        );
    }

    protected static OptionInstance<SearchBarColor> searchBarColor() {
        return createEnumOption(
                "search_bar_color",
                option -> switch (option) {
                    case DEFAULT -> Tooltip.create(Component.translatable("qualityofqueso.options.search_bar_color.default.tooltip"));
                    case BLACK -> Tooltip.create(Component.translatable("qualityofqueso.options.search_bar_color.black.tooltip"));
                },
                SearchBarColor.values(),
                SearchBarColor.CODEC,
                options().searching.searchBarColor,
                (options, value) -> options.searching.searchBarColor = value
        );
    }

    protected static OptionInstance<SearchBarPosition> searchBarPosition() {
        return createEnumOption(
                "search_bar_position",
                option -> switch (option) {
                    case OVERLAY -> Tooltip.create(Component.translatable("qualityofqueso.options.search_bar_position.overlay.tooltip"));
                    case TOP -> Tooltip.create(Component.translatable("qualityofqueso.options.search_bar_position.top.tooltip"));
                },
                SearchBarPosition.values(),
                SearchBarPosition.CODEC,
                options().searching.searchBarPosition,
                (options, value) -> options.searching.searchBarPosition = value
        );
    }

    protected static OptionInstance<Layout> layout() {
        return createEnumOption(
                "layout",
                option -> switch (option) {
                    case VERTICAL -> Tooltip.create(Component.translatable("qualityofqueso.options.layout.vertical.tooltip"));
                    case HORIZONTAL -> Tooltip.create(Component.translatable("qualityofqueso.options.layout.horizontal.tooltip"));
                },
                Layout.values(),
                Layout.CODEC,
                options().management.layout,
                (options, value) -> options.management.layout = value
        );
    }

    protected static OptionInstance<Transferring> transferring() {
        return createEnumOption(
                "transferring",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case BUTTON_OR_KEY -> text = Component.translatable("qualityofqueso.options.transferring.button_or_key.tooltip");
                        case KEY_ONLY -> text = Component.translatable("qualityofqueso.options.transferring.key_only.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.create(Component.translatable("qualityofqueso.options.transferring.tooltip").append(appended).append(text));
                },
                Transferring.values(),
                Transferring.CODEC,
                options().management.transferring,
                (options, value) -> options.management.transferring = value
        );
    }

    protected static OptionInstance<Sorting> sorting() {
        return createEnumOption(
                "sorting",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case BUTTON_OR_KEY -> text = Component.translatable("qualityofqueso.options.sorting.button_or_key.tooltip");
                        case KEY_ONLY -> text = Component.translatable("qualityofqueso.options.sorting.key_only.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.create(Component.translatable("qualityofqueso.options.sorting.tooltip").append(appended).append(text));
                },
                Sorting.values(),
                Sorting.CODEC,
                options().sorting.sortingEnabled,
                (options, value) -> options.sorting.sortingEnabled = value
        );
    }

    protected static OptionInstance<GlobalSortingMode> globalSortingMode() {
        return createEnumOption(
                "global_sorting_mode",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case ALPHABETICALLY -> text = Component.translatable("qualityofqueso.options.global_sorting_mode.alphabetically.tooltip");
                        case BY_TAG -> text = Component.translatable("qualityofqueso.options.global_sorting_mode.by_tag.tooltip");
                        case DESCENDING -> text = Component.translatable("qualityofqueso.options.global_sorting_mode.descending.tooltip");
                        case ASCENDING -> text = Component.translatable("qualityofqueso.options.global_sorting_mode.ascending.tooltip");
                        case CREATIVE_MENU -> text = Component.translatable("qualityofqueso.options.global_sorting_mode.creative_menu.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.create(Component.translatable("qualityofqueso.options.global_sorting_mode.tooltip").append(appended).append(text));
                },
                GlobalSortingMode.values(),
                GlobalSortingMode.CODEC,
                options().sorting.globalSortingMode,
                (options, value) -> {
                    if (value.equals(GlobalSortingMode.ALPHABETICALLY)) {
                        options.sorting.currentSortingMode = CurrentSortingMode.ALPHABETICAL;
                    } else if (value.equals(GlobalSortingMode.BY_TAG)) {
                        options.sorting.currentSortingMode = CurrentSortingMode.TAG;
                    } else if (value.equals(GlobalSortingMode.DESCENDING)) {
                        options.sorting.currentSortingMode = CurrentSortingMode.COUNT_DESCENDING;
                    } else if (value.equals(GlobalSortingMode.ASCENDING)) {
                        options.sorting.currentSortingMode = CurrentSortingMode.COUNT_ASCENDING;
                    } else if (value.equals(GlobalSortingMode.CREATIVE_MENU)) {
                        options.sorting.currentSortingMode = CurrentSortingMode.CREATIVE_MENU;
                    }
                    options.sorting.globalSortingMode = value;
                }
        );
    }

    protected static OptionInstance<QuickDrop> quickDrop() {
        return createEnumOption(
                "quick_drop",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case BUTTON_OR_KEY -> text = Component.translatable("qualityofqueso.options.quick_drop.button_or_key.tooltip");
                        case KEY_ONLY -> text = Component.translatable("qualityofqueso.options.quick_drop.key_only.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.create(
                            Component.translatable("qualityofqueso.options.quick_drop.tooltip",
                                    keyMappingAsString(getDropKey(), false)
                            ).append(appended).append(text));
                },
                QuickDrop.values(),
                QuickDrop.CODEC,
                options().management.quickDrop,
                (options, value) -> options.management.quickDrop = value
        );
    }

    protected static OptionInstance<Swapping> swapping() {
        return createEnumOption(
                "swapping",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case BUTTON_OR_KEY -> text = Component.translatable("qualityofqueso.options.swapping.button_or_key.tooltip");
                        case KEY_ONLY -> text = Component.translatable("qualityofqueso.options.swapping.key_only.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.create(Component.translatable("qualityofqueso.options.swapping.tooltip").append(appended).append(text));
                },
                Swapping.values(),
                Swapping.CODEC,
                options().management.swapping,
                (options, value) -> options.management.swapping = value
        );
    }

    protected static OptionInstance<IncludeHotbar> displayIncludeHotbar() {
        return createEnumOption(
                "include_hotbar",
                option -> switch (option) {
                    case ALWAYS -> Tooltip.create(Component.translatable("qualityofqueso.options.include_hotbar.always.tooltip"));
                    case CONTAINER_SCREENS_ONLY -> Tooltip.create(Component.translatable("qualityofqueso.options.include_hotbar.container_screens_only.tooltip"));
                    case OFF -> Tooltip.create(Component.translatable("qualityofqueso.options.include_hotbar.off.tooltip"));
                },
                IncludeHotbar.values(),
                IncludeHotbar.CODEC,
                options().buttonDisplayOptions.displayIncludeHotbar,
                (options, value) -> options.buttonDisplayOptions.displayIncludeHotbar = value
        );
    }

    protected static OptionInstance<MoveMatchingItems> displayMoveMatchingItems() {
        return createEnumOption(
                "move_matching_items",
                option -> switch (option) {
                    case ALWAYS -> Tooltip.create(Component.translatable("qualityofqueso.options.move_matching_items.always.tooltip"));
                    case FILTERED_CONTAINERS_ONLY -> Tooltip.create(Component.translatable("qualityofqueso.options.move_matching_items.filtered_containers_only.tooltip"));
                },
                MoveMatchingItems.values(),
                MoveMatchingItems.CODEC,
                options().buttonDisplayOptions.displayMoveMatchingItems,
                (options, value) -> options.buttonDisplayOptions.displayMoveMatchingItems = value
        );
    }

    protected static OptionInstance<ArmorStatus> armorStatus() {
        return createEnumOption(
                "armor_status",
                option -> switch (option) {
                    case OFF -> Tooltip.create(Component.translatable("qualityofqueso.options.armor_status.off.tooltip"));
                    case ON_UPDATE -> Tooltip.create(Component.translatable("qualityofqueso.options.armor_status.on_update.tooltip"));
                    default -> Tooltip.create(Component.translatable("qualityofqueso.options.armor_status.tooltip"));
                },
                ArmorStatus.values(),
                ArmorStatus.CODEC,
                options().hud.armorStatus,
                (options, value) -> {
                    options.hud.armorStatus = value;
                    resetArmorHudState();
                }
        );
    }

    protected static OptionInstance<ItemCounter> itemCounter() {
        return createEnumOption(
                "item_counter",
                option -> {
                    Component text;
                    switch (option) {
                        case TOTAL -> text = Component.translatable("qualityofqueso.options.item_counter.total.tooltip");
                        case STACKS -> text = Component.translatable("qualityofqueso.options.item_counter.stacks.tooltip");
                        default -> text = Component.translatable("qualityofqueso.options.item_counter.off.tooltip");
                    }
                    return Tooltip.create(Component.translatable("qualityofqueso.options.item_counter.tooltip").append("\n\n").append(text));
                },
                ItemCounter.values(),
                ItemCounter.CODEC,
                options().itemCounter.enableItemCounter,
                (options, value) -> options.itemCounter.enableItemCounter = value
        );
    }

    protected static OptionInstance<ElytraAlarm> elytraAlarm() {
        return createEnumOption(
                "elytra_alarm",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case INDICATOR_ONLY -> text = Component.translatable("qualityofqueso.options.elytra_alarm.indicator_only.tooltip");
                        case OFF -> text = Component.translatable("qualityofqueso.options.elytra_alarm.off.tooltip");
                    }
                    return Tooltip.create(Component.translatable("qualityofqueso.options.elytra_alarm.tooltip").append(text));
                },
                ElytraAlarm.values(),
                ElytraAlarm.CODEC,
                options().elytraAlarm.enableElytraAlarm,
                (options, value) -> options.elytraAlarm.enableElytraAlarm = value
        );
    }

    protected static OptionInstance<Tooltips> tooltips() {
        return createEnumOption(
                "tooltips",
                option -> switch (option) {
                    case DEFAULT -> Tooltip.create(Component.translatable("qualityofqueso.options.tooltips.default.tooltip"));
                    case OVERLAY -> Tooltip.create(Component.translatable("qualityofqueso.options.tooltips.overlay.tooltip"));
                    case OFF -> Tooltip.create(Component.translatable("qualityofqueso.options.tooltips.off.tooltip"));
                },
                Tooltips.values(),
                Tooltips.CODEC,
                options().accessibility.tooltips,
                (options, value) -> options.accessibility.tooltips = value
        );
    }

    protected static OptionInstance<MenuButton> menuButton() {
        return createEnumOption(
                "menu_button",
                option -> switch (option) {
                    case EVERYWHERE -> Tooltip.create(Component.translatable("qualityofqueso.options.menu_button.everywhere.tooltip"));
                    case BOTTOM_LEFT -> Tooltip.create(Component.translatable("qualityofqueso.options.menu_button.bottom_left.tooltip"));
                    case BOTTOM_RIGHT -> Tooltip.create(Component.translatable("qualityofqueso.options.menu_button.bottom_right.tooltip"));
                    case TITLE_ONLY -> Tooltip.create(Component.translatable("qualityofqueso.options.menu_button.title_only.tooltip"));
                    case OFF -> Tooltip.create(Component.translatable("qualityofqueso.options.menu_button.off.tooltip"));
                },
                MenuButton.values(),
                MenuButton.CODEC,
                uoptions().main.menuButton,
                (options, value) -> uoptions().main.menuButton = value
        );
    }

    protected static OptionInstance<EChestButton> eChestButton() {
        return createEnumOption(
                "echest_button",
                option -> switch (option) {
                    case QOQ_MENU -> Tooltip.create(Component.translatable("qualityofqueso.options.echest_button.qoq_menu.tooltip"));
                    case PAUSE_SCREEN -> Tooltip.create(Component.translatable("qualityofqueso.options.echest_button.pause_screen.tooltip"));
                    case OFF -> Tooltip.create(Component.translatable("qualityofqueso.options.echest_button.off.tooltip"));
                },
                EChestButton.values(),
                EChestButton.CODEC,
                options().accessibility.eChestButton,
                (options, value) -> options.accessibility.eChestButton = value
        );
    }

    protected static OptionInstance<WidgetTheme> widgetTheme() {
        return createEnumOption(
                "widget_theme",
                option -> switch (option) {
                    case VANILLA -> Tooltip.create(Component.translatable("qualityofqueso.options.widget_theme.vanilla.tooltip"));
                    case DARK -> Tooltip.create(Component.translatable("qualityofqueso.options.widget_theme.dark.tooltip"));
                    case TRANSPARENT -> Tooltip.create(Component.translatable("qualityofqueso.options.widget_theme.transparent.tooltip"));
                },
                WidgetTheme.values(),
                WidgetTheme.CODEC,
                options().accessibility.widgetTheme,
                (options, value) -> options.accessibility.widgetTheme = value
        );
    }

    protected static OptionInstance<PotionEffects> potionEffects() {
        return createEnumOption(
                "fov_effects.potion_effects",
                option -> switch (option) {
                    case NON_BEACON -> Tooltip.create(Component.translatable("qualityofqueso.options.fov_effects.potion_effects.non_beacon.tooltip"));
                    case OFF -> Tooltip.create(Component.translatable("qualityofqueso.options.fov_effects.potion_effects.off.tooltip"));
                    default -> Tooltip.create(Component.translatable("qualityofqueso.options.fov_effects.potion_effects.default.tooltip"));
                },
                PotionEffects.values(),
                PotionEffects.CODEC,
                options().fovEffects.potionEffects,
                (options, value) -> options.fovEffects.potionEffects = value
        );
    }

    protected static OptionInstance<Bows> bows() {
        return createEnumOption(
                "fov_effects.bows",
                option -> switch (option) {
                    case QUICK_PULL -> Tooltip.create(Component.translatable("qualityofqueso.options.fov_effects.bows.quick_pull.tooltip"));
                    case OFF -> Tooltip.create(Component.translatable("qualityofqueso.options.fov_effects.bows.off.tooltip"));
                    default -> Tooltip.create(Component.translatable("qualityofqueso.options.fov_effects.bows.default.tooltip"));
                },
                Bows.values(),
                Bows.CODEC,
                options().fovEffects.bows,
                (options, value) -> options.fovEffects.bows = value
        );
    }
}
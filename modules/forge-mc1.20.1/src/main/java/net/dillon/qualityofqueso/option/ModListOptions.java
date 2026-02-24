package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

import static net.dillon.qualityofqueso.main.QoQ.*;

/**
 * Options displayed on ModOptionsScreen.
 */
@OnlyIn(Dist.CLIENT)
public class ModListOptions {
    public static final OptionInstance.CaptionBasedToString<Boolean> ON_OFF_TEXT = (p_231544_, p_231545_) -> p_231545_
            ? ModTexts.ON
            : ModTexts.OFF;
    public static final OptionInstance.CaptionBasedToString<Boolean> YES_NO_TEXT = (p_231544_, p_231545_) -> p_231545_
            ? ModTexts.YES
            : ModTexts.NO;

    public static OptionInstance<Boolean> enableMod() {
        return OptionInstance.createBoolean("qualityofqueso.options.enable_mod", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.enable_mod.tooltip")),
                YES_NO_TEXT, options().enableMod, value -> options().enableMod = value);
    }

    public static OptionInstance<Boolean> quickGuiExit() {
        return OptionInstance.createBoolean("qualityofqueso.options.quick_gui_exit", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.quick_gui_exit.tooltip")),
                ON_OFF_TEXT, options().quickGuiExit, value -> options().quickGuiExit = value);
    }

    public static OptionInstance<Boolean> quickSearch() {
        return OptionInstance.createBoolean("qualityofqueso.options.quick_search", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.quick_search.tooltip")),
                ON_OFF_TEXT, options().quickSearch, value -> options().quickSearch = value);
    }

    public static OptionInstance<Boolean> chestSearching() {
        return OptionInstance.createBoolean("qualityofqueso.options.chest_searching", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.chest_searching.tooltip")),
                ON_OFF_TEXT, options().chestSearching, value -> options().chestSearching = value);
    }

    public static OptionInstance<Boolean> searchInventory() {
        return OptionInstance.createBoolean("qualityofqueso.options.search_inventory", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.search_inventory.tooltip")),
                YES_NO_TEXT, options().searchInventory, value -> options().searchInventory = value);
    }

    public static OptionInstance<Boolean> inventorySearching() {
        return OptionInstance.createBoolean("qualityofqueso.options.inventory_searching", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.inventory_searching.tooltip")),
                ON_OFF_TEXT, options().inventorySearching, value -> options().inventorySearching = value);
    }

    public static OptionInstance<Boolean> saveSearchText() {
        return OptionInstance.createBoolean("qualityofqueso.options.save_search_text", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.save_search_text.tooltip")),
                YES_NO_TEXT, options().saveSearchText, value -> options().saveSearchText = value);
    }

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
                    return Tooltip.create(Component.translatable("qualityofqueso.options.transferring.tooltip", keyBindingAsString(ModKeybinds.MOVE_INVENTORY), keyBindingAsString(ModKeybinds.MOVE_CONTAINER)).append(appended).append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(Transferring.values()), Transferring.Codec),
                options().transferring,
                value -> options().transferring = value);
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
                    return Tooltip.create(Component.translatable("qualityofqueso.options.container_sorting.tooltip", keyBindingAsString(ModKeybinds.SORT_CONTAINER)).append(appended).append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(ContainerSorting.values()), ContainerSorting.Codec),
                options().containerSorting,
                value -> options().containerSorting = value);
    }

    public static OptionInstance<QuickDrop> quickDrop() {
        return new OptionInstance<>(
                "qualityofqueso.options.quick_drop",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case SHORTCUT_KEY_OR_BUTTON -> text = Component.translatable("qualityofqueso.options.quick_drop.shortcut_key_or_button.tooltip");
                        case SHORTCUT_KEY_ONLY -> text = Component.translatable("qualityofqueso.options.quick_drop.shortcut_key_only.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.create(Component.translatable("qualityofqueso.options.quick_drop.tooltip").append(appended).append(text));
                },
                (optionText, value) -> value.getCaption(),
                new OptionInstance.Enum<>(Arrays.asList(QuickDrop.values()), QuickDrop.Codec),
                options().quickDrop,
                value -> options().quickDrop = value);
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
                    return Tooltip.create(Component.translatable("qualityofqueso.options.swapping.tooltip", keyBindingAsString(ModKeybinds.SWAP_ITEMS)).append(appended).append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(Swapping.values()), Swapping.Codec),
                options().swapping,
                value -> options().swapping = value);
    }

    public static OptionInstance<MoveItemsIf> moveItemsIf() {
        return new OptionInstance<>(
                "qualityofqueso.options.move_items_if",
                option -> {
                    Component text;
                    switch (option) {
                        case CONTAINER_ISNT_FILLED -> text = Component.translatable("qualityofqueso.options.move_items_if.container_isnt_filled.tooltip");
                        case LESS_THAN_MAX_STACK_SIZE -> text = Component.translatable("qualityofqueso.options.move_items_if.less_than_max_item_count.tooltip");
                        default -> text = Component.translatable("qualityofqueso.options.move_items_if.can_move_at_all.tooltip");
                    }
                    return Tooltip.create(Component.translatable("qualityofqueso.options.move_items_if.tooltip").append("\n\n").append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(MoveItemsIf.values()), MoveItemsIf.Codec),
                options().moveItemsIf,
                value -> options().moveItemsIf = value);
    }

    public static OptionInstance<Boolean> includeHotbar() {
        return OptionInstance.createBoolean("qualityofqueso.options.include_hotbar", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.include_hotbar.tooltip")),
                YES_NO_TEXT, options().includeHotbar, value -> options().includeHotbar = value);
    }

    public static OptionInstance<Boolean> perpendicularQuickMoving() {
        return OptionInstance.createBoolean("qualityofqueso.options.perpendicular_quick_moving", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.perpendicular_quick_moving.tooltip")),
                ON_OFF_TEXT, options().perpendicularQuickMoving, value -> options().perpendicularQuickMoving = value);
    }

    public static OptionInstance<Boolean> dragSorting() {
        return OptionInstance.createBoolean("qualityofqueso.options.drag_sorting", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.drag_sorting.tooltip")),
                ON_OFF_TEXT, options().dragSorting, value -> options().dragSorting = value);
    }

    public static OptionInstance<Boolean> saveExcludedSlots() {
        return OptionInstance.createBoolean("qualityofqueso.options.save_excluded_slots", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.save_excluded_slots.tooltip")),
                ON_OFF_TEXT, options().saveExcludedSlots, value -> options().saveExcludedSlots = value);
    }

    public static OptionInstance<ArmorStatus> armorStatus() {
        return new OptionInstance<>(
                "qualityofqueso.options.armor_status",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case ALWAYS -> text = Component.translatable("qualityofqueso.options.armor_status.always.tooltip");
                        case ON_CHANGE -> text = Component.translatable("qualityofqueso.options.armor_status.on_change.tooltip");
                    }
                    return Tooltip.create(Component.translatable("qualityofqueso.options.armor_status.tooltip").copy().append(text != ModTexts.BLANK ? "\n\n" : "").append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(ArmorStatus.values()), ArmorStatus.Codec),
                options().armorStatus,
                value -> options().armorStatus = value);
    }

    public static OptionInstance<ItemCount> itemCount() {
        return new OptionInstance<>(
                "qualityofqueso.options.item_count",
                option -> {
                    Component text = ModTexts.BLANK;
                    switch (option) {
                        case TOTAL -> text = Component.translatable("qualityofqueso.options.item_count.total.tooltip");
                        case STACKS -> text = Component.translatable("qualityofqueso.options.item_count.stacks.tooltip");
                        case REMAINDER -> text = Component.translatable("qualityofqueso.options.item_count.remainder.tooltip");
                    }
                    return Tooltip.create(Component.translatable("qualityofqueso.options.item_count.tooltip").append(text != ModTexts.BLANK ? "\n\n" : "").append(text));
                },
                (optionText, value) -> value.getText(),
                new OptionInstance.Enum<>(Arrays.asList(ItemCount.values()), ItemCount.Codec),
                options().itemCount,
                value -> options().itemCount = value);
    }

    public static OptionInstance<Boolean> displayOnThrow() {
        return OptionInstance.createBoolean("qualityofqueso.options.display_on_throw", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.display_on_throw.tooltip")),
                YES_NO_TEXT, options().displayOnThrow, value -> options().displayOnThrow = value);
    }

    public static OptionInstance<Boolean> displayOnPickup() {
        return OptionInstance.createBoolean("qualityofqueso.options.display_on_pickup", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.display_on_pickup.tooltip")),
                YES_NO_TEXT, options().displayOnPickup, value -> options().displayOnPickup = value);
    }

    public static OptionInstance<Boolean> countContainers() {
        return OptionInstance.createBoolean("qualityofqueso.options.count_containers", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.count_containers.tooltip")),
                YES_NO_TEXT, options().countContainers, value -> options().countContainers = value);
    }

    public static OptionInstance<Boolean> displayTotalWithStacks() {
        return OptionInstance.createBoolean("qualityofqueso.options.display_total_with_stacks", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.display_total_with_stacks.tooltip")),
                YES_NO_TEXT, options().displayTotalWithStacks, value -> options().displayTotalWithStacks = value);
    }

    public static OptionInstance<Boolean> showButtonShortcuts() {
        return OptionInstance.createBoolean("qualityofqueso.options.show_button_shortcuts", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.show_button_shortcuts.tooltip")),
                YES_NO_TEXT, options().showButtonShortcuts, value -> options().showButtonShortcuts = value);
    }

    public static OptionInstance<Boolean> quickEquip() {
        return OptionInstance.createBoolean("qualityofqueso.options.quick_equip", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.quick_equip.tooltip",
                        keyBindingAsString(ModKeybinds.QUICK_EQUIP))),
                ON_OFF_TEXT, options().quickEquip, value -> options().quickEquip = value);
    }

    public static OptionInstance<Boolean> preventRageQuitting() {
        return OptionInstance.createBoolean("qualityofqueso.options.prevent_rage_quitting", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.prevent_rage_quitting.tooltip")),
                YES_NO_TEXT, options().preventRageQuitting, value -> options().preventRageQuitting = value);
    }

    public static OptionInstance<Boolean> alwaysPreventRageQuitting() {
        return OptionInstance.createBoolean("qualityofqueso.options.always_prevent_rage_quitting", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.always_prevent_rage_quitting.tooltip")),
                YES_NO_TEXT, options().alwaysPreventRageQuitting, value -> options().alwaysPreventRageQuitting = value);
    }

    public static OptionInstance<Boolean> preventEFromTyping() {
        return OptionInstance.createBoolean("qualityofqueso.options.prevent_e_from_typing", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.prevent_e_from_typing.tooltip")),
                YES_NO_TEXT, options().preventEFromTyping, value -> options().preventEFromTyping = value);
    }

    public static OptionInstance<Boolean> helpfulTooltips() {
        return OptionInstance.createBoolean("qualityofqueso.options.helpful_tooltips", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.helpful_tooltips.tooltip")),
                ON_OFF_TEXT, options().helpfulTooltips, value -> options().helpfulTooltips = value);
    }

    public static OptionInstance<Boolean> itemFrameSearching() {
        return OptionInstance.createBoolean("qualityofqueso.options.item_frame_searching", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.item_frame_searching.tooltip",
                        keyBindingAsString(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI))),
                ON_OFF_TEXT, coptions().itemFrameSearching, value -> coptions().itemFrameSearching = value);
    }

    public static OptionInstance<Boolean> multiServerConfigs() {
        return OptionInstance.createBoolean("qualityofqueso.options.multi_server_configs", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.multi_server_configs.tooltip")),
                ON_OFF_TEXT, uoptions().multiServerConfigs, value -> uoptions().multiServerConfigs = value);
    }

    public static OptionInstance<Boolean> showInGameTime() {
        return OptionInstance.createBoolean("qualityofqueso.options.show_in_game_time", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.show_in_game_time.tooltip")),
                ON_OFF_TEXT, options().showInGameTime, value -> options().showInGameTime = value);
    }

    public static OptionInstance<Boolean> showIrlTime() {
        return OptionInstance.createBoolean("qualityofqueso.options.show_irl_time", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.show_irl_time.tooltip")),
                ON_OFF_TEXT, options().showIrlTime, value -> options().showIrlTime = value);
    }

    public static OptionInstance<Boolean> fog() {
        return OptionInstance.createBoolean("qualityofqueso.options.fog", OptionInstance.cachedConstantTooltip(
                        Component.translatable("qualityofqueso.options.fog.tooltip")
                                .copy()
                                .append(!uoptions().applyFogFunction ?
                                        Component.translatable("qualityofqueso.options.fog.disabled") :
                                        isSimpleKeybindsLoaded() ? Component.translatable("qualityofqueso.options.fog.simple_keybinds") : ModTexts.BLANK)
                ),
                ON_OFF_TEXT, options().fog, value -> options().fog = value);
    }

    public static OptionInstance<Boolean> autoCloseRecipeBook() {
        return OptionInstance.createBoolean("qualityofqueso.options.auto_close_recipe_book", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.auto_close_recipe_book.tooltip", options().autoCloseRecipeBook)),
                ON_OFF_TEXT, options().autoCloseRecipeBook, value -> options().autoCloseRecipeBook = value);
    }

    public static OptionInstance<Boolean> mobHitDing() {
        return OptionInstance.createBoolean("qualityofqueso.options.mob_hit_ding", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.mob_hit_ding.tooltip", options().minMobHitDingDistance)),
                ON_OFF_TEXT, options().mobHitDing, value -> options().mobHitDing = value);
    }

    public static OptionInstance<Integer> minMobHitDingDistance() {
        return new OptionInstance<>("qualityofqueso.options.min_mob_hit_ding_distance",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.min_mob_hit_ding_distance.tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + " blocks")),
                new OptionInstance.IntRange(15, 50), options().minMobHitDingDistance, value -> options().minMobHitDingDistance = value);
    }

    public static OptionInstance<QoQButtons> qoqButtons() {
        return new OptionInstance<>(
                "qualityofqueso.options.qoq_buttons",
                option -> {
                    return switch (option) {
                        case EVERYWHERE -> Tooltip.create(Component.translatable("qualityofqueso.options.qoq_buttons.everywhere.tooltip"));
                        case TITLE_ONLY -> Tooltip.create(Component.translatable("qualityofqueso.options.qoq_buttons.title_only.tooltip"));
                        case OFF -> Tooltip.create(Component.translatable("qualityofqueso.options.qoq_buttons.off.tooltip"));
                    };
                },
                (optionText, value) -> value.getCaption(),
                new OptionInstance.Enum<>(Arrays.asList(QoQButtons.values()), QoQButtons.Codec),
                options().qoqButtons,
                value -> options().qoqButtons = value);
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
                new OptionInstance.IntRange(0, 180), options().itemFrameSearchGlowDuration, value -> options().itemFrameSearchGlowDuration = value);
    }

    public static OptionInstance<Integer> itemFrameSearchRadius() {
        return new OptionInstance<>("qualityofqueso.options.item_frame_search_radius",
                OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.item_frame_search_radius.tooltip")),
                (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + "m")),
                new OptionInstance.IntRange(25, 500), options().itemFrameSearchRadius, value -> options().itemFrameSearchRadius = value);
    }

    /**
     * @return the bound key as a string.
     */
    private static String keyBindingAsString(KeyMapping keyBinding) {
        return parseKeyAsString(keyBinding.getKey().toString().toUpperCase());
    }

    /**
     * @return the key name (ex. B or F).
     */
    private static String parseKeyAsString(String translationKey) {
        return translationKey.substring(13).toUpperCase();
    }
}
package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.util.ModTexts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Options displayed on {@link ModOptionsScreen}.
 */
@Environment(EnvType.CLIENT)
public class ModListOptions {
    public static final SimpleOption<Boolean> ENABLE_MOD = new SimpleOption<>("qualityofqueso.options.enable_mod", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.enable_mod.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().enableMod, value -> QoQ.options().enableMod = value);

    public static final SimpleOption<Boolean> BETTER_GUI_EXIT = new SimpleOption<>("qualityofqueso.options.better_gui_exit", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.better_gui_exit.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().betterGuiExit, value -> QoQ.options().betterGuiExit = value);

    public static final SimpleOption<Boolean> BETTER_SEARCHING = new SimpleOption<>("qualityofqueso.options.better_searching", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.better_searching.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().betterSearching, value -> QoQ.options().betterSearching = value);

    public static final SimpleOption<Boolean> CHEST_SEARCHING = new SimpleOption<>("qualityofqueso.options.chest_searching", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.chest_searching.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().chestSearching, value -> QoQ.options().chestSearching = value);

    public static final SimpleOption<Boolean> SEARCH_INVENTORY = new SimpleOption<>("qualityofqueso.options.search_inventory", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.search_inventory.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().searchInventory, value -> QoQ.options().searchInventory = value);

    public static final SimpleOption<Boolean> INVENTORY_SEARCHING = new SimpleOption<>("qualityofqueso.options.inventory_searching", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.inventory_searching.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().inventorySearching, value -> QoQ.options().inventorySearching = value);

    public static final SimpleOption<Boolean> SAVE_SEARCH_TEXT = new SimpleOption<>("qualityofqueso.options.save_search_text", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.save_search_text.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().saveSearchText, value -> QoQ.options().saveSearchText = value);

    public static SimpleOption<Boolean> inventoryManagement() {
        return new SimpleOption<>("qualityofqueso.options.inventory_management", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.inventory_management.tooltip",
                keyBindingAsString(ModKeybinds.MOVE_CONTAINER),
                keyBindingAsString(ModKeybinds.MOVE_INVENTORY))),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().inventoryManagement, value -> QoQ.options().inventoryManagement = value);
    }

    public static final SimpleOption<Boolean> SHORTCUT_KEYS = new SimpleOption<>("qualityofqueso.options.shortcut_keys", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.shortcut_keys.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().shortcutKeys, value -> QoQ.options().shortcutKeys = value);

    public static final SimpleOption<Boolean> QUICK_DROP = new SimpleOption<>("qualityofqueso.options.quick_drop", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.quick_drop.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().quickDrop, value -> QoQ.options().quickDrop = value);

    public static SimpleOption<Boolean> swapping() {
        return new SimpleOption<>("qualityofqueso.options.swapping", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.swapping.tooltip",
                keyBindingAsString(ModKeybinds.SWAP_ITEMS))),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().swapping, value -> QoQ.options().swapping = value);
    }

    public static final SimpleOption<Boolean> INCLUDE_HOTBAR = new SimpleOption<>("qualityofqueso.options.include_hotbar", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.include_hotbar.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().includeHotbar, value -> QoQ.options().includeHotbar = value);

    public static SimpleOption<Boolean> quickEquip() {
        return new SimpleOption<>("qualityofqueso.options.quick_equip", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.quick_equip.tooltip",
                keyBindingAsString(ModKeybinds.QUICK_EQUIP))),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().quickEquip, value -> QoQ.options().quickEquip = value);
    }

    public static final SimpleOption<Boolean> PREVENT_RAGE_QUITTING = new SimpleOption<>("qualityofqueso.options.prevent_rage_quitting", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.prevent_rage_quitting.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().preventRageQuitting, value -> QoQ.options().preventRageQuitting = value);

    public static final SimpleOption<Boolean> PREVENT_E_FROM_TYPING = new SimpleOption<>("qualityofqueso.options.prevent_e_from_typing", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.prevent_e_from_typing.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().preventEFromTyping, value -> QoQ.options().preventEFromTyping = value);

    public static final SimpleOption<Boolean> HELPFUL_TOOLTIPS = new SimpleOption<>("qualityofqueso.options.helpful_tooltips", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.helpful_tooltips.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().helpfulTooltips, value -> QoQ.options().helpfulTooltips = value);

    public static SimpleOption<Boolean> itemFrameSearching() {
        return new SimpleOption<>("qualityofqueso.options.item_frame_searching", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.item_frame_searching.tooltip",
                keyBindingAsString(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI))),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.coptions().itemFrameSearching, value -> QoQ.coptions().itemFrameSearching = value);
    }

    public static final SimpleOption<Boolean> SHOW_QOQ_BUTTONS = new SimpleOption<>("qualityofqueso.options.show_qoq_buttons", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.show_qoq_buttons.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QoQ.options().showQoQButtons, value -> QoQ.options().showQoQButtons = value);

    public static final SimpleOption<Integer> ITEM_FRAME_SEARCH_TIMER =
            new SimpleOption<>("qualityofqueso.options.item_frame_search_timer", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.item_frame_search_timer.tooltip")),
                    (optionText, value) -> {
                        if (value == 0) {
                            return GameOptions.getGenericValueText(optionText, Text.literal("No Timer").formatted(Formatting.GREEN));
                        } else if (value < 60) {
                            return GameOptions.getGenericValueText(optionText, Text.literal(value + "s"));
                        } else {
                            int minutes = value / 60; // Gets minutes value
                            int seconds = value % 60; // Gets seconds value
                            if (seconds == 0) {
                                return GameOptions.getGenericValueText(optionText, Text.literal(minutes + "m"));
                            } else {
                                return GameOptions.getGenericValueText(optionText, Text.literal(minutes + "m " + seconds + "s"));
                            }
                        }
                    },
                    new SimpleOption.ValidatingIntSliderCallbacks(0, 180), QoQ.options().itemFrameSearchTimer, value -> QoQ.options().itemFrameSearchTimer = value);

    public static final SimpleOption<Integer> ITEM_FRAME_SEARCH_RADIUS =
            new SimpleOption<>("qualityofqueso.options.item_frame_search_radius", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.item_frame_search_radius.tooltip")),
                    (optionText, value) -> GameOptions.getGenericValueText(optionText, Text.literal(value + "m")),
                    new SimpleOption.ValidatingIntSliderCallbacks(25, 500), QoQ.options().itemFrameSearchRadius, value -> QoQ.options().itemFrameSearchRadius = value);

    /**
     * @return the bound key as a string.
     */
    private static String keyBindingAsString(KeyBinding keyBinding) {
        return parseKeyAsString(keyBinding.boundKey.toString().toUpperCase());
    }

    /**
     * @return the key name (ex. B or F).
     */
    private static String parseKeyAsString(String translationKey) {
        return translationKey.substring(13).toUpperCase();
    }
}
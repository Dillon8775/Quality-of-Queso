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

    public static final OptionInstance<Boolean> ENABLE_MOD = OptionInstance.createBoolean("qualityofqueso.options.enable_mod", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.enable_mod.tooltip")),
            YES_NO_TEXT, ModClientOptions.ENABLE_MOD.get(), ModClientOptions.ENABLE_MOD::set);

    public static final OptionInstance<Boolean> BETTER_GUI_EXIT = OptionInstance.createBoolean("qualityofqueso.options.better_gui_exit", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.better_gui_exit.tooltip")),
            ON_OFF_TEXT, ModClientOptions.BETTER_GUI_EXIT.get(), ModClientOptions.BETTER_GUI_EXIT::set);

    public static final OptionInstance<Boolean> BETTER_SEARCHING = OptionInstance.createBoolean("qualityofqueso.options.better_searching", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.better_searching.tooltip")),
            ON_OFF_TEXT, ModClientOptions.BETTER_SEARCHING.get(), ModClientOptions.BETTER_SEARCHING::set);

    public static final OptionInstance<Boolean> CHEST_SEARCHING = OptionInstance.createBoolean("qualityofqueso.options.chest_searching", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.chest_searching.tooltip")),
            ON_OFF_TEXT, ModClientOptions.CHEST_SEARCHING.get(), ModClientOptions.CHEST_SEARCHING::set);

    public static final OptionInstance<Boolean> SEARCH_INVENTORY = OptionInstance.createBoolean("qualityofqueso.options.search_inventory", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.search_inventory.tooltip")),
            YES_NO_TEXT, ModClientOptions.SEARCH_INVENTORY.get(), ModClientOptions.SEARCH_INVENTORY::set);

    public static final OptionInstance<Boolean> INVENTORY_SEARCHING = OptionInstance.createBoolean("qualityofqueso.options.inventory_searching", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.inventory_searching.tooltip")),
            ON_OFF_TEXT, ModClientOptions.INVENTORY_SEARCHING.get(), ModClientOptions.INVENTORY_SEARCHING::set);

    public static final OptionInstance<Boolean> SAVE_SEARCH_TEXT = OptionInstance.createBoolean("qualityofqueso.options.save_search_text", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.save_search_text.tooltip")),
            YES_NO_TEXT, ModClientOptions.SAVE_SEARCH_TEXT.get(), ModClientOptions.SAVE_SEARCH_TEXT::set);

    public static OptionInstance<Boolean> inventoryManagement() {
        return OptionInstance.createBoolean("qualityofqueso.options.inventory_management", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.inventory_management.tooltip",
                        keyBindingAsString(ModKeybinds.MOVE_CONTAINER),
                        keyBindingAsString(ModKeybinds.MOVE_INVENTORY))),
                ON_OFF_TEXT, ModClientOptions.INVENTORY_MANAGEMENT.get(), ModClientOptions.INVENTORY_MANAGEMENT::set);
    }

    public static final OptionInstance<Boolean> SHORTCUT_KEYS = OptionInstance.createBoolean("qualityofqueso.options.shortcut_keys", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.shortcut_keys.tooltip")),
            ON_OFF_TEXT, ModClientOptions.SHORTCUT_KEYS.get(), ModClientOptions.SHORTCUT_KEYS::set);

    public static final OptionInstance<Boolean> QUICK_DROP = OptionInstance.createBoolean("qualityofqueso.options.quick_drop", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.quick_drop.tooltip")),
            ON_OFF_TEXT, ModClientOptions.QUICK_DROP.get(), ModClientOptions.QUICK_DROP::set);

    public static OptionInstance<Boolean> swapping() {
        return OptionInstance.createBoolean("qualityofqueso.options.swapping", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.swapping.tooltip",
                        keyBindingAsString(ModKeybinds.SWAP_ITEMS))),
                ON_OFF_TEXT, ModClientOptions.SWAPPING.get(), ModClientOptions.SWAPPING::set);
    }

    public static final OptionInstance<Boolean> INCLUDE_HOTBAR = OptionInstance.createBoolean("qualityofqueso.options.include_hotbar", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.include_hotbar.tooltip")),
            YES_NO_TEXT, ModClientOptions.INCLUDE_HOTBAR.get(), ModClientOptions.INCLUDE_HOTBAR::set);

    public static final OptionInstance<Boolean> LEGACY_QUICK_MOVE = OptionInstance.createBoolean("qualityofqueso.options.legacy_quick_move", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.legacy_quick_move.tooltip")),
            ON_OFF_TEXT, ModClientOptions.LEGACY_QUICK_MOVE.get(), ModClientOptions.LEGACY_QUICK_MOVE::set);

    public static final OptionInstance<Boolean> SHOW_BUTTON_OUTLINES = OptionInstance.createBoolean("qualityofqueso.options.show_button_outlines", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.show_button_outlines.tooltip")),
            YES_NO_TEXT, ModClientOptions.SHOW_BUTTON_OUTLINES.get(), ModClientOptions.SHOW_BUTTON_OUTLINES::set);

    public static final OptionInstance<Boolean> SHOW_BUTTON_SHORTCUTS = OptionInstance.createBoolean("qualityofqueso.options.show_button_shortcuts", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.show_button_shortcuts.tooltip")),
            YES_NO_TEXT, ModClientOptions.SHOW_BUTTON_SHORTCUTS.get(), ModClientOptions.SHOW_BUTTON_SHORTCUTS::set);

    public static OptionInstance<Boolean> quickEquip() {
        return OptionInstance.createBoolean("qualityofqueso.options.quick_equip", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.quick_equip.tooltip",
                        keyBindingAsString(ModKeybinds.QUICK_EQUIP))),
                ON_OFF_TEXT, ModClientOptions.QUICK_EQUIP.get(), ModClientOptions.QUICK_EQUIP::set);
    }

    public static final OptionInstance<Boolean> PREVENT_RAGE_QUITTING = OptionInstance.createBoolean("qualityofqueso.options.prevent_rage_quitting", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.prevent_rage_quitting.tooltip")),
            YES_NO_TEXT, ModClientOptions.PREVENT_RAGE_QUITTING.get(), ModClientOptions.PREVENT_RAGE_QUITTING::set);

    public static final OptionInstance<Boolean> PREVENT_E_FROM_TYPING = OptionInstance.createBoolean("qualityofqueso.options.prevent_e_from_typing", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.prevent_e_from_typing.tooltip")),
            YES_NO_TEXT, ModClientOptions.PREVENT_E_FROM_TYPING.get(), ModClientOptions.PREVENT_E_FROM_TYPING::set);

    public static final OptionInstance<Boolean> HELPFUL_TOOLTIPS = OptionInstance.createBoolean("qualityofqueso.options.helpful_tooltips", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.helpful_tooltips.tooltip")),
            ON_OFF_TEXT, ModClientOptions.HELPFUL_TOOLTIPS.get(), ModClientOptions.HELPFUL_TOOLTIPS::set);

    public static OptionInstance<Boolean> itemFrameSearching() {
        return OptionInstance.createBoolean("qualityofqueso.options.item_frame_searching", OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.item_frame_searching.tooltip",
                        keyBindingAsString(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI))),
                ON_OFF_TEXT, ModCommonOptions.ITEM_FRAME_SEARCHING.get(), ModCommonOptions.ITEM_FRAME_SEARCHING::set);
    }

    public static final OptionInstance<QoQButtons> QOQ_BUTTONS = new OptionInstance<>(
            "qualityofqueso.options.qoq_buttons",
            option -> {
                return switch (option) {
                    case EVERYWHERE -> Tooltip.create(Component.translatable("qualityofqueso.options.qoq_buttons.everywhere.tooltip"));
                    case TITLE_ONLY -> Tooltip.create(Component.translatable("qualityofqueso.options.qoq_buttons.title_only.tooltip"));
                    case OFF -> Tooltip.create(Component.translatable("qualityofqueso.options.qoq_buttons.off.tooltip"));
                };
            },
            (optionText, value) -> value.getText(),
            new OptionInstance.Enum<>(Arrays.asList(QoQButtons.values()), QoQButtons.Codec),
            ModClientOptions.QOQ_BUTTONS.get(),
            ModClientOptions.QOQ_BUTTONS::set);

    public static final OptionInstance<Integer> ITEM_FRAME_SEARCH_TIMER =
            new OptionInstance<>("qualityofqueso.options.item_frame_search_timer",
                    OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.item_frame_search_timer.tooltip")),
                    (optionText, value) -> {
                        if (value == 0) {
                            return Options.genericValueLabel(optionText, Component.literal("No Timer").withStyle(ChatFormatting.GREEN));
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
                    new OptionInstance.IntRange(0, 180), ModClientOptions.ITEM_FRAME_SEARCH_TIMER.get(), ModClientOptions.ITEM_FRAME_SEARCH_TIMER::set);

    public static final OptionInstance<Integer> ITEM_FRAME_SEARCH_RADIUS =
            new OptionInstance<>("qualityofqueso.options.item_frame_search_radius",
                    OptionInstance.cachedConstantTooltip(Component.translatable("qualityofqueso.options.item_frame_search_radius.tooltip")),
                    (optionText, value) -> Options.genericValueLabel(optionText, Component.literal(value + "m")),
                    new OptionInstance.IntRange(25, 500), ModClientOptions.ITEM_FRAME_SEARCH_RADIUS.get(), ModClientOptions.ITEM_FRAME_SEARCH_RADIUS::set);

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
package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.option.screen.ModOptionsScreen;
import net.dillon.qualityofqueso.util.ModTexts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;

import static net.dillon.qualityofqueso.main.QoQ.*;

/**
 * Options displayed on {@link ModOptionsScreen}.
 */
@Environment(EnvType.CLIENT)
public class ModListOptions {
    public static SimpleOption<Boolean> enableMod() {
        return new SimpleOption<>("qualityofqueso.options.enable_mod", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.enable_mod.tooltip")),
                (optionText, value) -> !value ? ModTexts.NO : ModTexts.YES, SimpleOption.BOOLEAN, options().enableMod, value -> options().enableMod = value);
    }

    public static SimpleOption<Boolean> betterGuiExit() {
        return new SimpleOption<>("qualityofqueso.options.better_gui_exit", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.better_gui_exit.tooltip")),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, options().betterGuiExit, value -> options().betterGuiExit = value);
    }

    public static SimpleOption<Boolean> betterSearching() {
        return new SimpleOption<>("qualityofqueso.options.better_searching", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.better_searching.tooltip")),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, options().betterSearching, value -> options().betterSearching = value);
    }

    public static SimpleOption<Boolean> chestSearching() {
        return new SimpleOption<>("qualityofqueso.options.chest_searching", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.chest_searching.tooltip")),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, options().chestSearching, value -> options().chestSearching = value);
    }

    public static  SimpleOption<Boolean> searchInventory() {
        return new SimpleOption<>("qualityofqueso.options.search_inventory", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.search_inventory.tooltip")),
                (optionText, value) -> !value ? ModTexts.NO : ModTexts.YES, SimpleOption.BOOLEAN, options().searchInventory, value -> options().searchInventory = value);
    }

    public static SimpleOption<Boolean> inventorySearching() {
        return new SimpleOption<>("qualityofqueso.options.inventory_searching", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.inventory_searching.tooltip")),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, options().inventorySearching, value -> options().inventorySearching = value);
    }

    public static SimpleOption<Boolean> saveSearchText() {
        return new SimpleOption<>("qualityofqueso.options.save_search_text", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.save_search_text.tooltip")),
                (optionText, value) -> !value ? ModTexts.NO : ModTexts.YES, SimpleOption.BOOLEAN, options().saveSearchText, value -> options().saveSearchText = value);
    }

    public static SimpleOption<Transferring> transferring() {
        return new SimpleOption<>(
                "qualityofqueso.options.transferring",
                option -> {
                    Text text = ModTexts.BLANK;
                    switch (option) {
                        case SHORTCUT_KEY_OR_BUTTON -> text = Text.translatable("qualityofqueso.options.transferring.shortcut_key_or_button.tooltip");
                        case SHORTCUT_KEY_ONLY -> text = Text.translatable("qualityofqueso.options.transferring.shortcut_key_only.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.of(Text.translatable("qualityofqueso.options.transferring.tooltip", keyBindingAsString(ModKeybinds.MOVE_INVENTORY), keyBindingAsString(ModKeybinds.MOVE_CONTAINER)).append(appended).append(text));
                },
                (optionText, value) -> value.getText(),
                new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(Transferring.values()), Transferring.Codec),
                options().transferring,
                value -> options().transferring = value);
    }

    public static SimpleOption<ContainerSorting> containerSorting() {
        return new SimpleOption<>(
                "qualityofqueso.options.container_sorting",
                option -> {
                    Text text = ModTexts.BLANK;
                    switch (option) {
                        case SHORTCUT_KEY_OR_BUTTON -> text = Text.translatable("qualityofqueso.options.container_sorting.shortcut_key_or_button.tooltip");
                        case SHORTCUT_KEY_ONLY -> text = Text.translatable("qualityofqueso.options.container_sorting.shortcut_key_only.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.of(Text.translatable("qualityofqueso.options.container_sorting.tooltip", keyBindingAsString(ModKeybinds.SORT_CONTAINER)).append(appended).append(text));
                },
                (optionText, value) -> value.getText(),
                new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(ContainerSorting.values()), ContainerSorting.Codec),
                options().containerSorting,
                value -> options().containerSorting = value);
    }

    public static SimpleOption<QuickDrop> quickDrop() {
        return new SimpleOption<>(
                "qualityofqueso.options.quick_drop",
                option -> {
                    Text text = ModTexts.BLANK;
                    switch (option) {
                        case SHORTCUT_KEY_OR_BUTTON -> text = Text.translatable("qualityofqueso.options.quick_drop.shortcut_key_or_button.tooltip");
                        case SHORTCUT_KEY_ONLY -> text = Text.translatable("qualityofqueso.options.quick_drop.shortcut_key_only.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.of(Text.translatable("qualityofqueso.options.quick_drop.tooltip").append(appended).append(text));
                },
                (optionText, value) -> value.getText(),
                new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(QuickDrop.values()), QuickDrop.Codec),
                options().quickDrop,
                value -> options().quickDrop = value);
    }

    public static SimpleOption<Swapping> swapping() {
        return new SimpleOption<>(
                "qualityofqueso.options.swapping",
                option -> {
                    Text text = ModTexts.BLANK;
                    switch (option) {
                        case SHORTCUT_KEY_OR_BUTTON -> text = Text.translatable("qualityofqueso.options.swapping.shortcut_key_or_button.tooltip");
                        case SHORTCUT_KEY_ONLY -> text = Text.translatable("qualityofqueso.options.swapping.shortcut_key_only.tooltip");
                    }
                    String appended = !text.equals(ModTexts.BLANK) ? "\n\n" : "";
                    return Tooltip.of(Text.translatable("qualityofqueso.options.swapping.tooltip", keyBindingAsString(ModKeybinds.SWAP_ITEMS)).append(appended).append(text));
                },
                (optionText, value) -> value.getText(),
                new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(Swapping.values()), Swapping.Codec),
                options().swapping,
                value -> options().swapping = value);
    }

    public static SimpleOption<Boolean> includeHotbar() {
        return new SimpleOption<>("qualityofqueso.options.include_hotbar", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.include_hotbar.tooltip")),
                (optionText, value) -> !value ? ModTexts.NO : ModTexts.YES, SimpleOption.BOOLEAN, options().includeHotbar, value -> options().includeHotbar = value);
    }

    public static SimpleOption<Boolean> perpendicularQuickMoving() {
        return new SimpleOption<>("qualityofqueso.options.perpendicular_quick_moving", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.perpendicular_quick_moving.tooltip")),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, options().perpendicularQuickMoving, value -> options().perpendicularQuickMoving = value);
    }

    public static SimpleOption<Boolean> dragToSort() {
        return new SimpleOption<>("qualityofqueso.options.drag_to_sort", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.drag_to_sort.tooltip")),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, options().dragToSort, value -> options().dragToSort = value);
    }

    public static SimpleOption<Boolean> showButtonShortcuts() {
        return new SimpleOption<>("qualityofqueso.options.show_button_shortcuts", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.show_button_shortcuts.tooltip")),
                (optionText, value) -> !value ? ModTexts.NO : ModTexts.YES, SimpleOption.BOOLEAN, options().showButtonShortcuts, value -> options().showButtonShortcuts = value);
    }

    public static SimpleOption<Boolean> quickEquip() {
        return new SimpleOption<>("qualityofqueso.options.quick_equip", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.quick_equip.tooltip",
                keyBindingAsString(ModKeybinds.QUICK_EQUIP))),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, options().quickEquip, value -> options().quickEquip = value);
    }

    public static SimpleOption<Boolean> preventRageQuitting() {
        return new SimpleOption<>("qualityofqueso.options.prevent_rage_quitting", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.prevent_rage_quitting.tooltip")),
                (optionText, value) -> !value ? ModTexts.NO : ModTexts.YES, SimpleOption.BOOLEAN, options().preventRageQuitting, value -> options().preventRageQuitting = value);
    }

    public static SimpleOption<Boolean> preventEFromTyping() {
        return new SimpleOption<>("qualityofqueso.options.prevent_e_from_typing", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.prevent_e_from_typing.tooltip")),
                (optionText, value) -> !value ? ModTexts.NO : ModTexts.YES, SimpleOption.BOOLEAN, options().preventEFromTyping, value -> options().preventEFromTyping = value);
    }

    public static SimpleOption<Boolean> helpfulTooltips() {
        return new SimpleOption<>("qualityofqueso.options.helpful_tooltips", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.helpful_tooltips.tooltip")),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, options().helpfulTooltips, value -> options().helpfulTooltips = value);
    }

    public static SimpleOption<Boolean> itemFrameSearching() {
        return new SimpleOption<>("qualityofqueso.options.item_frame_searching", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.item_frame_searching.tooltip",
                keyBindingAsString(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI))),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, coptions().itemFrameSearching, value -> coptions().itemFrameSearching = value);
    }

    public static SimpleOption<Boolean> multiServerConfigs() {
        return new SimpleOption<>("qualityofqueso.options.multi_server_configs", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.multi_server_configs.tooltip")),
                (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, uoptions().multiServerConfigs, value -> uoptions().multiServerConfigs = value);
    }

    public static SimpleOption<QoQButtons> qoqButtons() {
        return new SimpleOption<>(
                "qualityofqueso.options.qoq_buttons",
                option -> {
                    return switch (option) {
                        case EVERYWHERE -> Tooltip.of(Text.translatable("qualityofqueso.options.qoq_buttons.everywhere.tooltip"));
                        case TITLE_ONLY -> Tooltip.of(Text.translatable("qualityofqueso.options.qoq_buttons.title_only.tooltip"));
                        case OFF -> Tooltip.of(Text.translatable("qualityofqueso.options.qoq_buttons.off.tooltip"));
                    };
                },
                (optionText, value) -> value.getText(),
                new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(QoQButtons.values()), QoQButtons.Codec),
                options().qoqButtons,
                value -> options().qoqButtons = value);
    }

    public static SimpleOption<Integer> itemFrameSearchGlowDuration() {
        return new SimpleOption<>("qualityofqueso.options.item_frame_search_glow_duration", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.item_frame_search_glow_duration.tooltip")),
                (optionText, value) -> {
                    if (value == 0) {
                        return GameOptions.getGenericValueText(optionText, Text.literal("Indefinite").formatted(Formatting.RED));
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
                new SimpleOption.ValidatingIntSliderCallbacks(0, 180), options().itemFrameSearchGlowDuration, value -> options().itemFrameSearchGlowDuration = value);
    }

    public static SimpleOption<Integer> itemFrameSearchRadius() {
        return new SimpleOption<>("qualityofqueso.options.item_frame_search_radius", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.item_frame_search_radius.tooltip")),
                (optionText, value) -> GameOptions.getGenericValueText(optionText, Text.literal(value + "m")),
                new SimpleOption.ValidatingIntSliderCallbacks(25, 500), options().itemFrameSearchRadius, value -> options().itemFrameSearchRadius = value);
    }

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
package net.dillon.qualityofqueso.event.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.event.QuesoScreen;
import net.dillon.qualityofqueso.event.management.CursorKey;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.dillon.qualityofqueso.helper.GuiHelper;
import net.dillon.qualityofqueso.helper.MethodHelper;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.dillon.qualityofqueso.event.management.ExtractingEvents.setCursor;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.*;
import static net.dillon.qualityofqueso.helper.ModHelper.allDisallowedKeys;
import static net.dillon.qualityofqueso.helper.ModHelper.popularKeys;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.*;
import static net.dillon.qualityofqueso.keybind.ModKeyMappings.QUICK_DROP;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles all key pressing events.
 */
public class KeyPressedEvents extends ManagementEvents {

    public KeyPressedEvents(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Handles management shortcut keys while the modifier key is held.
     */
    private void handleManagementKeybinds(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        boolean quickDropShortcutPressed = kumaKeyPressed(QUICK_DROP, event);

        if (!quickDropShortcutPressed
                && !isCreativeInventoryScreen(holder().screen())
                && transferInstance().canSingularQuickDrop(event)) {
            transferInstance().performSingularDrop();
        }

        if (client().lockedSlots().lockedSlots && client().lockedSlots().preventDropping && event.key() == key(getDropKey()).getValue() && holder().screensHoveredSlot() != null && lockedSlotsInstance().isLockedSlot(holder().screensHoveredSlot().index)) {
            if (canScrollMoveAndHasScrollModifierDown() && hasDropOnlyOneItemModifierDown()) {
                if (!Minecraft.getInstance().hasAltDown()) {
                    performClickSlot(holder().screen(), holder().screensHoveredSlot(), holder().screensHoveredSlot().index, 1, ContainerInput.THROW);
                }
            } else {
                cir.setReturnValue(false);
            }
        }

        if (!hasAnyManagementModifierDown()) {
            return;
        }

        if (!(holder().screen() instanceof AbstractContainerScreen<?>)) {
            return;
        }

        if (client().management().transferring.any()) {
            if (kumaKeyPressed(ModKeyMappings.MOVE_TO_INVENTORY, event)) {
                transferInstance().transferItems(true, false);
            }
            if (!isBrewingOrFurnaceScreen(holder().screen()) && kumaKeyPressed(ModKeyMappings.MOVE_TO_CONTAINER, event)) {
                transferInstance().transferItems(false, false);
            }
        }

        if (client().sorting().sorting.any()
                && kumaKeyPressed(ModKeyMappings.SORT, event)) {
            sortingInstance().trySort();
        }

        if (client().management().quickDrop.any() && quickDropShortcutPressed) {
            transferInstance().dropItems(!isContainerScreen(holder().screen()));
        }

        if (isContainerScreen(holder().screen())
                && client().management().swapping.any()
                && kumaKeyPressed(ModKeyMappings.SWAP_ITEMS, event)) {
            transferInstance().trySwap();
        }
    }

    /**
     * Handles inventory-key closing behavior.
     *
     * @return {@code true} when key handling should stop immediately.
     */
    private boolean handleInventoryCloseKey(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (event.key() != key(Minecraft.getInstance().options.keyInventory).getValue()) {
            return false;
        }

        boolean isContainer = isContainerScreen(holder().screen());
        boolean isInventory = isInventoryScreen(holder().screen());

        if (!searchInstance().hasSearchField()) {
            holder().screen().onClose();
            cir.setReturnValue(true);
            return true;
        }

        if (client().accessibility().preventEFromTyping) {
            boolean isFocused = (holder().searchFields().container() != null && holder().searchFields().container().isFocused())
                    || (holder().searchFields().inventory() != null && holder().searchFields().inventory().isFocused());

            if (!isFocused || (!isContainer && !isInventory)) {
                holder().screen().onClose();
                cir.setReturnValue(true);
            }
        }

        return false;
    }

    /**
     * Handles key pressing events, such as management keybinds, quick searching into different search bars, and correct closing of screens.
     */
    public void handleKeyPressing(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (isContainerScreen(holder().screen()) && hoveredSlotHasItem(holder().screensHoveredSlot()) && hasAllQuickDropModifiersDown() && !shouldButtonBeActive(false, null)) {
            cir.setReturnValue(true);
        }

        if (client().misc().enhancedCursor && event.key() == key(getDropKey()).getValue()) {
            setCursor(CursorKey.CROSSHAIR);
        }

        if (handleInventoryCloseKey(event, cir)) {
            return;
        }
        handleManagementKeybinds(event, cir);

        // Quick equip logic
        if (kumaKeyPressed(ModKeyMappings.QUICK_EQUIP, event)) {
            quickEquipInstance().quickEquipItem();
            if (hoveredSlotHasItem(holder().screensHoveredSlot())) {
                if (holder().searchFields().inventory() != null && holder().searchFields().inventory().isFocused()) {
                    holder().searchFields().inventory().setFocused(false);
                }
                if (isInventoryScreen(holder().screen())) {
                    return;
                }
            }
        }

        // Prevent E from typing entirely in fromInventory screens
        boolean canCloseFromE = (holder().searchFields().container() != null && !holder().searchFields().container().isFocused()) || (holder().searchFields().inventory() != null && !holder().searchFields().inventory().isFocused());
        if ((event.key() == key(Minecraft.getInstance().options.keyInventory).getValue() && client().accessibility().preventEFromTyping && canCloseFromE)
                && (isContainerScreen(holder().screen()) || isInventoryScreen(holder().screen()) || isCreativeInventoryScreen(holder().screen()))) {
            holder().screen().onClose();
            cir.setReturnValue(true);
        }

        // Declare typing variables
        // Both of these variables apply to disallowed keys and hotbar switching
        boolean ignoreTyping = hoveredSlotHasItem(holder().screensHoveredSlot()); // Basic ignore typing variable; applies to recipe book screens only.
        boolean secondaryIgnoreTyping = false; // Secondary ignore typing variable; applies to "T" and "E" keys and chest searching screens only.

        // These variables only apply to the chest search bar
        boolean numberKeyPressed = false; // Determines if a number key was pressed
        boolean hotbarKeyPressed = false; // Determines if a hotbar key was pressed
        boolean dropKeyPressed = false; // Determines if the drop key was pressed
        boolean swapKeyPressed = false; // Determines if the swap item key was pressed

        // If a "disallowed key" is pressed, ignoreTyping and secondaryIgnoreTyping become true.
        for (int key : allDisallowedKeys()) {
            if (event.key() == key) {
                ignoreTyping = true;
                secondaryIgnoreTyping = true;
                break;
            }
        }

        // handle switching items from hotbar to another containerSlot in fromInventory; cancel out typing if an query can be moved
        if (holder().screen().getMenu().getCarried().isEmpty() && holder().screensHoveredSlot() != null) {
            for (int i = 0; i < 9; i++) {
                if (holder().mc().options.keyHotbarSlots[i].matches(event)) {
                    ignoreTyping = true;
                    secondaryIgnoreTyping = true;
                    hotbarKeyPressed = true;
                    break;
                }
            }
            if (holder().screen() instanceof InventoryScreen && kumaKeyPressed(ModKeyMappings.QUICK_EQUIP, event) && hoveredSlotHasItem(holder().screensHoveredSlot())) {
                ignoreTyping = true;
            }
        }

        // Handle 'T' and 'E' keys
        for (int key : popularKeys()) {
            if (event.key() == key) {
                secondaryIgnoreTyping = false;
                cir.setReturnValue(true);
                break;
            }
        }

        // Check if number key was pressed
        List<Integer> numbers = List.of(
                InputConstants.KEY_1,
                InputConstants.KEY_2,
                InputConstants.KEY_3,
                InputConstants.KEY_4,
                InputConstants.KEY_5,
                InputConstants.KEY_6,
                InputConstants.KEY_7,
                InputConstants.KEY_8,
                InputConstants.KEY_9
        );
        for (int key : numbers) {
            if (event.key() == key) {
                numberKeyPressed = true;
                break;
            }
        }

        // Check if drop key or swap hands key was pressed
        if (holder().screensHoveredSlot() != null) {
            if (event.key() == key(getDropKey()).getValue()) {
                secondaryIgnoreTyping = true;
                dropKeyPressed = true;
            }
        }
        if (hoveredSlotHasItem(holder().screensHoveredSlot()) || !Minecraft.getInstance().player.getOffhandItem().isEmpty()) {
            if (event.key() == key(Minecraft.getInstance().options.keySwapOffhand).getValue()) {
                secondaryIgnoreTyping = true;
                swapKeyPressed = true;
            }
        }

        // If any of these are true, the user cannot type in the search field
        boolean cannotType = (numberKeyPressed || hotbarKeyPressed || dropKeyPressed || swapKeyPressed) && hoveredSlotHasItem(holder().screensHoveredSlot());

        // Recipe book search field logic
        if (holder().screen() instanceof AbstractRecipeBookScreen<?> recipeScreen && !Minecraft.getInstance().hasControlDown()) {
            boolean swapKeyValid = swapKeyPressed && (hoveredSlotHasItem(holder().screensHoveredSlot()) || holder().screen().getMenu().getSlot(45).hasItem());
            if (!client().accessibility().preventEFromTyping || event.key() != key(Minecraft.getInstance().options.keyInventory).getValue()) {
                if (client().searching().quickSearch.enabledForAny() && !ignoreTyping && !swapKeyValid && !dropKeyPressed && !getRecipeBookComponent(recipeScreen).isVisible() &&
                        (holder().searchFields().inventory() == null ||
                                (!holder().searchFields().inventory().isFocused() && !client().searching().quickSearch.searchBar()))) {
                    getRecipeBookComponent(recipeScreen).toggleVisibility();
                    MethodHelper.refreshWidgets(holder().screen());
                }
                if (getSearchBoxInsideRecipeBook(recipeScreen) != null) {
                    boolean unfocus = false;
                    for (int i = 0; i < 9; i++) {
                        if (holder().screensHoveredSlot() != null && holder().mc().options.keyHotbarSlots[i].matches(event)) {
                            unfocus = true;
                            break;
                        }
                    }
                    if (Minecraft.getInstance().hasShiftDown() || unfocus) {
                        getSearchBoxInsideRecipeBook(recipeScreen).setFocused(false);
                        return;
                    }
                    if (!cannotType && holder().searchFields().inventory() == null) {
                        GuiHelper.autoFocusElement(getSearchBoxInsideRecipeBook(recipeScreen), event, true);
                    } else if (client().searching().quickSearch.onForAny() || client().searching().quickSearch.recipeBook()) {
                        getRecipeBookComponent(recipeScreen).setFocused(!cannotType);
                    }

                    if (getSearchBoxInsideRecipeBook(recipeScreen).isFocused()) {
                        if (getRecipeBookComponent(recipeScreen).keyPressed(event)) {
                            cir.setReturnValue(true);
                        }
                    }
                }
            }
        }
        // Inventory search field logic
        if (client().searching().inventorySearching && holder().searchFields().inventory() != null) {
            if (!Minecraft.getInstance().hasControlDown() && holder().screen() instanceof AbstractRecipeBookScreen<?> recipeScreen && getRecipeBookComponent(recipeScreen).isVisible() && !holder().searchFields().inventory().isFocused()) {
                GuiHelper.autoFocusElement(getSearchBoxInsideRecipeBook(recipeScreen), event, !cannotType);
            } else if ((client().searching().quickSearch.enabledForAny() || client().searching().quickSearch.searchBar()) && !secondaryIgnoreTyping && (!Minecraft.getInstance().hasControlDown() || (Minecraft.getInstance().hasControlDown() && event.key() == InputConstants.KEY_A))) {
                GuiHelper.autoFocusElement(holder().searchFields().inventory(), event, true);
            } else if (holder().searchFields().inventory().isFocused() && cannotType) {
                holder().searchFields().inventory().setFocused(false);
            }

            // Unfocus and close recipe book when fromInventory search field is focused
            if (holder().screen() instanceof AbstractRecipeBookScreen<?> recipeScreen && getSearchBoxInsideRecipeBook(recipeScreen) != null) {
                if (holder().searchFields().inventory().isFocused()) {
                    getSearchBoxInsideRecipeBook(recipeScreen).setFocused(false);
                    if (event.key() == InputConstants.KEY_BACKSPACE && getRecipeBookComponent(recipeScreen).isVisible()) {
                        String text = holder().searchFields().searchText();
                        getRecipeBookComponent(recipeScreen).toggleVisibility();
                        MethodHelper.refreshWidgets(holder().screen());
                        holder().searchFields().inventory().setValue(text.substring(0, text.length() - 1));
                        holder().searchFields().inventory().setFocused(true);
                        holder().screen().setFocused(holder().searchFields().inventory());
                    }
                    return;
                }
                // Unfocus fromInventory search field when recipe book search field is focused
                else if (getSearchBoxInsideRecipeBook(recipeScreen).isFocused()) {
                    holder().searchFields().inventory().setFocused(false);
                }
            }

            if (holder().searchFields().inventory().isFocused() && holder().searchFields().inventory().keyPressed(event)) {
                cir.setReturnValue(true);
            }
        }

        // Chest search field logic
        if (client().searching().containerSearching && isContainerScreen(holder().screen()) && holder().searchFields().container() != null) {
            if ((client().searching().quickSearch.onForAny() || client().searching().quickSearch.searchBar()) && !secondaryIgnoreTyping && (!Minecraft.getInstance().hasControlDown() || (Minecraft.getInstance().hasControlDown() && event.key() == InputConstants.KEY_A))) {
                GuiHelper.autoFocusElement(holder().searchFields().container(), event, true);
            } else if (holder().searchFields().container().isFocused() && cannotType) {
                holder().searchFields().container().setFocused(false);
            }

            if (holder().searchFields().container().isFocused() && holder().searchFields().container().keyPressed(event)) {
                cir.setReturnValue(true);
            }
        }
    }
}
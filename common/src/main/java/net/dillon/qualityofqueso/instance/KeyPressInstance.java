package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.helper.MethodHelper;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.inventory.ContainerInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.*;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.keybind.ModKeyMappings.*;

/**
 * Handles all key pressing events.
 */
public class KeyPressInstance extends ManagementInstance {

    public KeyPressInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Handles management shortcut keys while the modifier key is held.
     */
    private void handleManagementKeybinds(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        boolean quickDropShortcutPressed = kumaKeyPressed(QUICK_DROP, event);

        if (!quickDropShortcutPressed
                && !isCreativeInventoryScreen(instance().getScreen())
                && transferInstance().canSingularQuickDrop(event)) {
            transferInstance().performSingularDrop();
        }

        if (options().lockedSlots.enableLockedSlots && options().lockedSlots.preventDropping && event.key() == key(getDropKey()).getValue() && instance().getScreensHoveredSlot() != null && lockedSlotsInstance().isLockedSlot(instance().getScreensHoveredSlot().index)) {
            if (hasMoveSingleModifierDown() && hasDropOnlyOneItemKeyDown()) {
                if (!Minecraft.getInstance().hasAltDown()) {
                    performClickSlot(instance().getScreen(), instance().getScreensHoveredSlot(), instance().getScreensHoveredSlot().index, 1, ContainerInput.THROW);
                }
            } else {
                cir.setReturnValue(false);
            }
        }

        if (!hasAnyManagementModifierDown()) {
            return;
        }

        if (!(instance().getScreen() instanceof AbstractContainerScreen<?>)) {
            return;
        }

        if (options().management.transferring.buttonOrKeyOrKeyOnly()) {
            if (kumaKeyPressed(ModKeyMappings.MOVE_TO_INVENTORY, event)) {
                transferInstance().transferItems(true, false);
            }
            if (!isBrewingOrFurnaceScreen(instance().getScreen()) && kumaKeyPressed(ModKeyMappings.MOVE_TO_CONTAINER, event)) {
                transferInstance().transferItems(false, false);
            }
        }

        if (options().sorting.sortingEnabled.buttonOrKeyOrKeyOnly()
                && kumaKeyPressed(ModKeyMappings.SORT, event)) {
            sortingInstance().trySort();
        }

        if (options().management.quickDrop.buttonOrKeyOrKeyOnly() && quickDropShortcutPressed) {
            transferInstance().dropItems(!isContainerScreen(instance().getScreen()));
        }

        if (isContainerScreen(instance().getScreen())
                && options().management.swapping.buttonOrKeyOrKeyOnly()
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

        boolean isContainer = isContainerScreen(instance().getScreen());
        boolean isInventory = isInventoryScreen(instance().getScreen());

        if (!searchInstance().hasSearchField()) {
            instance().getScreen().onClose();
            cir.setReturnValue(true);
            return true;
        }

        if (options().accessibility.preventEFromTyping) {
            boolean isFocused = (instance().getSearchFields().container() != null && instance().getSearchFields().container().isFocused())
                    || (instance().getSearchFields().inventory() != null && instance().getSearchFields().inventory().isFocused());

            if (!isFocused || (!isContainer && !isInventory)) {
                instance().getScreen().onClose();
                cir.setReturnValue(true);
            }
        }

        return false;
    }

    /**
     * Handles key pressing events, such as management keybinds, quick searching into different search bars, and correct closing of screens.
     */
    public void handleKeyPressing(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (isContainerScreen(instance().getScreen()) && hoveredSlotHasItem(instance().getScreensHoveredSlot()) && hasQuickDropKeysDown() && !shouldButtonBeActive(false, null)) {
            cir.setReturnValue(true);
        }

        if (handleInventoryCloseKey(event, cir)) {
            return;
        }
        handleManagementKeybinds(event, cir);

        // Quick equip logic
        if (kumaKeyPressed(ModKeyMappings.QUICK_EQUIP, event)) {
            quickEquipInstance().quickEquip();
            if (hoveredSlotHasItem(instance().getScreensHoveredSlot())) {
                if (instance().getSearchFields().inventory() != null && instance().getSearchFields().inventory().isFocused()) {
                    instance().getSearchFields().inventory().setFocused(false);
                }
                if (isInventoryScreen(instance().getScreen())) {
                    return;
                }
            }
        }

        // Prevent E from typing entirely in fromInventory screens
        boolean canCloseFromE = (instance().getSearchFields().container() != null && !instance().getSearchFields().container().isFocused()) || (instance().getSearchFields().inventory() != null && !instance().getSearchFields().inventory().isFocused());
        if ((event.key() == key(Minecraft.getInstance().options.keyInventory).getValue() && options().accessibility.preventEFromTyping && canCloseFromE)
                && (isContainerScreen(instance().getScreen()) || isInventoryScreen(instance().getScreen()) || isCreativeInventoryScreen(instance().getScreen()))) {
            instance().getScreen().onClose();
            cir.setReturnValue(true);
        }

        // Declare typing variables
        // Both of these variables apply to disallowed keys and hotbar switching
        boolean ignoreTyping = hoveredSlotHasItem(instance().getScreensHoveredSlot()); // Basic ignore typing variable; applies to recipe book screens only.
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
        if (instance().getScreen().getMenu().getCarried().isEmpty() && instance().getScreensHoveredSlot() != null) {
            for (int i = 0; i < 9; i++) {
                if (instance().getMinecraft().options.keyHotbarSlots[i].matches(event)) {
                    ignoreTyping = true;
                    secondaryIgnoreTyping = true;
                    hotbarKeyPressed = true;
                    break;
                }
            }
            if (instance().getScreen() instanceof InventoryScreen && kumaKeyPressed(ModKeyMappings.QUICK_EQUIP, event) && hoveredSlotHasItem(instance().getScreensHoveredSlot())) {
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
                GLFW.GLFW_KEY_1,
                GLFW.GLFW_KEY_2,
                GLFW.GLFW_KEY_3,
                GLFW.GLFW_KEY_4,
                GLFW.GLFW_KEY_5,
                GLFW.GLFW_KEY_6,
                GLFW.GLFW_KEY_7,
                GLFW.GLFW_KEY_8,
                GLFW.GLFW_KEY_9
        );
        for (int key : numbers) {
            if (event.key() == key) {
                numberKeyPressed = true;
                break;
            }
        }

        // Check if drop key or swap hands key was pressed
        if (instance().getScreensHoveredSlot() != null) {
            if (event.key() == key(getDropKey()).getValue()) {
                secondaryIgnoreTyping = true;
                dropKeyPressed = true;
            }
        }
        if (hoveredSlotHasItem(instance().getScreensHoveredSlot()) || !Minecraft.getInstance().player.getOffhandItem().isEmpty()) {
            if (event.key() == key(Minecraft.getInstance().options.keySwapOffhand).getValue()) {
                secondaryIgnoreTyping = true;
                swapKeyPressed = true;
            }
        }

        // If any of these are true, the user cannot type in the search field
        boolean cannotType = (numberKeyPressed || hotbarKeyPressed || dropKeyPressed || swapKeyPressed) && hoveredSlotHasItem(instance().getScreensHoveredSlot());

        // Recipe book search field logic
        if (instance().getScreen() instanceof AbstractRecipeBookScreen<?> recipeScreen && !Minecraft.getInstance().hasControlDown() && !hasAnyManagementModifierDown()) {
            boolean swapKeyValid = swapKeyPressed && (hoveredSlotHasItem(instance().getScreensHoveredSlot()) || instance().getScreen().getMenu().getSlot(45).hasItem());
            if (!options().accessibility.preventEFromTyping || event.key() != key(Minecraft.getInstance().options.keyInventory).getValue()) {
                if (options().searching.quickSearch.enabled() && !ignoreTyping && !swapKeyValid && !dropKeyPressed && !getRecipeBookComponent(recipeScreen).isVisible() &&
                        (instance().getSearchFields().inventory() == null ||
                                (!instance().getSearchFields().inventory().isFocused() && !options().searching.quickSearch.searchBar()))) {
                    getRecipeBookComponent(recipeScreen).toggleVisibility();
                    MethodHelper.refreshWidgets(instance().getScreen());
                }
                if (getSearchBoxInsideRecipeBook(recipeScreen) != null) {
                    boolean unfocus = false;
                    for (int i = 0; i < 9; i++) {
                        if (instance().getScreensHoveredSlot() != null && instance().getMinecraft().options.keyHotbarSlots[i].matches(event)) {
                            unfocus = true;
                            break;
                        }
                    }
                    if (Minecraft.getInstance().hasShiftDown() || unfocus) {
                        getSearchBoxInsideRecipeBook(recipeScreen).setFocused(false);
                        return;
                    }
                    if (!cannotType && instance().getSearchFields().inventory() == null) {
                        getSearchBoxInsideRecipeBook(recipeScreen).setFocused(true);
                    } else if (options().searching.quickSearch.on() || options().searching.quickSearch.recipeBook()) {
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
        if (options().searching.inventorySearching && instance().getSearchFields().inventory() != null) {
            if (!Minecraft.getInstance().hasControlDown() && !hasAnyManagementModifierDown() && instance().getScreen() instanceof AbstractRecipeBookScreen<?> recipeScreen && getRecipeBookComponent(recipeScreen).isVisible() && !instance().getSearchFields().inventory().isFocused()) {
                getSearchBoxInsideRecipeBook(recipeScreen).setFocused(!cannotType);
            } else if ((options().searching.quickSearch.enabled() || options().searching.quickSearch.searchBar()) && !secondaryIgnoreTyping && (!Minecraft.getInstance().hasControlDown() || (Minecraft.getInstance().hasControlDown() && event.key() == GLFW.GLFW_KEY_A))) {
                instance().getSearchFields().inventory().setFocused(true);
                instance().getScreen().setFocused(instance().getSearchFields().inventory());
            } else if (instance().getSearchFields().inventory().isFocused() && cannotType) {
                instance().getSearchFields().inventory().setFocused(false);
            }

            // Unfocus and close recipe book when fromInventory search field is focused
            if (instance().getScreen() instanceof AbstractRecipeBookScreen<?> recipeScreen && getSearchBoxInsideRecipeBook(recipeScreen) != null) {
                if (instance().getSearchFields().inventory().isFocused()) {
                    getSearchBoxInsideRecipeBook(recipeScreen).setFocused(false);
                    if (event.key() == GLFW.GLFW_KEY_BACKSPACE && getRecipeBookComponent(recipeScreen).isVisible()) {
                        String text = instance().getSearchFields().searchText();
                        getRecipeBookComponent(recipeScreen).toggleVisibility();
                        MethodHelper.refreshWidgets(instance().getScreen());
                        instance().getSearchFields().inventory().setValue(text.substring(0, text.length() - 1));
                        instance().getSearchFields().inventory().setFocused(true);
                        instance().getScreen().setFocused(instance().getSearchFields().inventory());
                    }
                    return;
                }
                // Unfocus fromInventory search field when recipe book search field is focused
                else if (getSearchBoxInsideRecipeBook(recipeScreen).isFocused()) {
                    instance().getSearchFields().inventory().setFocused(false);
                }
            }

            if (instance().getSearchFields().inventory().isFocused() && instance().getSearchFields().inventory().keyPressed(event)) {
                cir.setReturnValue(true);
            }
        }

        // Chest search field logic
        if (options().searching.containerSearching && isContainerScreen(instance().getScreen()) && instance().getSearchFields().container() != null) {
            if ((options().searching.quickSearch.on() || options().searching.quickSearch.searchBar()) && !secondaryIgnoreTyping && (!Minecraft.getInstance().hasControlDown() || (Minecraft.getInstance().hasControlDown() && event.key() == GLFW.GLFW_KEY_A))) {
                instance().getSearchFields().container().setFocused(true);
            } else if (instance().getSearchFields().container().isFocused() && cannotType) {
                instance().getSearchFields().container().setFocused(false);
            }

            if (instance().getSearchFields().container().isFocused() && instance().getSearchFields().container().keyPressed(event)) {
                cir.setReturnValue(true);
            }
        }
    }
}
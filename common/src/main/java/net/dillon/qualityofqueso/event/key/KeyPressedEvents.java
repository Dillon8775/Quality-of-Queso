package net.dillon.qualityofqueso.event.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.management.CursorKey;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.dillon.qualityofqueso.helper.GuiHelper;
import net.dillon.qualityofqueso.helper.MethodHelper;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.inventory.SlotWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.event.management.ExtractingEvents.setCursor;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.*;
import static net.dillon.qualityofqueso.helper.ModHelper.allDisallowedAutoFocusKeys;
import static net.dillon.qualityofqueso.helper.ModHelper.popularKeys;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.*;
import static net.dillon.qualityofqueso.keybind.ModKeyMappings.QUICK_DROP;
import static net.dillon.qualityofqueso.option.ClientOptionInstances.client;

/**
 * Handles all key pressing events.
 */
public class KeyPressedEvents extends ManagementEvents {
    private final KeyEvent event;
    private final CallbackInfoReturnable<Boolean> cir;

    /// Applies to disallowed keys that block autofocusing and hotbar switching actions Both of these variables apply to disallowed keys and hotbar switching
    private boolean blockRecipeBookTyping = hoveredSlotHasItem(holder().getHoveredSlotWidget()); // Basic ignore typing variable; applies to recipe book screens only
    private boolean blockContainerTyping; // Secondary ignore typing variable; applies to "T" and "E" keys and chest searching screens only

    /// Only apply to container search bar
    private boolean numberOrHotbarKeyPressed; // Determines if a number or hotbar key was pressed
    private boolean dropKeyPressed; // Determines if the drop key was pressed
    private boolean swapKeyPressed; // Determines if the swap item key was pressed

    public KeyPressedEvents(QuesoScreenHolder screen, KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        super(screen);
        this.event = event;
        this.cir = cir;
    }

    /**
     * Handles inventory-key closing behavior.
     *
     * @return {@code true} when key handling should stop immediately.
     */
    private boolean handleInventoryCloseKey() {
        if (event.key() != inputKey(getMinecraft().options.keyInventory).getValue()) {
            return false;
        }

        if (!searchEvents().hasSearchField()) {
            holder().screen().onClose();
            cir.setReturnValue(true);
            return true;
        }

        if (client().misc().preventEFromTyping) {
            return handlePreventEFromTyping();
        }

        return false;
    }

    /**
     * Handles the "{@code prevent E from typing}" option.
     */
    private boolean handlePreventEFromTyping() {
        if (event.key() == inputKey(getMinecraft().options.keyInventory).getValue()
                && client().misc().preventEFromTyping
                && canCloseFromE()
                && (isContainerScreen() || isInventoryScreen() || isCreativeInventoryScreen())) {
            holder().screen().onClose();
            return true;
        }

        return false;
    }

    /**
     * @return if the screen can close from the "{@code E}" key.
     */
    private boolean canCloseFromE() {
        boolean containerFieldFocused =
                (holder().searchFields().container() != null && holder().searchFields().container().isFocused());

        boolean inventoryFieldFocused =
                (holder().searchFields().inventory() != null && holder().searchFields().inventory().isFocused())
                        || isSearchBoxInsideRecipeBookBordered();

        return !containerFieldFocused && !inventoryFieldFocused;
    }

    /**
     * @return if the search box inside the current recipe book screen is bordered.
     */
    private boolean isSearchBoxInsideRecipeBookBordered() {
        return holder().screen() instanceof AbstractRecipeBookScreen<?> recipeBookScreen
                && getSearchBoxInsideRecipeBook(recipeBookScreen) != null
                && getSearchBoxInsideRecipeBook(recipeBookScreen).isBordered();
    }

    /**
     * Handles management shortcut keys while the modifier key is held.
     */
    private void handleManagementKeybinds() {
        boolean quickDropShortcutPressed = kumaKeyPressed(QUICK_DROP, event);

        SlotWidget hoveredSlotWidget = holder().getHoveredSlotWidget();

        if (!quickDropShortcutPressed
                && !isCreativeInventoryScreen()
                && transferEvents().canSingularQuickDrop(event)) {
            transferEvents().performSingularDrop(hoveredSlotWidget);
        }

        if (client().lockedSlots().lockedSlots && client().lockedSlots().preventDropping && event.key() == inputKey(getDropKey()).getValue() && hoveredSlotWidget != null && lockedSlotEvents().isLockedSlot(hoveredSlotWidget.slot().index)) {
            if (canScrollMoveAndHasScrollModifierDown() && hasDropOnlyOneItemModifierDown()) {
                if (!hasAltDown()) {
                    performClickSlot(holder().screen(), hoveredSlotWidget.slot().index, 1, ContainerInput.THROW);
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

        if (client().management().transferring.enabled()) {
            if (kumaKeyPressed(ModKeyMappings.MOVE_TO_INVENTORY, event)) {
                transferEvents().transferItems(true, false);
            }

            if (!isBrewingOrFurnaceScreen() && kumaKeyPressed(ModKeyMappings.MOVE_TO_CONTAINER, event)) {
                transferEvents().transferItems(false, false);
            }
        }

        if (client().sorting().sorting.enabled()
                && kumaKeyPressed(ModKeyMappings.SORT, event)) {
            sortingEvents().trySort();
        }

        if (client().management().quickDrop.enabled() && quickDropShortcutPressed) {
            transferEvents().dropItems();
            cir.setReturnValue(true);
        }

        if (isContainerScreen()
                && client().management().swapping.enabled()
                && kumaKeyPressed(ModKeyMappings.SWAP_ITEMS, event)) {
            transferEvents().trySwap();
        }
    }

    /**
     * @return if the user attempted to press ctrl + a or no ctrl at all.
     */
    private boolean hasCtrlAllOrNoCtrl() {
        return !hasCtrlDown() || (hasCtrlDown() && event.key() == InputConstants.KEY_A);
    }

    /**
     * Handles quick equipping.
     */
    private boolean handleQuickEquip() {
        if (kumaKeyPressed(ModKeyMappings.QUICK_EQUIP, event)) {
            quickEquipEvents().quickEquipItem();
            if (hoveredSlotHasItem(holder().getHoveredSlotWidget())) {
                if (holder().searchFields().inventory() != null && holder().searchFields().inventory().isFocused()) {
                    holder().searchFields().inventory().setFocused(false);
                }

                return isInventoryScreen();
            }
        }

        return false;
    }

    /**
     * Handles all disallowed keys.
     */
    private void handleAllDisallowedKeys() {
        for (int key : allDisallowedAutoFocusKeys()) {
            if (event.key() == key) {
                blockRecipeBookTyping = true;
                blockContainerTyping = true;
                break;
            }
        }
    }

    /**
     * Handles hotbar switching between inventories, and when quick-equipping.
     */
    private void checkForItemSwitch() {
        if (holder().screen().getMenu().getCarried().isEmpty() && holder().getHoveredSlotWidget() != null) {
            for (int i = 0; i < 9; i++) {
                if (getMinecraft().options.keyHotbarSlots[i].matches(event)) {
                    blockRecipeBookTyping = true;
                    blockContainerTyping = true;
                    numberOrHotbarKeyPressed = true;
                    break;
                }
            }

            if (holder().screen() instanceof InventoryScreen && kumaKeyPressed(ModKeyMappings.QUICK_EQUIP, event) && hoveredSlotHasItem(holder().getHoveredSlotWidget())) {
                blockRecipeBookTyping = true;
            }
        }
    }

    /**
     * Handles pressing popular keys.
     */
    private void handlePopularKeys() {
        for (int popularKey : popularKeys()) {
            if (event.key() == popularKey) {
                blockContainerTyping = false;
                cir.setReturnValue(true);
                break;
            }
        }
    }

    /**
     * Checks if any number key was pressed.
     */
    private void checkNumberKeyPressed() {
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
                numberOrHotbarKeyPressed = true;
                break;
            }
        }
    }

    /**
     * Checks if the drop key was pressed.
     */
    private void checkDropKeyPressed() {
        if (holder().getHoveredSlotWidget() != null) {
            if (event.key() == inputKey(getDropKey()).getValue()) {
                blockContainerTyping = true;
                dropKeyPressed = true;
            }
        }
    }

    /**
     * Checks if the swap item key was pressed.
     */
    private void checkSwapKeyPressed() {
        Minecraft minecraft = getMinecraft();
        if (hoveredSlotHasItem(holder().getHoveredSlotWidget()) || minecraft.player.getOffhandItem().isEmpty()) {
            if (event.key() == inputKey(minecraft.options.keySwapOffhand).getValue()) {
                blockContainerTyping = true;
                swapKeyPressed = true;
            }
        }
    }

    /**
     * Handles key pressing events, such as management keybinds, quick searching into different search bars, and correct closing of screens.
     */
    public void handleKeyPressing() {
        // Return key pressed cannot quick drop but attempted
        if (isContainerScreen() && hoveredSlotHasItem(holder().getHoveredSlotWidget()) && hasAllQuickDropModifiersDown() && !shouldButtonBeActive(false, null)) {
            cir.setReturnValue(true);
        }

        // If user dropped something, set static cursor to crosshair
        if (client().gui().enhancedCursor && event.key() == inputKey(getDropKey()).getValue()) {
            setCursor(CursorKey.CROSSHAIR);
        }

        // Handle inventory close key (ex. E)
        if (handleInventoryCloseKey()) {
            return;
        }

        // Management keybind handling (can pass through)
        handleManagementKeybinds();

        // Quick equip logic
        if (handleQuickEquip()) {
            return;
        }

        // Prevent E from typing entirely in fromInventory screens
        if (handlePreventEFromTyping()) {
            return;
        }

        // If a "disallowed key that blocks auto-focusing" is pressed, blockRecipeBookTyping and blockContainerTyping become true
        handleAllDisallowedKeys();

        // Handle switching items from hotbar to another containerSlot in fromInventory; cancel out typing if a query can be moved
        checkForItemSwitch();

        // Handle 'T' and 'E' keys
        handlePopularKeys();

        // Check if number key was pressed
        checkNumberKeyPressed();

        // Block container typing if drop key was pressed
        checkDropKeyPressed();

        // Block container typing if player has offhand slot
        checkSwapKeyPressed();

        // If any of these are true, the user cannot type in the search field
        boolean cannotType = (numberOrHotbarKeyPressed || dropKeyPressed || swapKeyPressed) && hoveredSlotHasItem(holder().getHoveredSlotWidget());

        // Determine if user can type into recipe book
        boolean canTypeRecipeBook = !hasCtrlDown() && !hasAltDown();

        // Recipe book search field logic
        if (holder().screen() instanceof AbstractRecipeBookScreen<?> recipeScreen
                && canTypeRecipeBook
        ) {
            boolean swapKeyValid = swapKeyPressed
                    && (hoveredSlotHasItem(holder().getHoveredSlotWidget()) || holder().screen().getMenu().getSlot(45).hasItem());
            if (!client().misc().preventEFromTyping
                    || event.key() != inputKey(getMinecraft().options.keyInventory).getValue()
            ) {
                if (client().searching().quickSearch.enabledForAny()
                        && !blockRecipeBookTyping
                        && !swapKeyValid
                        && !dropKeyPressed
                        && !getRecipeBookComponent(recipeScreen).isVisible()
                        &&
                        (
                                holder().searchFields().inventory() == null
                                        ||
                                        (
                                                !holder().searchFields().inventory().isFocused() && !client().searching().quickSearch.searchBar()
                                        )
                        )
                ) {
                    getRecipeBookComponent(recipeScreen).toggleVisibility();
                    MethodHelper.refreshWidgets(holder().screen());
                }

                if (getSearchBoxInsideRecipeBook(recipeScreen) != null) {
                    boolean unfocus = false;
                    for (int i = 0; i < 9; i++) {
                        if (holder().getHoveredSlotWidget() != null && getMinecraft().options.keyHotbarSlots[i].matches(event)) {
                            unfocus = true;
                            break;
                        }
                    }

                    if (hasShiftDown() || unfocus) {
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
            if (canTypeRecipeBook
                    && holder().screen() instanceof AbstractRecipeBookScreen<?> recipeScreen
                    && getRecipeBookComponent(recipeScreen).isVisible()
                    && !holder().searchFields().inventory().isFocused()
            ) {
                GuiHelper.autoFocusElement(getSearchBoxInsideRecipeBook(recipeScreen), event, !cannotType);
            } else if (
                    (client().searching().quickSearch.enabledForAny() || client().searching().quickSearch.searchBar())
                            && !isSearchBoxInsideRecipeBookBordered()
                            && !blockContainerTyping
                            && hasCtrlAllOrNoCtrl()
            ) {
                GuiHelper.autoFocusElement(holder().searchFields().inventory(), event, true);
            } else if (holder().searchFields().inventory().isFocused() && cannotType) {
                holder().searchFields().inventory().setFocused(false);
            }

            // Unfocus and close recipe book when fromInventory search field is focused
            if (holder().screen() instanceof AbstractRecipeBookScreen<?> recipeScreen
                    && getSearchBoxInsideRecipeBook(recipeScreen) != null
            ) {
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

            if (holder().searchFields().inventory().isFocused()
                    && holder().searchFields().inventory().keyPressed(event)
            ) {
                cir.setReturnValue(true);
            }
        }

        // Container search field logic
        if (client().searching().containerSearching
                && isContainerScreen()
                && holder().searchFields().container() != null
        ) {
            if (
                    (client().searching().quickSearch.onForAny() || client().searching().quickSearch.searchBar())
                            && !blockContainerTyping
                            && hasCtrlAllOrNoCtrl()
            ) {
                GuiHelper.autoFocusElement(holder().searchFields().container(), event, true);
            } else if (holder().searchFields().container().isFocused() && cannotType) {
                holder().searchFields().container().setFocused(false);
            }

            if (holder().searchFields().container().isFocused()
                    && holder().searchFields().container().keyPressed(event)
            ) {
                cir.setReturnValue(true);
            }
        }
    }
}
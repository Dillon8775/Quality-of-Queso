package net.dillon.qualityofqueso.event.management;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.option.eum.general.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static net.dillon.dillonlib.task.ClientTasks.drawSprite;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.*;
import static net.dillon.qualityofqueso.keybind.ModKeyMappings.MOVE_TO_CONTAINER;
import static net.dillon.qualityofqueso.keybind.ModKeyMappings.MOVE_TO_INVENTORY;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles extracting and rendering events.
 */
public class ExtractingEvents extends ManagementEvents {
    private static boolean swapCursor = false;

    public ExtractingEvents(QuesoScreenHolder screen) {
        super(screen);
    }

    /**
     * Sets the cursor type to a new cursor type.
     */
    public static void setCursor(CursorKey cursorKey) {
        CURSOR_KEY = cursorKey;
    }

    /**
     * Handles all slot extracting events.
     */
    public void handleSlotExtraction(GuiGraphicsExtractor graphics) {
        extractLockedSlotColor(graphics);
        extractHighlightedSlots(graphics);
    }

    /**
     * Grays out filtered slots (or hotbar slots), renders the locked slot texture, and highlights matching items according to the cursor.
     */
    public void grayoutSlotsAndExtractLockedIcon(GuiGraphicsExtractor graphics) {
        // Begin iterating slots to gray out
        boolean inventorySearchFieldPresent = holder().searchFields().inventory() != null;
        boolean validScreen = isValidScreen(holder().screen());
        for (int i = 0; i < searchEvents().getSearchSlotCount(); i++) {
            Slot slot = holder().menu().getSlot(i);

            // Gray out hotbar slots if include hotbar is off and one of the transfer buttons are hovered
            boolean alreadyExcluded = false;
            // Only do this on valid screens
            if (validScreen) {
                if (searchEvents().isFilteredBySearch(slot, inventorySearchFieldPresent)) {
                    renderGrayedSlot(graphics, slot, false);
                    alreadyExcluded = true;
                }
                // Otherwise, gray out slots that don't match the search
                else if (!client().management().includingHotbar
                        && (isInventoryScreen(holder().screen()) ? isInventoryHotbarSlot(true, slot.index) : isHotbarSlot(holder().menu().slots.size(), slot.index))
                        && (client().management().transferring.any() || client().management().scrollMoving)) {
                    if (shouldGrayout(slot)) {
                        renderGrayedSlot(graphics, slot, slot.hasItem());
                        alreadyExcluded = true;
                    }
                }
            }
            // Renders the lock texture on locked slots (yes, the lock icon itself, not the color)
            if (isValidScreenForRenderingSlotOverlays(holder().screen()) && client().lockedSlots().showLock.inScreens() && client().lockedSlots().lockedSlots && holder().searchFields().searchText().isEmpty() && holder().excludedSlots().isEmpty()) {
                lockedSlotEvents().renderLockedSlot(graphics, slot, true);
            }

            // Return out for further code if not valid screen
            if (!validScreen) {
                continue;
            }

            // Gray out player-chosen excluded slots
            if (!alreadyExcluded && client().management().dragSorting) {
                for (int id : holder().excludedSlots()) {
                    if (slot.index == id) {
                        boolean renderUnavailable = true;

                        if (isContainerScreen(holder().screen())) {
                            // Don't grayout if CTRL is pressed and transfer keys are bounded
                            if (client().management().transferring.any()
                                    && (kumaAnyModifierDown(MOVE_TO_INVENTORY) || kumaAnyModifierDown(MOVE_TO_CONTAINER))
                                    && MOVE_TO_INVENTORY.getBinding().key().getValue() != InputConstants.UNKNOWN.getValue()
                                    && MOVE_TO_CONTAINER.getBinding().key().getValue() != InputConstants.UNKNOWN.getValue()) {
                                renderUnavailable = false;
                            } else if (buttonHoveredActiveOrShiftHeld(this, holder().managementButtons().getTransferContainer(), false)) {
                                renderUnavailable = slot.index <= getTotalSlots() - 37;
                            } else if (buttonHoveredActiveOrShiftHeld(this, holder().managementButtons().getTransferInventory(), true)) {
                                renderUnavailable = slot.index >= getTotalSlots() - 36;
                            }
                        }
                        // Above is complex. We must deteremine the size to search to grayout, so that the correct slots are grayed out at the correct position

                        // If a slot was found as "unavailable", render it as unavailable
                        if (renderUnavailable) {
                            renderGrayedSlot(graphics, slot, false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles all main extracting events.
     */
    public void handle(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractEnhancedCursor(graphics);
        clearToRefreshWidgets();
        extractSearchFields(graphics, mouseX, mouseY, a);
        extractButtons(graphics, mouseX, mouseY, a);
        extractLockingUnlockingSlots(graphics, mouseX, mouseY);
    }

    /**
     * Grays out a search, typically from search queries or excluding hotbar.
     */
    private void renderGrayedSlot(GuiGraphicsExtractor graphics, Slot slot, boolean hotbarOverlay) {
        String id = "grayed";
        if (hotbarOverlay) {
            id = "grayed_hotbar";
        } else if (client().accessibility().darkerOverlay || client().general().theme != Theme.VANILLA) {
            id = "grayed_dark";
        }
        drawSprite(graphics, qoqIdentifier("slot_overlays/" + id), slot.x, slot.y, 16, 16);
    }

    /**
     * Renders a highlighted slot, based on the hovered or held item.
     */
    private void renderHighlightedSlot(GuiGraphicsExtractor graphics, Slot slot) {
        graphics.fill(slot.x - 2, slot.y - 2, slot.x + 17, slot.y + 17, client().management().matchingItemsColor);
    }

    /**
     * Renders the blue "locked" overlay for locked slots (not the lock icon, the color itself)
     */
    private void extractLockedSlotColor(GuiGraphicsExtractor graphics) {
        if (!client().lockedSlots().lockedSlots || !isValidScreenForRenderingSlotOverlays(holder().screen())) {
            return;
        }

        for (int i = 0; i < searchEvents().getSearchSlotCount(); i++) {
            Slot slot = holder().menu().getSlot(i);
            lockedSlotEvents().renderLockedSlot(graphics, slot, false);
        }
    }

    /**
     * Highlight matching items based on the cursor held item or hovered item
     */
    private void extractHighlightedSlots(GuiGraphicsExtractor graphics) {
        if (!isValidScreenForRenderingSlotOverlays(holder().screen()) || !(client().management().highlightMatchingItems.always() || (client().management().highlightMatchingItems.onCtrl() && Minecraft.getInstance().hasControlDown()))) {
            return;
        }

        Slot hoveredSlot = holder().screensHoveredSlot();

        // Get the slot to check (if cursor stack isn't empty, or there is no hovered slot, then check cursor stack)
        // Otherwise, check hovered slot stack
        ItemStack cursorStack = getCursorStack();
        ItemStack stackToCheck = (hoveredSlot == null || !cursorStack.isEmpty())
                ? cursorStack
                : hoveredSlot.getItem();

        // Check all slots in current screen
        boolean foundMatchingItem = false;

        for (int i = 0; i < getTotalSlots(); i++) {
            Slot slot = holder().menu().getSlot(i);
            ItemStack slotStack = slot.getItem();

            if (slotStack.isEmpty()) {
                continue;
            }

            boolean isHoveredSlot = hoveredSlot != null && slot.index == hoveredSlot.index;

            if (stackToCheck.is(slotStack.getItem()) && !isHoveredSlot) {
                renderHighlightedSlot(graphics, slot);
                foundMatchingItem = true;
            }
        }

        // Highlight hovered slot afterward if another match exists
        if (foundMatchingItem && hoveredSlot != null) {
            ItemStack hoveredStack = hoveredSlot.getItem();

            if (!hoveredStack.isEmpty() && stackToCheck.is(hoveredStack.getItem())) {
                renderHighlightedSlot(graphics, hoveredSlot);
            }
        }
    }

    /**
     * Renders the process of unlocking / locking slots.
     */
    private void extractLockingUnlockingSlots(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // Only render other locked slot textures on valid screens
        if (isValidScreen(holder().screen())) {
            // Renders the key, or unlocked slot texture beside the mouse, indicating that the user is attempting to lock/unlock slots
            if (client().lockedSlots().lockedSlots && client().lockedSlots().showLock.inScreens() && holder().screensHoveredSlot() != null && holder().excludedSlots().isEmpty()
                    && hasLockSlotModifierDown() && !hasAnyManagementModifierDown() && !Minecraft.getInstance().hasControlDown() && !Minecraft.getInstance().hasShiftDown()) {
                lockedSlotEvents().renderUnlockedSlot(graphics, lockedSlotEvents().isLockedSlot(holder().screensHoveredSlot().index), mouseX, mouseY);
            }
            // For drag sorting and/or locking slots, set the cursor to "pointing hand", like the user is grabbing onto slots to lock/select them
            if (client().misc().enhancedCursor && (client().management().dragSorting || client().lockedSlots().lockedSlots)
                    && holder().screensHoveredSlot() != null
                    && !Minecraft.getInstance().hasControlDown()
                    && Minecraft.getInstance().hasAltDown()) {
                graphics.requestCursor(CursorTypes.POINTING_HAND);
            }
        }
    }

    /**
     * Draws an enhanced cursor on the screen.
     */
    private void extractEnhancedCursor(GuiGraphicsExtractor graphics) {
        if (!client().misc().enhancedCursor) {
            return;
        }

        boolean hasHoveredSlot = holder().screensHoveredSlot() != null && holder().screensHoveredSlot().hasItem();
        if (!getCursorStack().isEmpty() || hasHoveredSlot) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }

        if (!hasHoveredSlot) {
            return;
        }

        if (Minecraft.getInstance().hasControlDown()) {
            if (ENHANCED_COOLDOWN_SWAP == 0) {
                swapCursor = !swapCursor;
                ENHANCED_COOLDOWN_SWAP = DEFAULT_ENHANCED_COOLDOWN_SWAP;
            }
            CursorType cursor;
            if (CURSOR_KEY == CursorKey.NULL) {
                cursor = swapCursor ? CursorTypes.RESIZE_NS : CursorTypes.CROSSHAIR;
                graphics.requestCursor(cursor);
            } else {
                if (CURSOR_KEY == CursorKey.MOVE || canScrollMoveAndHasScrollModifierDown()) {
                    graphics.requestCursor(CursorTypes.RESIZE_NS);
                }
                if (CURSOR_KEY == CursorKey.CROSSHAIR || hasDropOnlyOneItemModifierDown() || hasAllQuickDropModifiersDown()) {
                    graphics.requestCursor(CursorTypes.CROSSHAIR);
                }
            }
        }

        if (isContainerScreen(holder().screen()) && hasAllQuickDropModifiersDown() && !shouldButtonBeActive(false, null)) {
            graphics.requestCursor(CursorTypes.NOT_ALLOWED);
        }
    }

    /**
     * Removes any widgets after rendering, to ensure they are actually placed in the right spot for clicking.
     */
    private void removeDynamicButtonWidget(@Nullable GuiEventListener widget) {
        if (widget != null) {
            removeModWidget(holder().screen(), widget);
            holder().removeDynamicButton(widget);
        }
    }

    /**
     * Removes previously registered dynamic button widgets to prevent stale click hitboxes.
     */
    private void clearToRefreshWidgets() {
        for (GuiEventListener listener : new ArrayList<>(holder().dynamicButtons())) {
            removeDynamicButtonWidget(listener);
        }

        holder().clearDynamicButtons();
    }

    /**
     * Renders and extracts all search fields.
     */
    private void extractSearchFields(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // Render the search field
        if (holder().searchFields().container() != null) {
            holder().searchFields().container().extractWidgetRenderState(graphics, mouseX, mouseY, a);
        }

        // Render inventory search field
        if (holder().searchFields().inventory() != null) {
            // Re-position the inventory search field if the recipe book screen is open
            holder().searchFields().inventory().setX(holder().screen().width / 2 + getBarWidth(getImageWidth(holder().screen())) / 2 - (
                    !client().misc().noRecipeBookShift && holder().screen() instanceof AbstractRecipeBookScreen<?> recipeBookScreen && getRecipeBookComponent(recipeBookScreen).isVisible() ? -16 : 60
            ));
            holder().searchFields().inventory().extractWidgetRenderState(graphics, mouseX, mouseY, a);
        }
    }

    /**
     * @return whether a slot should be grayed out.
     */
    private boolean shouldGrayout(Slot slot) {
        boolean inventoryScreen = isInventoryScreen(holder().screen());
        boolean shortcutKeyReady = inventoryScreen
                ? client().sorting().sorting.any() ? hasAnyManagementModifierDown() : hasAllQuickDropModifiersDown()
                : hasAnyManagementModifierDown();
        return shortcutKeyReady
                || (!inventoryScreen && shiftHeld(false))
                || (buttonHoveredAndActive(holder().managementButtons().getTransferInventory()) && slot.hasItem())
                || buttonHoveredAndActive(holder().managementButtons().getTransferContainer())
                || (buttonHoveredAndActive(holder().managementButtons().getIncludeHotbar()) && slot.hasItem())
                || (inventoryScreen && buttonHoveredAndActive(holder().managementButtons().getSort()) && slot.hasItem())
                || (inventoryScreen && buttonHoveredAndActive(holder().managementButtons().getQuickDrop()) && slot.hasItem());
    }

    /**
     * @return if shift is held and a slot is hovered.
     */
    public boolean shiftHeld(boolean inventory) {
        int totalSlots = getTotalSlots();
        Slot hoveredSlot = getHoveredSlot(holder().screen());
        return Minecraft.getInstance().hasShiftDown()
                && (!client().management().dragSorting || !hasSelectSlotsModifierDown())
                && hoveredSlot != null
                && hoveredSlot.hasItem()
                && (inventory ? hoveredSlot.index >= totalSlots - 36 : hoveredSlot.index <= totalSlots - 37);
    }

    /**
     * Renders and extracts all buttons, including management buttons and layouts.
     */
    private void extractButtons(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        widgetEvents().initTransferContainer();

        widgetEvents().initTransferInventory();

        widgetEvents().initSort();

        widgetEvents().initFiltering();

        widgetEvents().initAlwaysQuickMove();

        widgetEvents().initIncludeHotbar();

        widgetEvents().initQuickDrop();

        widgetEvents().initSearchTransportables();

        widgetEvents().initSwap();

        widgetEvents().initClearExcludedSlots();

        widgetEvents().initBulkTrade();

        widgetEvents().initBulkCraft();

        widgetEvents().initLockInventory();

        widgetEvents().initLayout(graphics, mouseX, mouseY, a);
    }
}
package net.dillon.qualityofqueso.instance.management;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.instance.context.ManagementButtons;
import net.dillon.qualityofqueso.option.eum.management.IncludeHotbar;
import net.dillon.qualityofqueso.widget.QuesoButton;
import net.dillon.qualityofqueso.widget.WidgetLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.*;
import static net.dillon.qualityofqueso.keybind.ModKeyMappings.MOVE_TO_CONTAINER;
import static net.dillon.qualityofqueso.keybind.ModKeyMappings.MOVE_TO_INVENTORY;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles extracting and rendering events.
 */
public class ExtractingInstance extends ManagementInstance {
    private static boolean swapCursor = false;

    public ExtractingInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Draws an enhanced cursor on the screen.
     */
    public void extractEnhancedCursor(GuiGraphicsExtractor graphics) {
        if (!client().misc().enhancedCursor) {
            return;
        }

        boolean hasHoveredSlot = instance().getScreensHoveredSlot() != null && instance().getScreensHoveredSlot().hasItem();
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
                if (CURSOR_KEY == CursorKey.SCROLL || canScrollMoveAndHasScrollModifierDown()) {
                    graphics.requestCursor(CursorTypes.RESIZE_NS);
                }
                if (CURSOR_KEY == CursorKey.DROP || hasDropOnlyOneItemModifierDown() || hasAllQuickDropModifiersDown()) {
                    graphics.requestCursor(CursorTypes.CROSSHAIR);
                }
            }
        }

        if (isContainerScreen(instance().getScreen()) && hasAllQuickDropModifiersDown() && !shouldButtonBeActive(false, null)) {
            graphics.requestCursor(CursorTypes.NOT_ALLOWED);
        }
    }

    /**
     * Renders the blue "locked" overlay for locked slots (not the lock icon, the color itself)
     */
    public void extractLockedSlotColor(GuiGraphicsExtractor graphics) {
        if (!client().lockedSlots().lockedSlots || !isValidScreenForRenderingLockedSlotOverlay(instance().getScreen())) {
            return;
        }

        for (int i = 0; i < searchInstance().getSearchSlotCount(); i++) {
            Slot slot = instance().getScreenMenu().getSlot(i);
            lockedSlotsInstance().renderLockedSlot(graphics, slot, false);
        }
    }

    /**
     * Renders the process of unlocking / locking slots.
     */
    public void extractLockingUnlockingSlots(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // Only render other locked slot textures on valid screens
        if (isValidScreen(instance().getScreen())) {
            // Renders the key, or unlocked slot texture beside the mouse, indicating that the user is attempting to lock/unlock slots
            if (client().lockedSlots().lockedSlots && client().lockedSlots().showLock.inScreens() && instance().getScreensHoveredSlot() != null && instance().getExcludedSlots().isEmpty()
                    && hasLockSlotModifierDown() && !hasAnyManagementModifierDown() && !Minecraft.getInstance().hasControlDown() && !Minecraft.getInstance().hasShiftDown()) {
                lockedSlotsInstance().renderUnlockedSlot(graphics, lockedSlotsInstance().isLockedSlot(instance().getScreensHoveredSlot().index), mouseX, mouseY);
            }
            // For drag sorting and/or locking slots, set the cursor to "pointing hand", like the user is grabbing onto slots to lock/select them
            if (client().misc().enhancedCursor && (client().management().dragSorting || client().lockedSlots().lockedSlots)
                    && instance().getScreensHoveredSlot() != null
                    && !Minecraft.getInstance().hasControlDown()
                    && Minecraft.getInstance().hasAltDown()) {
                graphics.requestCursor(CursorTypes.POINTING_HAND);
            }
        }
    }

    /**
     * Grays out filtered slots (or hotbar slots), and renders the locked slot texture.
     */
    public void grayoutSlotsAndExtractLockedIcon(GuiGraphicsExtractor graphics) {
        // Begin iterating slots to gray out
        boolean inventorySearchFieldPresent = instance().getSearchFields().inventory() != null;
        boolean validScreen = isValidScreen(instance().getScreen());
        for (int i = 0; i < searchInstance().getSearchSlotCount(); i++) {
            Slot slot = instance().getScreenMenu().getSlot(i);

            // Gray out hotbar slots if include hotbar is off and one of the transfer buttons are hovered
            boolean alreadyExcluded = false;
            // Only do this on valid screens
            if (validScreen) {
                if (searchInstance().isFilteredBySearch(slot, inventorySearchFieldPresent)) {
                    searchInstance().renderGrayedSlot(graphics, slot, false);
                    alreadyExcluded = true;
                }
                // Otherwise, gray out slots that don't match the search
                else if (!client().management().includingHotbar
                        && (isInventoryScreen(instance().getScreen()) ? isInventoryHotbarSlot(true, slot.index) : isHotbarSlot(instance().getScreenMenu().slots.size(), slot.index))
                        && (client().management().transferring.any() || client().management().scrollMoving)) {
                    if (shouldGrayout(slot)) {
                        searchInstance().renderGrayedSlot(graphics, slot, slot.hasItem());
                        alreadyExcluded = true;
                    }
                }
            }
            // Renders the lock texture on locked slots (yes, the lock icon itself, not the color)
            if (isValidScreenForRenderingLockedSlotOverlay(instance().getScreen()) && client().lockedSlots().showLock.inScreens() && client().lockedSlots().lockedSlots && instance().getSearchFields().searchText().isEmpty() && instance().getExcludedSlots().isEmpty()) {
                lockedSlotsInstance().renderLockedSlot(graphics, slot, true);
            }

            // Return out for further code if not valid screen
            if (!validScreen) {
                continue;
            }

            // Gray out player-chosen excluded slots
            if (!alreadyExcluded && client().management().dragSorting) {
                for (int id : instance().getExcludedSlots()) {
                    if (slot.index == id) {
                        boolean renderUnavailable = true;

                        if (isContainerScreen(instance().getScreen())) {
                            // Don't grayout if CTRL is pressed and transfer keys are bounded
                            if (client().management().transferring.any()
                                    && (kumaAnyModifierDown(MOVE_TO_INVENTORY) || kumaAnyModifierDown(MOVE_TO_CONTAINER))
                                    && MOVE_TO_INVENTORY.getBinding().key().getValue() != InputConstants.UNKNOWN.getValue()
                                    && MOVE_TO_CONTAINER.getBinding().key().getValue() != InputConstants.UNKNOWN.getValue()) {
                                renderUnavailable = false;
                            } else if (buttonHoveredActiveOrShiftHeld(this, instance().getManagementButtons().transferContainer(), false)) {
                                renderUnavailable = slot.index <= getTotalSlots() - 37;
                            } else if (buttonHoveredActiveOrShiftHeld(this, instance().getManagementButtons().transferInventory(), true)) {
                                renderUnavailable = slot.index >= getTotalSlots() - 36;
                            }
                        }
                        // Above is complex. We must deteremine the size to search to grayout, so that the correct slots are grayed out at the correct position

                        // If a slot was found as "unavailable", render it as unavailable
                        if (renderUnavailable) {
                            searchInstance().renderGrayedSlot(graphics, slot, false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes any widgets after rendering, to ensure they are actually placed in the right spot for clicking.
     */
    private void removeDynamicButtonWidget(@Nullable GuiEventListener widget) {
        if (widget != null) {
            removeModWidget(instance().getScreen(), widget);
        }
    }

    /**
     * Removes previously registered dynamic button widgets to prevent stale click hitboxes.
     */
    public void clearWidgets() {
        for (QuesoButton button : instance().getManagementButtons().all()) {
            removeDynamicButtonWidget(button);
        }
        instance().getManagementButtons().clear();
    }

    /**
     * Renders and extracts all search fields.
     */
    public void extractSearchFields(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // Render the search field
        if (instance().getSearchFields().container() != null) {
            instance().getSearchFields().container().extractWidgetRenderState(graphics, mouseX, mouseY, a);
        }

        // Render inventory search field
        if (instance().getSearchFields().inventory() != null) {
            // Re-position the inventory search field if the recipe book screen is open
            instance().getSearchFields().inventory().setX(instance().getScreen().width / 2 + getBarWidth(getImageWidth(instance().getScreen())) / 2 - (
                    !client().misc().noRecipeBookShift && instance().getScreen() instanceof AbstractRecipeBookScreen<?> recipeBookScreen && getRecipeBookComponent(recipeBookScreen).isVisible() ? -16 : 60
            ));
            instance().getSearchFields().inventory().extractWidgetRenderState(graphics, mouseX, mouseY, a);
        }
    }

    /**
     * @return whether a slot should be grayed out.
     */
    public boolean shouldGrayout(Slot slot) {
        boolean inventoryScreen = isInventoryScreen(instance().getScreen());
        boolean shortcutKeyReady = inventoryScreen
                ? client().sorting().sorting.any() ? hasAnyManagementModifierDown() : hasAllQuickDropModifiersDown()
                : hasAnyManagementModifierDown();
        return shortcutKeyReady
                || (!inventoryScreen && shiftHeld(false))
                || (buttonHoveredAndActive(instance().getManagementButtons().transferInventory()) && slot.hasItem())
                || buttonHoveredAndActive(instance().getManagementButtons().transferContainer())
                || (buttonHoveredAndActive(instance().getManagementButtons().includeHotbar()) && slot.hasItem())
                || (inventoryScreen && buttonHoveredAndActive(instance().getManagementButtons().sort()) && slot.hasItem())
                || (inventoryScreen && buttonHoveredAndActive(instance().getManagementButtons().quickDrop()) && slot.hasItem());
    }

    /**
     * @return if shift is held and a slot is hovered.
     */
    public boolean shiftHeld(boolean inventory) {
        int totalSlots = getTotalSlots();
        Slot hoveredSlot = getHoveredSlot(instance().getScreen());
        return Minecraft.getInstance().hasShiftDown()
                && (!client().management().dragSorting || !hasSelectSlotsModifierDown())
                && hoveredSlot != null
                && hoveredSlot.hasItem()
                && (inventory ? hoveredSlot.index >= totalSlots - 36 : hoveredSlot.index <= totalSlots - 37);
    }

    /**
     * Renders and extracts all buttons, including management buttons and layouts.
     */
    public void extractButtons(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // Create variables to reference what screens certain buttons should be added to
        boolean containerScreen = isContainerScreen(instance().getScreen());
        boolean inventoryScreen = isInventoryScreen(instance().getScreen());
        boolean validScreen = containerScreen || inventoryScreen;
        boolean dropperDispenserOrHopperScreen = isDropperDispenserOrHopperScreen(instance().getScreen());
        boolean brewingOrFurnaceScreen = isBrewingOrFurnaceScreen(instance().getScreen());
        boolean merchantScreen = isMerchantScreen(instance().getScreen());
        boolean craftingScreen = isCraftingScreen(instance().getScreen());

        // Begin initializing buttons, starting with the transfer buttons
        if (client().management().transferring.buttonOrKey()) {

            if (containerScreen || dropperDispenserOrHopperScreen || brewingOrFurnaceScreen) {
                // TRANSFER CONTAINER BUTTON (container -> inventory)
                widgetHandler().getManagementButtons().initTransferContainer(
                        widgetHandlerInstance().addWidget(widgetHandlerInstance().createTransferContainer()));

                if (!brewingOrFurnaceScreen) {
                    // TRANSFER INVENTORY BUTTON (inventory -> container)
                    widgetHandler().getManagementButtons().initTransferInventory(
                            widgetHandlerInstance().addWidget(widgetHandlerInstance().createTransferInventory()));
                }
            }
        }

        // Only add these buttons in container, inventory, hopper and dropper screens
        if (validScreen || dropperDispenserOrHopperScreen) {
            if (client().sorting().sorting.buttonOrKey()) {
                // SORT BUTTON
                widgetHandler().getManagementButtons().initSort(
                        widgetHandlerInstance().addWidget(widgetHandlerInstance().createSort()));
            }

            if (!inventoryScreen
                    && (ContainerHelper.isTrackedFilteringActive() || !client().buttonDisplayOptions().displayFiltering.filteredContainersOnly()
                    && ((containerScreen || dropperDispenserOrHopperScreen) && client().management().transferring.any()))) {
                // MOVE MATCHING ITEMS BUTTON
                widgetHandler().getManagementButtons().initFiltering(
                        widgetHandlerInstance().addWidget(widgetHandlerInstance().createFiltering()));
            }
        }

        // Continue initializing buttons
        if (isValidScreenForSingularMoving(instance().getScreen(), true)) {
            if (client().buttonDisplayOptions().displayAlwaysQuickMove) {
                // ALWAYS QUICK MOVE BUTTON
                widgetHandler().getManagementButtons().initAlwaysQuickMove(widgetHandlerInstance().addWidget(widgetHandlerInstance().createAlwaysQuickMove()));
            }
        }

        // Only initialize these buttons in container/inventory screens
        if (validScreen) {
            if (containerScreen && client().management().transferring.any() || client().management().quickDrop.any()) {
                boolean canDisplayIncludeHotbarButton = client().buttonDisplayOptions().displayIncludeHotbar != IncludeHotbar.OFF
                        && (!inventoryScreen || !client().buttonDisplayOptions().displayIncludeHotbar.containerScreensOnly());

                if (canDisplayIncludeHotbarButton) {
                    // INCLUDE HOTBAR BUTTON
                    widgetHandler().getManagementButtons().initIncludeHotbar(widgetHandlerInstance().addWidget(widgetHandlerInstance().createIncludeHotbar()));
                }
            }

            if (client().management().quickDrop.buttonOrKey() || (client().management().quickDrop.any() && hasAllQuickDropModifiersDown())) {
                // QUICK DROP BUTTON
                widgetHandler().getManagementButtons().initQuickDrop(
                        widgetHandlerInstance().addWidget(widgetHandlerInstance().createQuickDrop()));
            }

            if ((
                    (instance().getSearchFields().inventory() != null && !instance().getSearchFields().inventory().getValue().isEmpty())
                            || (instance().getSearchFields().container() != null && !instance().getSearchFields().container().getValue().isEmpty())
            ) && client().buttonDisplayOptions().displaySearchTransportables && ((client().searching().containerSearching && containerScreen) || (client().searching().inventorySearching && inventoryScreen))) {
                boolean canRenderTransportablesButton = false;
                for (int i = 0; i < searchInstance().getSearchSlotCount(); i++) {
                    ItemStack stack = instance().getScreenMenu().getSlot(i).getItem();
                    if (stack.is(ItemTags.SHULKER_BOXES) || stack.is(ItemTags.BUNDLES)) {
                        canRenderTransportablesButton = true;
                        break;
                    }
                }

                if (canRenderTransportablesButton) {
                    // SEARCH INSIDE TRANSPORTABLE CONTAINERS BUTTON
                    widgetHandler().getManagementButtons().initSearchTransportables(
                            widgetHandlerInstance().addWidget(widgetHandlerInstance().createSearchTransportables()));
                }
            }

            if (client().management().swapping.buttonOrKey() && containerScreen) {
                // SWAP BUTTON
                widgetHandler().getManagementButtons().initSwap(
                        widgetHandlerInstance().addWidget(widgetHandlerInstance().createSwap()));
            }

            if (client().management().dragSorting && !instance().getExcludedSlots().isEmpty()) {
                // CLEAR EXCLUDED SLOTS BUTTON
                widgetHandler().getManagementButtons().initClearExcludedSlots(
                        widgetHandlerInstance().addWidget(widgetHandlerInstance().createClearExcludedSlots()));
            }
        }

        if (client().buttonDisplayOptions().displayBulkTrade && merchantScreen) {
            // TRADE ALL BUTTON
            widgetHandler().getManagementButtons().initTradeAll(
                    widgetHandlerInstance().addWidget(widgetHandlerInstance().createTradeAll()));
        }

        if (client().buttonDisplayOptions().displayBulkCraft && (craftingScreen || inventoryScreen)) {
            // CRAFT ALL BUTTON
            widgetHandler().getManagementButtons().initBulkCraft(
                    widgetHandlerInstance().addWidget(widgetHandlerInstance().createBulkCraft()));
        }

        if (client().buttonDisplayOptions().displayLockInventory && inventoryScreen) {
            widgetHandler().getManagementButtons().initLockInventory(
                    widgetHandlerInstance().addWidget(widgetHandlerInstance().createLockInventory()));
        }

        // Finally, render the widget layout
        if (validScreen || isOtherValidScreen(instance().getScreen()) || merchantScreen || craftingScreen) {
            // Create the default, horizontal layout
            AbstractList<AbstractWidget> horizontalLayout = buttonLayoutFromButtonName(client().management().horizontalButtonLayout, instance().getManagementButtons());

            // Create the vertical layout (up-down, box beside GUI)
            AbstractList<AbstractWidget> verticalLayout = buttonLayoutFromButtonName(client().management().verticalButtonLayout, instance().getManagementButtons());

            // Construct the final layout
            AbstractList<AbstractWidget> finalLayout = client().management().layout.horizontal() ? horizontalLayout : verticalLayout;
            if (merchantScreen) {
                finalLayout = NonNullList.of(null, instance().getManagementButtons().tradeAll());
            }

            // Set and initialize the widget layout
            instance().setWidgetLayout(WidgetLayout.initializeLayout(instance().getScreen(),
                    instance().getCachedContainer(), getTopPos(instance().getScreen()), getTitleLabelY(instance().getScreen()), finalLayout
            ));
            instance().getWidgetLayout().extractRenderState(graphics, mouseX, mouseY, a);
        }
    }

    /**
     * @return the actual button list to use in-game from the user-picked button layout.
     */
    private AbstractList<AbstractWidget> buttonLayoutFromButtonName(List<String> layoutList, ManagementButtons buttons) {
        AbstractList<AbstractWidget> finalLayout = new ArrayList<>();

        for (String s : layoutList) {
            switch (s) {
                case TRANSFER_CONTAINER_BUTTON_SERIALIZED_NAME -> finalLayout.add(buttons.transferContainer());
                case TRANSFER_INVENTORY_BUTTON_SERIALIZED_NAME -> finalLayout.add(buttons.transferInventory());
                case LOCK_INVENTORY_BUTTON_SERIALIZED_NAME -> finalLayout.add(buttons.lockInventory());
                case INCLUDE_HOTBAR_BUTTON_SERIALIZED_NAME -> finalLayout.add(buttons.includeHotbar());
                case ALWAYS_QUICK_MOVE_BUTTON_SERIALIZED_NAME -> finalLayout.add(buttons.alwaysQuickMove());
                case FILTERING_BUTTON_SERIALIZED_NAME -> finalLayout.add(buttons.filtering());
                case BULK_CRAFT_BUTTON_SERIALIZED_NAME -> finalLayout.add(buttons.bulkCraft());
                case QUICK_DROP_BUTTON_SERIALIZED_NAME -> finalLayout.add(buttons.quickDrop());
                case SWAP_BUTTON_SERIALIZED_NAME -> finalLayout.add(buttons.swap());
                case SEARCH_TRANSPORTABLES_BUTTON_SERIALIZED_NAME -> finalLayout.add(buttons.searchTransportables());
                case CLEAR_EXCLUDED_SLOTS_BUTTON_SERIALIZED_NAME -> finalLayout.add(buttons.clearExcludedSlots());
                default -> finalLayout.add(buttons.sort());
            }
        }

        return finalLayout;
    }
}
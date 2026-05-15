package net.dillon.qualityofqueso.instance.management;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.option.eum.management.IncludeHotbar;
import net.dillon.qualityofqueso.widget.TransferButton;
import net.dillon.qualityofqueso.widget.layout.WidgetLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;

import static net.dillon.qualityofqueso.helper.ButtonHelper.isStackShulker;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.*;
import static net.dillon.qualityofqueso.helper.ModHelper.options;
import static net.dillon.qualityofqueso.keybind.ModKeybinds.*;

/**
 * Handles extracting and rendering events.
 */
public class ExtractingInstance extends ManagementInstance {

    public ExtractingInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Renders the locked slot tint and lock icon for a single slot during the slot render pass.
     * This keeps durability overlays visible and keeps the carried cursor stack above lock visuals.
     */
    public void renderLockedSlotOverlayAtSlot(GuiGraphics graphics, Slot slot) {
        if (!options().lockedSlots.enableLockedSlots || !isValidScreenForRenderingLockedSlotOverlay(instance().getScreen())) {
            return;
        }
        lockedSlotsInstance().renderLockedSlot(graphics, slot, false);
    }

    /**
     * Renders the process of unlocking / locking slots.
     */
    public void extractLockingUnlockingSlots(GuiGraphics graphics, int mouseX, int mouseY) {
        // Only render other locked slot textures on valid screens
        if (isValidScreen(instance().getScreen())) {
            // Renders the key, or unlocked slot texture beside the mouse, indicating that the user is attempting to lock/unlock slots
            if (options().lockedSlots.enableLockedSlots && options().lockedSlots.showLock.inScreens() && instance().getScreensHoveredSlot() != null && instance().getExcludedSlots().isEmpty()
                    && hasLockSlotModifierDown() && !hasAnyManagementModifierDown() && !Screen.hasControlDown() && !Screen.hasShiftDown()) {
                lockedSlotsInstance().renderUnlockedSlot(graphics, lockedSlotsInstance().isLockedSlot(instance().getScreensHoveredSlot().index), mouseX, mouseY);
            }
        }
    }

    /**
     * Grays out filtered slots (or hotbar slots).
     */
    public void grayoutSlotsAndExtractLockedIcon(GuiGraphics graphics, double mouseX, double mouseY) {
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
                else if (!options().management.includeHotbar
                        && (isInventoryScreen(instance().getScreen()) ? isInventoryHotbarSlot(true, slot.index) : isHotbarSlot(instance().getScreenMenu().slots.size(), slot.index))
                        && (options().management.transferring.buttonOrKeyOrKeyOnly() || options().management.singularMoving)) {
                    if (shouldGrayout(slot, mouseX, mouseY)) {
                        searchInstance().renderGrayedSlot(graphics, slot, slot.hasItem());
                        alreadyExcluded = true;
                    }
                }
            }

            // Return out for further code if not valid screen
            if (!validScreen) {
                continue;
            }

            // Gray out player-chosen excluded slots
            if (!alreadyExcluded && options().management.dragSorting) {
                for (int id : instance().getExcludedSlots()) {
                    if (slot.index == id) {
                        boolean renderUnavailable = true;

                        if (isContainerScreen(instance().getScreen())) {
                            // Don't grayout if CTRL is pressed and transfer keys are bounded
                            if (hasAnyManagementModifierDown()
                                    && key(MOVE_TO_INVENTORY).getValue() != InputConstants.UNKNOWN.getValue()
                                    && key(MOVE_TO_CONTAINER).getValue() != InputConstants.UNKNOWN.getValue()) {
                                renderUnavailable = false;
                            } else if (buttonHoveredActiveOrShiftHeld(this, instance().getManagementButtons().transferContainer(), mouseX, mouseY, false)) {
                                renderUnavailable = slot.index <= getTotalSlots() - 37;
                            } else if (buttonHoveredActiveOrShiftHeld(this, instance().getManagementButtons().transferInventory(), mouseX, mouseY, true)) {
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
     * Renders lock icons at screen-tail using container-local slot coordinates.
     */
    public void extractLockedSlotIcon(GuiGraphics graphics) {
        if (!options().lockedSlots.enableLockedSlots
                || !isValidScreenForRenderingLockedSlotOverlay(instance().getScreen())
                || !options().lockedSlots.showLock.inScreens()
                || !instance().getSearchFields().searchText().isEmpty()
                || !instance().getExcludedSlots().isEmpty()) {
            return;
        }

        graphics.pose().pushPose();
        graphics.pose().translate(getLeftPos(instance().getScreen()), getTopPos(instance().getScreen()), 0.0F);

        for (int i = 0; i < searchInstance().getSearchSlotCount(); i++) {
            Slot slot = instance().getScreenMenu().getSlot(i);
            lockedSlotsInstance().renderLockedSlot(graphics, slot, true);
        }

        graphics.pose().popPose();
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
        for (TransferButton button : instance().getManagementButtons().all()) {
            removeDynamicButtonWidget(button);
        }
        instance().getManagementButtons().clear();
    }

    /**
     * Renders and extracts all search fields.
     */
    public void extractSearchFields(GuiGraphics graphics, int mouseX, int mouseY, float a) {
        // Render the search field
        if (instance().getSearchFields().container() != null) {
            instance().getSearchFields().container().renderWidget(graphics, mouseX, mouseY, a);
        }

        // Render inventory search field
        if (instance().getSearchFields().inventory() != null) {
            // Re-position the inventory search field if the recipe book screen is open
            instance().getSearchFields().inventory().setX(instance().getScreen().width / 2 + getBarWidth(getImageWidth(instance().getScreen())) / 2 - (
                    instance().getScreen() instanceof InventoryScreen recipeBookScreen && getRecipeBookComponent(recipeBookScreen).isVisible() ? -16 : 60
            ));
            instance().getSearchFields().inventory().renderWidget(graphics, mouseX, mouseY, a);
        }
    }

    /**
     * @return whether a slot should be grayed out.
     */
    public boolean shouldGrayout(Slot slot, double mouseX, double mouseY) {
        boolean inventoryScreen = isInventoryScreen(instance().getScreen());
        boolean shortcutKeyReady = inventoryScreen
                ? options().sorting.sortingEnabled.buttonOrKeyOrKeyOnly() ? hasAnyManagementModifierDown() : hasQuickDropKeysDown()
                : hasAnyManagementModifierDown();
        return shortcutKeyReady
                || (!inventoryScreen && shiftHeld(false))
                || (buttonHoveredAndActive(instance().getManagementButtons().transferInventory(), mouseX, mouseY) && slot.hasItem())
                || buttonHoveredAndActive(instance().getManagementButtons().transferContainer(), mouseX, mouseY)
                || (buttonHoveredAndActive(instance().getManagementButtons().includeHotbar(), mouseX, mouseY) && slot.hasItem())
                || (inventoryScreen && buttonHoveredAndActive(instance().getManagementButtons().sort(), mouseX, mouseY) && slot.hasItem())
                || (inventoryScreen && buttonHoveredAndActive(instance().getManagementButtons().quickDrop(), mouseX, mouseY) && slot.hasItem());
    }

    /**
     * @return if shift is held and a slot is hovered.
     */
    public boolean shiftHeld(boolean inventory) {
        int totalSlots = getTotalSlots();
        Slot hoveredSlot = getHoveredSlot(instance().getScreen());
        return Screen.hasShiftDown()
                && (!options().management.dragSorting || !hasSelectSlotsKeyDown())
                && hoveredSlot != null
                && hoveredSlot.hasItem()
                && (inventory ? hoveredSlot.index >= totalSlots - 36 : hoveredSlot.index <= totalSlots - 37);
    }

    /**
     * Renders and extracts all buttons, including management buttons and layouts.
     */
    public void extractButtons(GuiGraphics graphics, int mouseX, int mouseY, float a) {
        // Create variables to reference what screens certain buttons should be added to
        boolean containerScreen = isContainerScreen(instance().getScreen());
        boolean inventoryScreen = isInventoryScreen(instance().getScreen());
        boolean validScreen = containerScreen || inventoryScreen;
        boolean dropperDispenserOrHopperScreen = isDropperDispenserOrHopperScreen(instance().getScreen());
        boolean brewingOrFurnaceScreen = isBrewingOrFurnaceScreen(instance().getScreen());
        boolean merchantScreen = isMerchantScreen(instance().getScreen());
        boolean craftingScreen = isCraftingScreen(instance().getScreen());

        // Begin initializing buttons, starting with the transfer buttons
        if (options().management.transferring.buttonOrKey()) {

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
            if (options().sorting.sortingEnabled.buttonOrKey()) {
                // SORT BUTTON
                widgetHandler().getManagementButtons().initSort(
                        widgetHandlerInstance().addWidget(widgetHandlerInstance().createSort()));
            }

            if (!inventoryScreen
                    && (ContainerHelper.IS_TRACKED_CONTAINER || !options().buttonDisplayOptions.displayMoveMatchingItems.filteredContainersOnly()
                    && ((containerScreen || dropperDispenserOrHopperScreen) && options().management.filtering && options().management.transferring.buttonOrKeyOrKeyOnly()))) {
                // MOVE MATCHING ITEMS BUTTON
                widgetHandler().getManagementButtons().initMoveMatchingItems(
                        widgetHandlerInstance().addWidget(widgetHandlerInstance().createMoveMatchingItems()));
            }

            if (!inventoryScreen && options().buttonDisplayOptions.displayFillStacks && options().management.transferring.buttonOrKeyOrKeyOnly()) {
                // FILL STACKS BUTTON
                widgetHandler().getManagementButtons().initFillStacks(
                        widgetHandlerInstance().addWidget(widgetHandlerInstance().createFillStacks()));
            }
        }

        // Continue initializing buttons
        if (isValidScreenForSingularMoving(instance().getScreen(), true)) {
            if (options().buttonDisplayOptions.displayAlwaysQuickMove) {
                // ALWAYS QUICK MOVE BUTTON
                widgetHandler().getManagementButtons().initAlwaysQuickMove(widgetHandlerInstance().addWidget(widgetHandlerInstance().createAlwaysQuickMove()));
            }
        }

        // Only initialize these buttons in container/inventory screens
        if (validScreen) {
            if (containerScreen && options().management.transferring.buttonOrKeyOrKeyOnly() || options().management.quickDrop.buttonOrKeyOrKeyOnly()) {
                boolean canDisplayIncludeHotbarButton = options().buttonDisplayOptions.displayIncludeHotbar != IncludeHotbar.OFF
                        && (!inventoryScreen || !options().buttonDisplayOptions.displayIncludeHotbar.containerScreensOnly());

                if (canDisplayIncludeHotbarButton) {
                    // INCLUDE HOTBAR BUTTON
                    widgetHandler().getManagementButtons().initIncludeHotbar(widgetHandlerInstance().addWidget(widgetHandlerInstance().createIncludeHotbar()));
                }
            }

            if (options().management.quickDrop.buttonOrKey() || (options().management.quickDrop.buttonOrKeyOrKeyOnly() && hasQuickDropKeysDown())) {
                // QUICK DROP BUTTON
                widgetHandler().getManagementButtons().initQuickDrop(
                        widgetHandlerInstance().addWidget(widgetHandlerInstance().createQuickDrop()));
            }

            if ((
                    (instance().getSearchFields().inventory() != null && !instance().getSearchFields().inventory().getValue().isEmpty())
                            || (instance().getSearchFields().container() != null && !instance().getSearchFields().container().getValue().isEmpty())
            ) && options().buttonDisplayOptions.displaySearchTransportables && ((options().searching.containerSearching && containerScreen) || (options().searching.inventorySearching && inventoryScreen))) {
                boolean canRenderTransportablesButton = false;
                for (int i = 0; i < searchInstance().getSearchSlotCount(); i++) {
                    ItemStack stack = instance().getScreenMenu().getSlot(i).getItem();
                    if (isStackShulker(stack)) {
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

            if (options().management.swapping.buttonOrKey() && containerScreen) {
                // SWAP BUTTON
                widgetHandler().getManagementButtons().initSwap(
                        widgetHandlerInstance().addWidget(widgetHandlerInstance().createSwap()));
            }

            if (options().management.dragSorting && !instance().getExcludedSlots().isEmpty()) {
                // CLEAR EXCLUDED SLOTS BUTTON
                widgetHandler().getManagementButtons().initClearExcludedSlots(
                        widgetHandlerInstance().addWidget(widgetHandlerInstance().createClearExcludedSlots()));
            }
        }

        if (options().buttonDisplayOptions.displayTradeAll && merchantScreen) {
            // TRADE ALL BUTTON
            widgetHandler().getManagementButtons().initTradeAll(
                    widgetHandlerInstance().addWidget(widgetHandlerInstance().createTradeAll()));
        }

        if (options().buttonDisplayOptions.displayCraftAll && (craftingScreen || inventoryScreen)) {
            // CRAFT ALL BUTTON
            widgetHandler().getManagementButtons().initCraftAll(
                    widgetHandlerInstance().addWidget(widgetHandlerInstance().createCraftAll()));
        }

        // Finally, render the widget layout
        if (validScreen || isOtherValidScreen(instance().getScreen()) || merchantScreen || craftingScreen) {
            // Create the vertical layout (up-down, box beside GUI)
            AbstractList<AbstractWidget> verticalLayout = NonNullList.of(
                    null,
                    instance().getManagementButtons().transferInventory(),
                    instance().getManagementButtons().transferContainer(),

                    instance().getManagementButtons().includeHotbar(),
                    instance().getManagementButtons().alwaysQuickMove(),

                    instance().getManagementButtons().sort(),
                    instance().getManagementButtons().craftAll(),
                    instance().getManagementButtons().moveMatchingItems(),

                    instance().getManagementButtons().quickDrop(),
                    instance().getManagementButtons().fillStacks(),

                    instance().getManagementButtons().swap(),
                    instance().getManagementButtons().searchTransportables(),

                    instance().getManagementButtons().clearExcludedSlots()
            );

            // Create the default, horizontal layout
            AbstractList<AbstractWidget> horizontalLayout = NonNullList.of(
                    null,
                    instance().getManagementButtons().transferContainer(),
                    instance().getManagementButtons().transferInventory(),

                    instance().getManagementButtons().includeHotbar(),
                    instance().getManagementButtons().alwaysQuickMove(),

                    instance().getManagementButtons().moveMatchingItems(),
                    instance().getManagementButtons().fillStacks(),

                    instance().getManagementButtons().sort(),
                    instance().getManagementButtons().craftAll(),
                    inventoryScreen ? instance().getManagementButtons().quickDrop() : instance().getManagementButtons().searchTransportables(),

                    instance().getManagementButtons().swap(),
                    inventoryScreen ? instance().getManagementButtons().searchTransportables() : instance().getManagementButtons().quickDrop(),

                    instance().getManagementButtons().clearExcludedSlots()
            );

            // Construct the final layout
            AbstractList<AbstractWidget> finalLayout = options().management.layout.horizontal() ? horizontalLayout : verticalLayout;
            if (merchantScreen) {
                finalLayout = NonNullList.of(null, instance().getManagementButtons().tradeAll());
            } else if (dropperDispenserOrHopperScreen) {
                finalLayout = NonNullList.of(null,
                        instance().getManagementButtons().transferInventory(),
                        instance().getManagementButtons().transferContainer(),
                        instance().getManagementButtons().sort(),
                        instance().getManagementButtons().moveMatchingItems(),
                        instance().getManagementButtons().alwaysQuickMove(),
                        instance().getManagementButtons().fillStacks()
                );
            }

            // Set and initialize the widget layout
            instance().setWidgetLayout(WidgetLayout.initializeLayout(instance().getScreen(),
                    instance().getCachedContainer(), getTopPos(instance().getScreen()), getTitleLabelY(instance().getScreen()), finalLayout
            ));
            instance().getWidgetLayout().render(graphics, mouseX, mouseY, a);
        }
    }
}

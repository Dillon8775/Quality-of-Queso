package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.instance.context.ManagementButtons;
import net.dillon.qualityofqueso.instance.context.SearchFields;
import net.dillon.qualityofqueso.mixin.client.screen.AbstractContainerScreenMixin;
import net.dillon.qualityofqueso.widget.layout.WidgetLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.Set;

/**
 * Exposes basic methods and variables created in {@link AbstractContainerScreenMixin}.
 */
public interface QuesoScreen {

    /**
     * @return the current instance of {@link Minecraft} that is running.
     */
    Minecraft getMinecraft();

    /**
     * @return the current instance of {@link AbstractContainerScreen}.
     */
    AbstractContainerScreen<?> getScreen();

    /**
     * @return the {@link AbstractContainerMenu} for the {@link AbstractContainerScreen}.
     */
    AbstractContainerMenu getScreenMenu();

    /**
     * @return the current inventory (or {@link Container}) for the screen.
     */
    Container getCurrentInventory();

    /**
     * @return the current {@link AbstractContainerScreen}'s hovered slot.
     */
    Slot getScreensHoveredSlot();

    /**
     * @return the set of excluded slots in the current instance of {@link AbstractContainerScreen}.
     */
    Set<Integer> getExcludedSlots();

    /**
     * @return all present search fields.

     */
    SearchFields getSearchFields();

    /**
     * @return all present management buttons.
     */
    ManagementButtons getManagementButtons();

    /**
     * Sets the cached container for a screen.
     */
    void setCachedContainer(Container container);

    /**
     * @return the cached container for a screen.
     */
    Container getCachedContainer();

    /**
     * Determines if the user can move one or drop one item out of a stack.
     */
    void setCanMoveOne(boolean value);

    /**
     * @return if the user is able to move one or drop one item out of a stack.
     */
    boolean getCanMoveOne();

    /**
     * Determines if "filtering" should be disabled when closing the screen.
     */
    void setDisableFilteringOnClose(boolean value);

    /**
     * @return if "filtering" should be disabled when closing the screen.
     */
    boolean getDisableFilteringOnClose();

    /**
     * Sets the last locked slot index for a screen.
     */
    void setLastLockedSlotIndex(int value);

    /**
     * @return the last locked slot index for a screen.
     */
    int getLastLockedSlotIndex();

    /**
     * Sets the lock drag action for a screen, either to lock or unlock.
     */
    void setLockDragAction(int value);

    /**
     * @return the lock drag action for a screen, either to lock or unlock.
     */
    int getLockDragAction();

    /**
     * Sets if the user has excluded all slots in a screen.
     */
    void setExcludedAll(boolean value);

    /**
     * @return if the user has excluded all slots in a screen.
     */
    boolean getExcludedAll();

    /**
     * Sets the {@link WidgetLayout} for a screen.
     */
    void setWidgetLayout(WidgetLayout layout);

    /**
     * @return the {@link WidgetLayout} for a screen.
     */
    WidgetLayout getWidgetLayout();
}
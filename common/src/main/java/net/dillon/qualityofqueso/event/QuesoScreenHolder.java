package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.event.context.ManagementButtons;
import net.dillon.qualityofqueso.event.context.SearchFields;
import net.dillon.qualityofqueso.event.key.CharTypedEvents;
import net.dillon.qualityofqueso.event.key.KeyPressedEvents;
import net.dillon.qualityofqueso.event.management.ExtractingEvents;
import net.dillon.qualityofqueso.event.management.SlotClickedEvents;
import net.dillon.qualityofqueso.event.mouse.MouseClickedEvents;
import net.dillon.qualityofqueso.event.mouse.MouseDraggedEvents;
import net.dillon.qualityofqueso.event.mouse.MouseReleasedEvents;
import net.dillon.qualityofqueso.event.mouse.MouseScrolledEvents;
import net.dillon.qualityofqueso.event.screen.ScreenClosedEvents;
import net.dillon.qualityofqueso.event.screen.ScreenInitializedEvents;
import net.dillon.qualityofqueso.event.screen.ScreenResizedEvents;
import net.dillon.qualityofqueso.event.screen.TooltipEvents;
import net.dillon.qualityofqueso.mixin.client.screen.AbstractContainerScreenMixin;
import net.dillon.qualityofqueso.widget.WidgetLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.List;
import java.util.Set;

/**
 * Exposes basic methods and variables created in {@link AbstractContainerScreenMixin}.
 */
public interface QuesoScreenHolder {

    /**
     * @return the current instance of {@link Minecraft} that is running.
     */
    Minecraft mc();

    /**
     * @return the current instance of {@link AbstractContainerScreen}.
     */
    AbstractContainerScreen<?> screen();

    /**
     * @return the {@link AbstractContainerMenu} for the {@link AbstractContainerScreen}.
     */
    AbstractContainerMenu menu();

    /**
     * @return the current inventory (or {@link Container}) for the screen.
     */
    Container container();

    /**
     * @return the current {@link AbstractContainerScreen}'s hovered slot.
     */
    Slot screensHoveredSlot();

    /**
     * @return the set of excluded slots in the current instance of {@link AbstractContainerScreen}.
     */
    Set<Integer> excludedSlots();

    /**
     * @return all present search fields.
     */
    SearchFields searchFields();

    /**
     * @return all present management buttons.
     */
    ManagementButtons managementButtons();

    /**
     * @return this {@code screen} as the {@link QuesoScreenHolder}.
     */
    default QuesoScreenHolder screenAsHolder() {
        return (QuesoScreenHolder) screen();
    }

    /**
     * @return the set of all events to use.
     */
    default Events events() {
        return new Events(
                new CharTypedEvents(screenAsHolder()),
                new KeyPressedEvents(screenAsHolder()),
                new ExtractingEvents(screenAsHolder()),
                new SlotClickedEvents(screenAsHolder()),
                new MouseClickedEvents(screenAsHolder()),
                new MouseDraggedEvents(screenAsHolder()),
                new MouseReleasedEvents(screenAsHolder()),
                new MouseScrolledEvents(screenAsHolder()),
                new ScreenClosedEvents(screenAsHolder()),
                new ScreenInitializedEvents(screenAsHolder()),
                new ScreenResizedEvents(screenAsHolder()),
                new TooltipEvents(screenAsHolder())
        );
    }

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

    /**
     * Sets the container search field.
     */
    void setContainerSearchField(EditBox containerSearchField);

    /**
     * Sets the inventory search field.
     */
    void setInventorySearchField(EditBox inventorySearchField);

    /**
     * @return the list of dynamic buttons.
     */
    List<GuiEventListener> dynamicButtons();

    /**
     * Removes a dynamic button.
     */
    void removeDynamicButton(GuiEventListener widget);

    /**
     * Clears all dynamic buttons.
     */
    void clearDynamicButtons();
}
package net.dillon.qualityofqueso.event;

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

/**
 * Stores all events for a {@link QuesoScreenHolder}.
 */
public record Events(
        CharTypedEvents charTyped,
        KeyPressedEvents keyPressed,
        ExtractingEvents extracting,
        SlotClickedEvents slotClicked,
        MouseClickedEvents mouseClicked,
        MouseDraggedEvents mouseDragged,
        MouseReleasedEvents mouseReleased,
        MouseScrolledEvents mouseScrolled,
        ScreenClosedEvents screenClosed,
        ScreenInitializedEvents screenInitialized,
        ScreenResizedEvents screenResized,
        TooltipEvents tooltips
) {
}
package net.dillon.qualityofqueso.event.mouse;

import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.dillon.qualityofqueso.widget.WidgetLayout;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Handles clicking on a {@link WidgetLayout}.
 */
public class WidgetBoxClickedEvents extends ManagementEvents {

    public WidgetBoxClickedEvents(QuesoScreenHolder screen) {
        super(screen);
    }

    /**
     * Determines if the widget box was clicked on.
     */
    public void handleClickingOnBox(double mouseX, double mouseY, WidgetLayout widgetLayout, CallbackInfoReturnable<Boolean> cir) {
        WidgetLayout.hasClickedOnBox(mouseX, mouseY, widgetLayout, cir);
    }
}
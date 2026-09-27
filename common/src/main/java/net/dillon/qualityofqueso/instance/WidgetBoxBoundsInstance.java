package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.dillon.qualityofqueso.widget.WidgetLayout;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Handles clicking on a {@link WidgetLayout}.
 */
public class WidgetBoxBoundsInstance extends ManagementInstance {

    public WidgetBoxBoundsInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Determines if the widget box was clicked on.
     */
    public void handleClickingOnBox(double mouseX, double mouseY, WidgetLayout widgetLayout, CallbackInfoReturnable<Boolean> cir) {
        WidgetLayout.hasClickedOnBox(mouseX, mouseY, widgetLayout, cir);
    }
}
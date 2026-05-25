package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.widget.WidgetLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Handles clicking on a {@link WidgetLayout}.
 */
public record WidgetBoxBoundsInstance(AbstractContainerScreen<?> screen) implements ModInstance {

    /**
     * Determines if the widget box was clicked on.
     */
    public void handleClickingOnBox(double mouseX, double mouseY, WidgetLayout widgetLayout, CallbackInfoReturnable<Boolean> cir) {
        WidgetLayout.hasClickedOnBox(mouseX, mouseY, widgetLayout, cir);
    }

    @Override
    public QuesoScreen instance() {
        return (QuesoScreen) this.screen;
    }
}
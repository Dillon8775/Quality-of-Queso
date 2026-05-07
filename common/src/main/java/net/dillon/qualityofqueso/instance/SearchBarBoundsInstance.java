package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.widget.gui.SearchField;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Handles clicking on the {@link SearchField}.
 */
public record SearchBarBoundsInstance(AbstractContainerScreen<?> screen) implements ModInstance {

    /**
     * Determines if the widget box was clicked on.
     */
    public void handleClickingOnBox(double mouseX, double mouseY, EditBox searchField, CallbackInfoReturnable<Boolean> cir) {
        SearchField.hasClickedOnBox(mouseX, mouseY, searchField, cir);
    }

    @Override
    public QuesoScreen instance() {
        return (QuesoScreen) this.screen;
    }
}
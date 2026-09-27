package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.dillon.qualityofqueso.widget.SearchBar;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Handles clicking on the {@link SearchBar}.
 */
public class SearchBarBoundsInstance extends ManagementInstance {

    public SearchBarBoundsInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Determines if the widget box was clicked on.
     */
    public void handleClickingOnBox(double mouseX, double mouseY, EditBox searchField, CallbackInfoReturnable<Boolean> cir) {
        SearchBar.hasClickedOnBox(mouseX, mouseY, searchField, cir);
    }
}
package net.dillon.qualityofqueso.event.mouse;

import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.dillon.qualityofqueso.widget.SearchBar;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Handles clicking on the {@link SearchBar}.
 */
public class SearchBarClickedEvents extends ManagementEvents {

    public SearchBarClickedEvents(QuesoScreenHolder screen) {
        super(screen);
    }

    /**
     * Determines if the widget box was clicked on.
     */
    public void handleClickingOnBox(double mouseX, double mouseY, EditBox searchField, CallbackInfoReturnable<Boolean> cir) {
        SearchBar.hasClickedOnBox(mouseX, mouseY, searchField, cir);
    }
}
package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.helper.MethodHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

import java.util.function.BooleanSupplier;

import static net.dillon.qualityofqueso.helper.ManagementHelper.isInventoryScreen;
import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

/**
 * Handles char typing events.
 */
public record CharTypedInstance(AbstractContainerScreen<?> screen) implements ModInstance {

    /**
     * Ensures variables and stored values aren't lost during resizing of window.
     */
    public boolean handleCharTyped(char ch, int scancode, BooleanSupplier superCharTyped) {
        if (clientOptionsInstance().getSearchingOptions().containerSearching && instance().getSearchFields().container() != null && instance().getSearchFields().container().isFocused()) {
            return instance().getSearchFields().container().charTyped(ch, scancode);
        }

        if (isInventoryScreen(screen)) {
            if (screen instanceof InventoryScreen recipeScreen
                    && getRecipeBookComponent(recipeScreen).isVisible()
                    && instance().getSearchFields().inventory() != null
                    && instance().getSearchFields().inventory().isFocused()) {
                String text = instance().getSearchFields().searchText();
                getRecipeBookComponent(recipeScreen).toggleVisibility();
                MethodHelper.refreshWidgets(screen);
                if (instance().getSearchFields().inventory() != null) {
                    instance().getSearchFields().inventory().setValue(text);
                    instance().getSearchFields().inventory().setFocused(true);
                    screen.setFocused(instance().getSearchFields().inventory());
                }
            }
        }

        return superCharTyped.getAsBoolean();
    }

    @Override
    public QuesoScreen instance() {
        return (QuesoScreen) this.screen;
    }
}

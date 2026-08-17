package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.helper.MethodHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.input.CharacterEvent;

import java.util.function.BooleanSupplier;

import static net.dillon.qualityofqueso.helper.ManagementHelper.isInventoryScreen;
import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles char typing events.
 */
public record CharTypedInstance(AbstractContainerScreen<?> screen) implements ModInstance {

    /**
     * Ensures variables and stored values aren't lost during resizing of window.
     */
    public boolean handleCharTyped(CharacterEvent event, BooleanSupplier superCharTyped) {
        if (client().searching().containerSearching && instance().getSearchFields().container() != null && instance().getSearchFields().container().isFocused()) {
            return instance().getSearchFields().container().charTyped(event);
        }

        if (isInventoryScreen(screen)) {
            if (screen instanceof AbstractRecipeBookScreen<?> recipeScreen
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

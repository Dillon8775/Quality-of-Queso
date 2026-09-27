package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.helper.MethodHelper;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.input.CharacterEvent;

import java.util.function.BooleanSupplier;

import static net.dillon.qualityofqueso.helper.ManagementHelper.isInventoryScreen;
import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles char typing events.
 */
public class CharTypedInstance extends ManagementInstance {

    public CharTypedInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Ensures variables and stored values aren't lost during resizing of window.
     */
    public boolean handleCharTyped(CharacterEvent event, BooleanSupplier superCharTyped) {
        if (client().searching().containerSearching && instance().getSearchFields().container() != null && instance().getSearchFields().container().isFocused()) {
            return instance().getSearchFields().container().charTyped(event);
        }

        if (isInventoryScreen(instance().getScreen())) {
            if (instance().getScreen() instanceof AbstractRecipeBookScreen<?> recipeScreen
                    && getRecipeBookComponent(recipeScreen).isVisible()
                    && instance().getSearchFields().inventory() != null
                    && instance().getSearchFields().inventory().isFocused()) {
                String text = instance().getSearchFields().searchText();
                getRecipeBookComponent(recipeScreen).toggleVisibility();
                MethodHelper.refreshWidgets(instance().getScreen());
                if (instance().getSearchFields().inventory() != null) {
                    instance().getSearchFields().inventory().setValue(text);
                    instance().getSearchFields().inventory().setFocused(true);
                    instance().getScreen().setFocused(instance().getSearchFields().inventory());
                }
            }
        }

        return superCharTyped.getAsBoolean();
    }
}

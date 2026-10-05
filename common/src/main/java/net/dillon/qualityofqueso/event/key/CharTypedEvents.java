package net.dillon.qualityofqueso.event.key;

import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.dillon.qualityofqueso.helper.MethodHelper;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.input.CharacterEvent;

import java.util.function.BooleanSupplier;

import static net.dillon.qualityofqueso.helper.ManagementHelper.isInventoryScreen;
import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles char typing events.
 */
public class CharTypedEvents extends ManagementEvents {

    public CharTypedEvents(QuesoScreenHolder screen) {
        super(screen);
    }

    /**
     * Ensures variables and stored values aren't lost during resizing of window.
     */
    public boolean handleCharTyped(CharacterEvent event, BooleanSupplier superCharTyped) {
        if (!modEnabled()) {
            return superCharTyped.getAsBoolean();
        }

        if (client().searching().containerSearching && holder().searchFields().container() != null && holder().searchFields().container().isFocused()) {
            return holder().searchFields().container().charTyped(event);
        }

        if (isInventoryScreen()) {
            if (holder().screen() instanceof AbstractRecipeBookScreen<?> recipeScreen
                    && getRecipeBookComponent(recipeScreen).isVisible()
                    && holder().searchFields().inventory() != null
                    && holder().searchFields().inventory().isFocused()) {
                String text = holder().searchFields().searchText();
                getRecipeBookComponent(recipeScreen).toggleVisibility();
                MethodHelper.refreshWidgets(holder().screen());
                if (holder().searchFields().inventory() != null) {
                    holder().searchFields().inventory().setValue(text);
                    holder().searchFields().inventory().setFocused(true);
                    holder().screen().setFocused(holder().searchFields().inventory());
                }
            }
        }

        return superCharTyped.getAsBoolean();
    }
}

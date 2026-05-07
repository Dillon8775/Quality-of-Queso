package net.dillon.qualityofqueso.helper;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.kuma.api.ManagedKeyMapping;
import net.dillon.qualityofqueso.mixin.client.accessor.*;
import net.dillon.qualityofqueso.mixin.main.AbstractContainerMenuAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * A class which allows for easier access of private fields and methods.
 */
public class MethodHelper {

    /**
     * @return a bounded key.
     */
    public static InputConstants.Key key(KeyMapping key) {
        return ((KeyMappingAccessor)key).getBoundKey();
    }

    /**
     * @return a {@link Kuma} key.
     */
    public static InputConstants.Key kumaKey(ManagedKeyMapping key) {
        return key.getBinding().key();
    }

    /**
     * @return if a {@link Kuma} key was pressed.
     */
    public static boolean kumaKeyPressed(ManagedKeyMapping kumaKey, KeyEvent event) {
        return kumaKey.isActiveAndMatchesKey(event);
    }

    /**
     * @return if at least one modifier on a {@link Kuma} keybinding is currently down.
     */
    public static boolean kumaAnyModifierDown(ManagedKeyMapping kumaKey) {
        KeyModifiers modifiers = kumaKey.getBinding().modifiers();

        for (KeyModifier modifier : modifiers.asList()) {
            if (modifier == KeyModifier.SHIFT && Kuma.hasShiftDown()) {
                return true;
            }
            if (modifier == KeyModifier.CONTROL && Kuma.hasControlDown()) {
                return true;
            }
            if (modifier == KeyModifier.ALT && Kuma.hasAltDown()) {
                return true;
            }
        }

        for (InputConstants.Key customModifier : modifiers.getCustomModifiers()) {
            if (Kuma.isDown(customModifier)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return if a {@link Kuma} {@code mouse key} was pressed.
     */
    public static boolean kumaMousePressed(ManagedKeyMapping kumaKey, MouseButtonEvent event) {
        return kumaKey.isActiveAndMatchesMouse(event.button());
    }

    /**
     * @return a recipe book component on a screen.
     */
    public static RecipeBookComponent<?> getRecipeBookComponent(AbstractRecipeBookScreen<?> recipeBookScreen) {
        return ((AbstractRecipeBookScreenAccessor) recipeBookScreen).getRecipeBookComponent();
    }

    /**
     * @return a search box in the recipe book component.
     */
    public static EditBox getSearchBoxInsideRecipeBook(AbstractRecipeBookScreen<?> recipeBookScreen) {
        return ((RecipeBookComponentAccessor) getRecipeBookComponent(recipeBookScreen)).getSearchBox();
    }

    /**
     * Adds a renderable widget to a screen.
     */
    public static <T extends GuiEventListener & Renderable & NarratableEntry> void addRenderableModWidget(Screen screen, T widget) {
        ((ScreenAccessor) screen).addRenderableModWidget(widget);
    }

    /**
     * Removes a widget from a screen.
     */
    public static void removeModWidget(Screen screen, GuiEventListener widget) {
        ((ScreenAccessor) screen).removeModWidget(widget);
    }

    /**
     * Refreshes the elements on the screen.
     */
    public static void refreshWidgets(AbstractContainerScreen<?> screen) {
        ((ScreenAccessor) screen).refreshWidgets();
    }

    /**
     * @return a hovered slot.
     */
    public static Slot getHoveredSlot(AbstractContainerScreen<?> screen) {
        return ((AbstractContainerScreenAccessor) screen).getHoveredSlot();
    }

    /**
     * @return the bar width for a screen.
     */
    public static int getImageWidth(AbstractContainerScreen<?> screen) {
        return ((AbstractContainerScreenAccessor) screen).getImageWidth();
    }

    /**
     * @return the top position for a container screen.
     */
    public static int getTopPos(AbstractContainerScreen<?> screen) {
        return ((AbstractContainerScreenAccessor)screen).getTopPos();
    }

    /**
     * @return the title label Y-position for a container screen.
     */
    public static int getTitleLabelY(AbstractContainerScreen<?> screen) {
        return ((AbstractContainerScreenAccessor)screen).getTitleLabelY();
    }

    /**
     * Moves an item stack using the new quick move functionality.
     */
    public static boolean moveItemStack(AbstractContainerMenu menu, ItemStack itemStack, int startSlot, int endSlot, boolean backwards) {
        return ((AbstractContainerMenuAccessor) menu).invokeMoveItemStackTo(itemStack, startSlot, endSlot, backwards);
    }

    public static void performClickSlot(AbstractContainerScreen<?> screen, Slot slot, int slotId, int buttonNum, ContainerInput containerInput) {
        ((AbstractContainerScreenAccessor)screen).performClickSlot(slot, slotId, buttonNum, containerInput);
    }

    /**
     * @return the brewing stand screen container.
     */
    public static Container brewingStand(BrewingStandScreen brewingStandScreen) {
        return ((BrewingStandMenuAccessor) brewingStandScreen.getMenu()).getBrewingStandContainer();
    }

    /**
     * @return the dispenser screen container.
     */
    public static Container dispenser(DispenserScreen dispenserScreen) {
        return ((DispenserMenuAccessor) dispenserScreen.getMenu()).getDispenserContainer();
    }

    /**
     * @return the hopper screen container.
     */
    public static Container hopper(HopperScreen hopperScreen) {
        return ((HopperMenuAccessor) hopperScreen.getMenu()).getHopperContainer();
    }

    /**
     * @return the shulker box screen container.
     */
    public static Container shulkerBox(ShulkerBoxScreen shulkerBoxScreen) {
        return ((ShulkerBoxMenuAccessor) shulkerBoxScreen.getMenu()).getShulkerBoxContainer();
    }

    /**
     * Registers a Quality of Queso debug entry.
     */
    public static void registerDebugEntry(Identifier identifier, DebugScreenEntry entry) {
        DebugScreenEntriesAccessor.invokeRegister(identifier, entry);
    }
}
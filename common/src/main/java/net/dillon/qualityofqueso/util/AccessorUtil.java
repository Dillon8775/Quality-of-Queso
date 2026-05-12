package net.dillon.qualityofqueso.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.mixin.client.accessor.*;
import net.dillon.qualityofqueso.mixin.main.AbstractContainerMenuAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * A class which allows for easier access of private fields and methods.
 */
public class AccessorUtil {

    /**
     * @return a bounded key.
     */
    public static InputConstants.Key key(KeyMapping key) {
        return ((KeyMappingAccessor) key).getBoundKey();
    }

    /**
     * @return a recipe book component on a screen.
     */
    public static RecipeBookComponent getRecipeBookComponent(InventoryScreen invScreen) {
        return invScreen.getRecipeBookComponent();
    }

    /**
     * @return a search box in the recipe book component.
     */
    public static EditBox getSearchBoxInsideRecipeBook(InventoryScreen invScreen) {
        return ((RecipeBookComponentAccessor) invScreen.getRecipeBookComponent()).getSearchBox();
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
     * Moves an item stack using the new quick move functionality.
     */
    public static boolean moveItemStack(AbstractContainerMenu menu, ItemStack itemStack, int startSlot, int endSlot, boolean backwards) {
        return ((AbstractContainerMenuAccessor) menu).invokeMoveItemStackTo(itemStack, startSlot, endSlot, backwards);
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
}
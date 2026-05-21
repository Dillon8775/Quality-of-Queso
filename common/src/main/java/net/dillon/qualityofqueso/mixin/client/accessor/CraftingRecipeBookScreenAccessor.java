package net.dillon.qualityofqueso.mixin.client.accessor;

import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingScreen.class)
public interface CraftingRecipeBookScreenAccessor {
    @Accessor("recipeBookComponent")
    RecipeBookComponent getRecipeBookComponent();
}
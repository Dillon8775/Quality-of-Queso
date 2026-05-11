package net.dillon.qualityofqueso.mixin.client.accessor;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookComponent.class)
public interface RecipeBookComponentAccessor {
    @Accessor("searchBox")
    EditBox getSearchBox();

    @Accessor("lastRecipe")
    @Nullable RecipeDisplayId getLastRecipe();
}
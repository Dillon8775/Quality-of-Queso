package net.dillon.qualityofqueso.mixin.client.hud;

import net.dillon.qualityofqueso.mixin.client.accessor.RecipeBookComponentAccessor;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.helper.ModHelper.options;

@Mixin(RecipeButton.class)
public abstract class RecipeButtonMixin extends AbstractWidget {
    @Shadow
    public abstract RecipeDisplayId getCurrentRecipe();

    public RecipeButtonMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    /**
     * Renders a selected sprite over top of a selected recipe, when bulk crafting.
     */
    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void extractSelectedRecipe(GuiGraphics graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!modEnabled(minecraft) || !options().management.bulkCraft || !options().buttonDisplayOptions.displayBulkCraft) {
            return;
        }

        if (!(minecraft.screen instanceof AbstractContainerScreen<?> recipeScreen)) {
            return;
        }

        if (!(recipeScreen instanceof AbstractRecipeBookScreen<?> recipeBookScreen)) {
            return;
        }

        RecipeDisplayId selectedRecipe = ((RecipeBookComponentAccessor) getRecipeBookComponent(recipeBookScreen)).getLastRecipe();
        if (selectedRecipe == null || !this.getCurrentRecipe().equals(selectedRecipe)) {
            return;
        }

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ModConstants.SELECTED_RECIPE, this.getX(), this.getY(), this.width, this.height);
    }
}
package net.dillon.qualityofqueso.mixin.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
    @Shadow
    protected Minecraft minecraft;
    @Shadow
    @Nullable
    public EditBox searchBox;

    /**
     * Prevents the actual screen from shifting when opening the recipe book.
     */
    @Inject(method = "updateScreenPosition", at = @At("HEAD"), cancellable = true)
    private void removeRecipeBookScreenShift(int width, int imageWidth, CallbackInfoReturnable<Integer> cir) {
        if (clientOptionsInstance().getMiscOptions().noRecipeBookShift) {
            cir.setReturnValue((width - imageWidth) / 2);
        }
    }

    /**
     * Removes bad visual effects when removing the shift from the recipe book.
     */
    @ModifyVariable(method = "initVisuals", at = @At("STORE"), index = 1)
    private int changeRecipeBookButtonsAndShitPosition(int original) {
        return clientOptionsInstance().getMiscOptions().noRecipeBookShift ? original - 77 : original;
    }

    /**
     * Renders the recipe book component properly.
     */
    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 2)
    private int changeRecipeBookBackgroundPosition(int original) {
        return clientOptionsInstance().getMiscOptions().noRecipeBookShift ? original - 77 : original;
    }

    /**
     * Updates tabs accordingly when removing the shift from the recipe book.
     */
    @ModifyArg(method = "updateTabs", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookTabButton;setPosition(II)V"), index = 0)
    private int changeRecipeBookTabButtonPosition(int original) {
        return clientOptionsInstance().getMiscOptions().noRecipeBookShift ? original - 77 : original;
    }

    /**
     * Fixes an odd bug where if the chat key is pressed, it focuses into the search field.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void cancelOutChatKey(int keycode, int scancode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        if (this.minecraft.options.keyChat.matches(keycode, scancode) && this.searchBox != null && !this.searchBox.isFocused()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
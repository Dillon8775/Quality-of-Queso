package net.dillon.qualityofqueso.mixin.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.input.KeyEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.option.ClientOptionInstances.client;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
    @Shadow
    protected Minecraft minecraft;
    @Shadow
    @Nullable
    public EditBox searchBox;

    /**
     * Updates tabs accordingly when removing the shift from the recipe book.
     */
    @ModifyArg(method = "initVisuals", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;setPosition(II)V"), index = 0)
    private int changeRecipeBookTabButtonPosition(int original) {
        return client().gui().noRecipeBookShift ? original - 77 : original;
    }

    /**
     * Fixes an odd bug where if the chat key is pressed, it focuses into the search field.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void cancelOutChatKey(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled()) {
            return;
        }

        if (this.minecraft.options.keyChat.matches(input) && this.searchBox != null && !this.searchBox.isFocused()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
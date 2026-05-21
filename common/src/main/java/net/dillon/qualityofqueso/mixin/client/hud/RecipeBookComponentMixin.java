package net.dillon.qualityofqueso.mixin.client.hud;

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
import static net.dillon.qualityofqueso.helper.ModHelper.options;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
    @Shadow
    protected Minecraft minecraft;
    @Shadow
    @Nullable
    public EditBox searchBox;
    @Shadow
    private int xOffset;
    @Shadow
    private int width;

    /**
     * Prevents the actual screen from shifting when opening the recipe book.
     */
    @Inject(method = "updateScreenPosition", at = @At("HEAD"), cancellable = true)
    private void removeRecipeBookScreenShift(int width, int imageWidth, CallbackInfoReturnable<Integer> cir) {
        if (!options().misc.shiftRecipeBook) {
            cir.setReturnValue((width - imageWidth) / 2);
        }
    }

    /**
     * The {@code X origin} position for the recipe book.
     */
    @Inject(method = "getXOrigin", at = @At("HEAD"), cancellable = true)
    private void changeRecipeBookPosition(CallbackInfoReturnable<Integer> cir) {
        if (!options().misc.shiftRecipeBook) {
            cir.setReturnValue(((this.width - 147) / 2 - this.xOffset) - 77);
        }
    }

    /**
     * Updates tabs accordingly when removing the shift from the recipe book.
     */
    @ModifyArg(method = "updateTabs", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookTabButton;setPosition(II)V"), index = 0)
    private int changeRecipeBookTabButtonPosition(int original) {
        return !options().misc.shiftRecipeBook ? original - 77 : original;
    }

    /**
     * Fixes an odd bug where if the chat key is pressed, it focuses into the search field.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void cancelOutChatKey(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        if (this.minecraft.options.keyChat.matches(input) && this.searchBox != null && !this.searchBox.isFocused()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
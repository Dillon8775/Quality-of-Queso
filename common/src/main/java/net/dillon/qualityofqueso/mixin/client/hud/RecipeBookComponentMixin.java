package net.dillon.qualityofqueso.mixin.client.hud;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
    @ModifyReturnValue(method = "updateScreenPosition", at = @At("RETURN"))
    private int removeRecipeBookScreenShift(int original, int width, int imageWidth) {
        return clientOptionsInstance().getMiscOptions().noRecipeBookShift ? (width - imageWidth) / 2 : original;
    }

    /**
     * The {@code X origin} position for the recipe book.
     */
    @ModifyReturnValue(method = "getXOrigin", at = @At("RETURN"))
    private int changeRecipeBookPosition(int original) {
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
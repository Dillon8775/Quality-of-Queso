package net.dillon.qualityofqueso.mixin.client;

import net.dillon.qualityofqueso.QualityOfQuesoClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin {
    @Shadow
    private @Nullable TextFieldWidget searchField;

    /**
     * Allows for typing anywhere to begin searching in the recipe GUI.
     */
    @Inject(method = "keyPressed", at = @At("TAIL"), cancellable = true)
    private void searchAnyways(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        boolean ignoreTyping = false;
        for (int key : QualityOfQuesoClient.disallowedKeys) {
            if (keyCode == key) {
                ignoreTyping = true;
                break;
            }
        }
        if (!ignoreTyping) {
            this.searchField.setFocused(true);
            cir.setReturnValue(true);
        }
    }
}
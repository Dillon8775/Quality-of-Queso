package net.dillon.qualityofqueso.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {
    @Shadow
    protected MinecraftClient client;
    @Shadow
    @Nullable
    public TextFieldWidget searchField;

    /**
     * Fixes an odd bug where if the chat key is pressed, it focuses into the search field.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void cancelOutChatKey(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.client.options.chatKey.matchesKey(keyCode, scanCode) && this.searchField != null && !this.searchField.isFocused()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
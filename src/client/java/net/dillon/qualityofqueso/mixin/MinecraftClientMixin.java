package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.QualityOfQuesoClient;
import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.screen.ItemFrameSearchScreen;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    /**
     * Makes the {@link ModKeybinds#OPEN_SEARCH_ITEM_FRAMES_GUI} open {@link ItemFrameSearchScreen}.
     */
    @Inject(method = "handleInputEvents", at = @At("TAIL"))
    private void handleKeyPressing(CallbackInfo ci) {
        if (QualityOfQuesoClient.options().itemFrameSearching) {
            while (ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new ItemFrameSearchScreen());
            }
        }
    }
}
package net.dillon.qualityofqueso.mixin.client;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.option.ModCommonOptions;
import net.dillon.qualityofqueso.screen.ItemFrameSearchScreen;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.keybind.ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI;
import static net.dillon.qualityofqueso.main.QoQ.modEnabled;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    /**
     * Makes the {@link ModKeybinds#OPEN_SEARCH_ITEM_FRAMES_GUI} open {@link ItemFrameSearchScreen}.
     */
    @Inject(method = "handleKeybinds", at = @At("TAIL"))
    private void handleKeyPressing(CallbackInfo ci) {
        if (modEnabled(Minecraft.getInstance()) && ModCommonOptions.ITEM_FRAME_SEARCHING.get()) {
            while (OPEN_SEARCH_ITEM_FRAMES_GUI.consumeClick()) {
                Minecraft.getInstance().setScreen(new ItemFrameSearchScreen());
            }
        }
    }
}
package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.screen.ItemFrameSearchScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.keybind.ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI;
import static net.dillon.qualityofqueso.util.ModUtil.coptions;
import static net.dillon.qualityofqueso.util.ModUtil.modEnabled;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Final
    public Options options;

    /**
     * Makes the {@link ModKeybinds#OPEN_SEARCH_ITEM_FRAMES_GUI} open {@link ItemFrameSearchScreen}.
     */
    @Inject(method = "handleKeybinds", at = @At("TAIL"))
    private void handleKeyPressing(CallbackInfo ci) {
        if (modEnabled(Minecraft.getInstance()) && coptions().itemFrameSearching) {
            while (OPEN_SEARCH_ITEM_FRAMES_GUI.consumeClick()) {
                Minecraft.getInstance().setScreen(new ItemFrameSearchScreen(null));
            }
        }
    }
}
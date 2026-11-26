package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.dillon.qualityofqueso.screen.ItemFrameSearchScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.keybind.ModKeyMappings.OPEN_ITEM_FRAME_SEARCH_GUI;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Final
    public Options options;

    /**
     * Makes the {@link ModKeyMappings#OPEN_ITEM_FRAME_SEARCH_GUI} open {@link ItemFrameSearchScreen}.
     */
    @Inject(method = "handleKeybinds", at = @At("TAIL"))
    private void handleSpecialKeyPressing(CallbackInfo ci) {
        if (!modEnabled()) {
            return;
        }

        if (ModKeyMappings.canSearchItemFrames()) {
            while (OPEN_ITEM_FRAME_SEARCH_GUI.isActiveAndDown()) {
                openScreen(new ItemFrameSearchScreen(null));
            }
        }
    }
}
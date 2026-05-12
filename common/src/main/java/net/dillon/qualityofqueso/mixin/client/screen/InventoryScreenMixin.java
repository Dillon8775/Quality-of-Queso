package net.dillon.qualityofqueso.mixin.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.util.ModUtil.options;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    /**
     * Removes the title {@code "Crafting"} text from rendering on the inventory screen.
     */
    @Inject(method = "renderLabels", at = @At("HEAD"), cancellable = true)
    private void removeTitleText(GuiGraphics graphics, int xm, int ym, CallbackInfo ci) {
        if (options().searching.inventorySearching) {
            ci.cancel();
        }
    }
}
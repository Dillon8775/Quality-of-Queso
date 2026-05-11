package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.helper.ModHelper.options;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    /**
     * Removes the title {@code "Crafting"} text from rendering on the inventory screen.
     */
    @Inject(method = "extractLabels", at = @At("HEAD"), cancellable = true)
    private void removeInventoryScreenTitleText(GuiGraphicsExtractor graphics, int xm, int ym, CallbackInfo ci) {
        if (!modEnabled(Minecraft.getInstance())) {
            return;
        }

        if (!options().searching.searchBarPosition.top() && options().searching.inventorySearching && (
                ((QuesoScreen)this).getSearchFields().inventory() != null && (((QuesoScreen)this).getSearchFields().inventory().isFocused() || !(((QuesoScreen)this).getSearchFields().searchText().isEmpty()))
        )) {
            ci.cancel();
        }
    }
}
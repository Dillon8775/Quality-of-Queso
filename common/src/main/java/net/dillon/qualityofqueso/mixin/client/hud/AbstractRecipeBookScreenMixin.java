package net.dillon.qualityofqueso.mixin.client.hud;

import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.widget.layout.WidgetLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

@Mixin(InventoryScreen.class)
public class AbstractRecipeBookScreenMixin {

    /**
     * Cancels out clicking on the widget layout for basic inventory screens.
     */
    @Inject(method = "hasClickedOutside", at = @At("HEAD"), cancellable = true)
    private void hasClickedOnBoxInInventory(double mouseX, double mouseY, int p_98847_, int p_98848_, int p_98849_, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(Minecraft.getInstance())) {
            return;
        }

        WidgetLayout layout = ((QuesoScreen) this).getWidgetLayout();
        WidgetLayout.hasClickedOnBox(mouseX, mouseY, layout, cir);
    }
}
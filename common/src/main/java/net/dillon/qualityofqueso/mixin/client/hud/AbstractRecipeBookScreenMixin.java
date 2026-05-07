package net.dillon.qualityofqueso.mixin.client.hud;

import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.widget.layout.WidgetLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

@Mixin(AbstractRecipeBookScreen.class)
public class AbstractRecipeBookScreenMixin {

    /**
     * Cancels out clicking on the widget layout for basic inventory screens.
     */
    @Inject(method = "hasClickedOutside", at = @At("HEAD"), cancellable = true)
    private void hasClickedOnBoxInInventory(double mx, double my, int xo, int yo, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(Minecraft.getInstance())) {
            return;
        }

        WidgetLayout layout = ((QuesoScreen) this).getWidgetLayout();
        WidgetLayout.hasClickedOnBox(mx, my, layout, cir);
    }
}
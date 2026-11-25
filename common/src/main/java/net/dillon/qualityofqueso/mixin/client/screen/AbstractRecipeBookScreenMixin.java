package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.widget.management.WidgetLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

@Mixin(AbstractRecipeBookScreen.class)
public class AbstractRecipeBookScreenMixin<T extends RecipeBookMenu> extends AbstractContainerScreen<T> {

    public AbstractRecipeBookScreenMixin(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    /**
     * Cancels out clicking on the widget layout for basic inventory screens.
     */
    @Inject(method = "hasClickedOutside", at = @At("HEAD"), cancellable = true)
    private void hasClickedOnBoxInInventory(double mx, double my, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled()) {
            return;
        }

        WidgetLayout layout = ((QuesoScreenHolder) this).getWidgetLayout();
        WidgetLayout.hasClickedOnBox(mx, my, layout, cir);
    }

    /**
     * Prevents the actual screen from shifting when opening the recipe book.
     */
    @Inject(method = "updateScreenPositions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;updateScreenPositions()V"))
    private void stopRecipeBookShift(CallbackInfo ci) {
        if (!client().gui().noRecipeBookShift) {
            return;
        }

        this.leftPos = (this.width - this.imageWidth) / 2;
    }
}
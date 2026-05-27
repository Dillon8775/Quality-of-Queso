package net.dillon.qualityofqueso.mixin.fabric;

import net.dillon.qualityofqueso.instance.ModTooltipInstance;
import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

@Mixin(AbstractContainerScreen.class)
public class FabricAbstractContainerScreenMixin {

    /**
     * Prevents dropping a full stack if the quick drop one of each item keybind is held down.
     */
    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(II)Z", ordinal = 2))
    private boolean preventDroppingInFull(KeyMapping original, int keycode, int scancode, int modifiers) {
        boolean bl = original.matches(keycode, scancode);
        return clientOptionsInstance().getManagementOptions().quickDrop.buttonOrKeyOrKeyOnly() ? bl && !Screen.hasShiftDown() : bl;
    }

    /**
     * Cancels out default tooltips, and renders the single moving tooltips.
     */
    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"), cancellable = true)
    private void cancelOutToRenderSingleMovingTooltips(GuiGraphics graphics, int mouseX, int mouseY, CallbackInfo ci) {
        ModTooltipInstance modTooltipInstance = new ModTooltipInstance(
                (QuesoScreen) this
        );
        modTooltipInstance.displaySingleMovingTooltips(graphics, Minecraft.getInstance().font, mouseX, mouseY, ci);
    }
}
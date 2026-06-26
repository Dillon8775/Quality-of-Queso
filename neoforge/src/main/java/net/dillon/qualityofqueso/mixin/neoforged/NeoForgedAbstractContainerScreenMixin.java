package net.dillon.qualityofqueso.mixin.neoforged;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.instance.ModTooltipInstance;
import net.dillon.qualityofqueso.instance.MouseReleaseInstance;
import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

import static net.dillon.qualityofqueso.helper.ModHelper.*;

@Mixin(AbstractContainerScreen.class)
public class NeoForgedAbstractContainerScreenMixin {

    /**
     * Utility class for NeoForged, because mappings are different here.
     */
    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isActiveAndMatches(Lcom/mojang/blaze3d/platform/InputConstants$Key;)Z", ordinal = 2))
    private boolean preventDroppingInFull(KeyMapping original, InputConstants.Key key) {
        boolean bl = original.isActiveAndMatches(key);
        return clientOptionsInstance().getManagementOptions().quickDrop.buttonOrKeyOrKeyOnly() ? bl && !Screen.hasShiftDown() : bl;
    }

    /**
     * Cancels out default tooltips, and renders the single moving tooltips.
     */
    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;Lnet/minecraft/world/item/ItemStack;II)V"), cancellable = true)
    private void cancelOutToRenderSingleMovingTooltips(GuiGraphics graphics, int mouseX, int mouseY, CallbackInfo ci) {
        ModTooltipInstance modTooltipInstance = new ModTooltipInstance(
                (QuesoScreen) this
        );
        modTooltipInstance.displaySingleMovingTooltips(graphics, Minecraft.getInstance().font, mouseX, mouseY, ci);
    }

    /**
     * Handles mouse-releasing events with the help of the {@link MouseReleaseInstance} record.
     */
    @Inject(method = "mouseReleased", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V", ordinal = 0), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void handleMouseReleased(double mouseX, double mouseY, int bl, CallbackInfoReturnable<Boolean> cir, Slot slot, int xo, int yo, boolean clickedOutside, InputConstants.Key key, int slotId, Iterator var7, Slot target) {
        if (!modEnabled(Minecraft.getInstance())) {
            return;
        }

        MouseReleaseInstance mouseReleasedInstance = new MouseReleaseInstance(
                (QuesoScreen) getCurrentScreen()
        );
        mouseReleasedInstance.disableHardLockedSlotsOnDoubleClick(slot, target, cir);
    }
}
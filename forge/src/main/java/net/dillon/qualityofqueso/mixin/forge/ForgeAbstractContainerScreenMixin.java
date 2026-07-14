package net.dillon.qualityofqueso.mixin.forge;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.instance.ModTooltipInstance;
import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

@Mixin(AbstractContainerScreen.class)
public class ForgeAbstractContainerScreenMixin {

    /**
     * @return a new {@link ManagementInstance} for {@code Forge.}
     */
    @Unique
    private ManagementInstance managementInstance() {
        return new ManagementInstance((QuesoScreen) (AbstractContainerScreen<?>)(Object)this);
    }

    /**
     * Utility class for Forge, because mappings are different here.
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
     * Always quickly moves items if the option is enabled.
     */
    @Redirect(method = {"mouseClicked", "mouseReleased"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;hasShiftDown()Z"))
    private boolean alwaysQuickMove(double mouseX, double mouseY, int idk) {
        return this.managementInstance().canQuickMove(idk);
    }
}
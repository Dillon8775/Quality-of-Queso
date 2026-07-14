package net.dillon.qualityofqueso.mixin.neoforged;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

@Mixin(AbstractContainerScreen.class)
public class NeoForgedAbstractContainerScreenMixin {

    /**
     * @return a new {@link ManagementInstance} for {@code NeoForged.}
     */
    @Unique
    private ManagementInstance managementInstance() {
        return new ManagementInstance((QuesoScreen) (AbstractContainerScreen<?>)(Object)this);
    }

    /**
     * Utility class for NeoForged, because mappings are different here.
     */
    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isActiveAndMatches(Lcom/mojang/blaze3d/platform/InputConstants$Key;)Z", ordinal = 2))
    private boolean preventDroppingInFull(KeyMapping original, InputConstants.Key key) {
        boolean bl = original.isActiveAndMatches(key);
        return clientOptionsInstance().getManagementOptions().quickDrop.buttonOrKeyOrKeyOnly() ? bl && !Minecraft.getInstance().hasShiftDown() : bl;
    }

    /**
     * Always quickly moves items if the option is enabled.
     */
    @Redirect(method = {"mouseClicked", "mouseReleased"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;hasShiftDown()Z"))
    private boolean alwaysQuickMove(double mouseX, double mouseY, int idk) {
        return this.managementInstance().canQuickMove(idk);
    }
}
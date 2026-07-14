package net.dillon.qualityofqueso.mixin.fix;

import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenFix {

    /**
     * @return a new {@link ManagementInstance} for {@code Fabric.}
     */
    @Unique
    private ManagementInstance managementInstance() {
        return new ManagementInstance((QuesoScreen) (AbstractContainerScreen<?>)(Object)this);
    }

    /**
     * Always quickly moves items if the option is enabled. This is placed here to fix a crash w/ {@code ViaFabricPlus}.
     */
    @Redirect(method = {"mouseClicked", "mouseReleased"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/MouseButtonEvent;hasShiftDown()Z"))
    private boolean alwaysQuickMove(MouseButtonEvent event) {
        return this.managementInstance().canQuickMove(event);
    }
}
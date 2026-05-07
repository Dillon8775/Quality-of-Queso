package net.dillon.qualityofqueso.mixin.neoforged;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.dillon.qualityofqueso.helper.ModHelper.options;

@Mixin(AbstractContainerScreen.class)
public class NeoForgedAbstractContainerScreenMixin {

    /**
     * Utility class for NeoForged, because mappings are different here.
     */
    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isActiveAndMatches(Lcom/mojang/blaze3d/platform/InputConstants$Key;)Z", ordinal = 2))
    private boolean preventDroppingInFull(KeyMapping original, InputConstants.Key key) {
        boolean bl = original.isActiveAndMatches(key);
        return options().management.quickDrop.buttonOrKeyOrKeyOnly() ? bl && !Minecraft.getInstance().hasShiftDown() : bl;
    }
}
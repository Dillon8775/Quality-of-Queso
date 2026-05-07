package net.dillon.qualityofqueso.mixin.fabric;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.dillon.qualityofqueso.helper.ModHelper.options;

@Mixin(AbstractContainerScreen.class)
public class FabricAbstractContainerScreenMixin {

    /**
     * Prevents dropping a full stack if the quick drop one of each item keybind is held down.
     */
    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(Lnet/minecraft/client/input/KeyEvent;)Z", ordinal = 2))
    private boolean preventDroppingInFull(KeyMapping original, KeyEvent event) {
        boolean bl = original.matches(event);
        return options().management.quickDrop.buttonOrKeyOrKeyOnly() ? bl && !Minecraft.getInstance().hasShiftDown() : bl;
    }
}
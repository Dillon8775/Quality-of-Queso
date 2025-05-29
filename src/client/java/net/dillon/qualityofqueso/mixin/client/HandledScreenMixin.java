package net.dillon.qualityofqueso.mixin.client;

import net.dillon.qualityofqueso.QuesoOptions;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {
    @Shadow @Final protected T handler;
    @Shadow
    public abstract void close();

    /**
     * Closes the screen when clicking outside of the menu.
     */
    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At("HEAD"))
    private void closeButtonOnClickOutOfBounds(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (QuesoOptions.OPTIONS.close_gui_menu_by_clicking_off && this.handler.getCursorStack().isEmpty() && button == 0 && slot == null) {
            this.close();
        }
    }
}
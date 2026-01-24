package net.dillon.qualityofqueso.mixin.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.main.QoQ.options;

@Mixin(value = {GenericContainerScreenHandler.class, ShulkerBoxScreenHandler.class})
public class ScreenHandlersMixin {

    /**
     * The new {@code Quality of Queso quick moving system}, where items move perpendicular instead of parallel.
     */
    @Inject(method = "quickMove", at = @At("HEAD"), cancellable = true)
    private void redirectQuickMove(PlayerEntity player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        if (options().perpendicularQuickMoving || !options().includeHotbar) {
            ScreenHandler handler = (ScreenHandler)(Object)this;

            Slot slot = handler.slots.get(slotIndex);
            if (slot == null || !slot.hasStack()) {
                return;
            }

            ItemStack stack = slot.getStack();
            ItemStack original = stack.copy();

            int containerSize = handler.slots.size() - 36;
            boolean allowHotbar = options().includeHotbar;
            int playerInvEnd = containerSize + (allowHotbar ? 36 : 27);

            // FROM container → player inventory (NO hotbar)
            if (slotIndex < containerSize) {
                if (!handler.insertItem(stack, containerSize, playerInvEnd, false)) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
            }
            // FROM player inventory → container
            else if (slotIndex < playerInvEnd) {
                if (!handler.insertItem(stack, 0, containerSize, false)) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
            }
            // FROM hotbar → container (allowed)
            else {
                if (!handler.insertItem(stack, 0, containerSize, false)) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
            }

            if (stack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            cir.setReturnValue(original);
        }
    }
}
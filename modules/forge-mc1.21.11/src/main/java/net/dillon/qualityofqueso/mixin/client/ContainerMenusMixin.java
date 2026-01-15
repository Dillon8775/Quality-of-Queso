package net.dillon.qualityofqueso.mixin.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.main.QoQ.options;

@Mixin(value = {ChestMenu.class, ShulkerBoxMenu.class})
public class ContainerMenusMixin {

    /**
     * The new {@code Quality of Queso quick moving system}, where items move perpendicular instead of parallel.
     */
    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void redirectQuickMove(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        if (!options().legacyQuickMove || !options().includeHotbar) {
            AbstractContainerMenu menu = (AbstractContainerMenu)(Object)this;

            Slot slot = menu.slots.get(slotIndex);
            if (slot == null || !slot.hasItem()) {
                return;
            }

            ItemStack stack = slot.getItem();
            ItemStack original = stack.copy();

            int containerSize = menu.slots.size() - 36;
            int playerInvEnd = containerSize + (!options().includeHotbar ? 27 : 36); // inventory only, no hotbar

            // FROM container → player inventory (NO hotbar)
            if (slotIndex < containerSize) {
                if (!menu.moveItemStackTo(stack, containerSize, playerInvEnd, false)) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
            }
            // FROM player inventory → container
            else if (slotIndex < playerInvEnd) {
                if (!menu.moveItemStackTo(stack, 0, containerSize, false)) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
            }
            // FROM hotbar → container (allowed)
            else {
                if (!menu.moveItemStackTo(stack, 0, containerSize, false)) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            cir.setReturnValue(original);
        }
    }
}
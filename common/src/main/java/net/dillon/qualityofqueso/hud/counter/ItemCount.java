package net.dillon.qualityofqueso.hud.counter;

import net.minecraft.world.item.ItemStack;

/**
 * Holds the result of counting an item.
 */
public record ItemCount(
        ItemStack stack,
        int count,
        boolean shouldRender,
        boolean arrowCounter,
        boolean projectileWeapon,
        boolean renderBundle,
        int animationYOffset
) {

    /**
     * Determines if the count rendered is an even stack.
     */
    public boolean evenStack() {
        return count != 0
                && count != 64
                && count % stack.getMaxStackSize() == 0;
    }
}
package net.dillon.qualityofqueso.util;

import net.minecraft.item.ItemStack;

/**
 * Utility class, for handling
 */
public class PickupHudTracker {
    private static ItemStack stack;
    private static long expireTime;

    /**
     * Handles the picking-up.
     */
    public static void onPickup(ItemStack pickedUp) {
        stack = pickedUp.copy();
        expireTime = System.currentTimeMillis() + 4000;
    }

    /**
     * @return if the stack should display.
     */
    public static ItemStack getStack() {
        if (System.currentTimeMillis() > expireTime) {
            stack = ItemStack.EMPTY;
        }
        return stack;
    }
}
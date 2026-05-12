package net.dillon.qualityofqueso.util;

import net.minecraft.world.item.ItemStack;

/**
 * Utility class, for handling
 */
public class ItemHudTracker {
    public static boolean ARROW_OUTLINE;
    private static ItemStack stack;
    private static long expireTime;

    /**
     * Handles the picking-up.
     */
    public static void setStack(ItemStack pickedUp, boolean arrowOutline) {
        stack = pickedUp.copy();
        ARROW_OUTLINE = arrowOutline;
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
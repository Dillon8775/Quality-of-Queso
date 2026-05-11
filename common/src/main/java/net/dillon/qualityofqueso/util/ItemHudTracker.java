package net.dillon.qualityofqueso.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import static net.dillon.qualityofqueso.helper.GuiHelper.getAnimationTimeInTicks;
import static net.dillon.qualityofqueso.helper.GuiHelper.getDisplayTimeInTicks;

/**
 * Utility class, for handling
 */
public class ItemHudTracker {
    public static boolean ARROW_OUTLINE;
    private static ItemStack stack = ItemStack.EMPTY;
    private static int expireTick;

    /**
     * Handles the picking-up.
     */
    public static void setStack(ItemStack pickedUpOrThrown, boolean arrowOutline) {
        stack = pickedUpOrThrown == null ? ItemStack.EMPTY : pickedUpOrThrown.copy();
        ARROW_OUTLINE = arrowOutline;
        int currentTick = getCurrentTick();
        expireTick = currentTick + getDisplayTimeInTicks();
    }

    /**
     * @return if the stack should display.
     */
    public static ItemStack getStack() {
        int currentTick = getCurrentTick();
        if (currentTick > getAnimationEndTick()) {
            stack = ItemStack.EMPTY;
        }
        if (stack == null) {
            stack = ItemStack.EMPTY;
        }
        return stack;
    }

    /**
     * @return when the tracked item should begin sliding out.
     */
    public static int getDisplayExpireTick() {
        return expireTick;
    }

    /**
     * @return if the tracked item is still in the static display window.
     */
    public static boolean isWithinDisplayWindow() {
        return getCurrentTick() <= expireTick;
    }

    /**
     * @return when the tracked item should fully disappear.
     */
    public static int getAnimationEndTick() {
        return expireTick + getAnimationTimeInTicks(Minecraft.getInstance(), true);
    }

    /**
     * @return the slide-out animation time.
     */
    public static int getAnimationTimeTicks() {
        return getAnimationTimeInTicks(Minecraft.getInstance(), true);
    }

    /**
     * @return current client tick.
     */
    private static int getCurrentTick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.player == null) {
            return 0;
        }
        return minecraft.player.tickCount;
    }
}
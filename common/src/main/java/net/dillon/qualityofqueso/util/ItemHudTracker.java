package net.dillon.qualityofqueso.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import static net.dillon.qualityofqueso.hud.ModHudElement.getAnimationTimeInTicks;
import static net.dillon.qualityofqueso.hud.ModHudElement.getDisplayTimeInTicks;

/**
 * Utility class, for handling the item counter.
 */
public class ItemHudTracker {
    private static int expireTick;
    private static ItemStack baseStack = ItemStack.EMPTY;
    private static ItemStack shotArrowStack = ItemStack.EMPTY;

    /**
     * Handles the picking-up.
     */
    public static void setBaseStack(ItemStack pickedUpOrThrown) {
        baseStack = pickedUpOrThrown == null
                ? ItemStack.EMPTY
                : pickedUpOrThrown.copy();
        calculateTick();
    }

    /**
     * Handles the picking-up.
     */
    public static void setShotStack(ItemStack shot) {
        shotArrowStack = shot == null
                ? ItemStack.EMPTY
                : shot.copy();
        calculateTick();
    }

    /**
     * @return the {@code active stack} that should display.
     */
    public static ItemStack getActiveStack() {
        ItemStack baseStack = getBaseStack();
        ItemStack shotArrowStack = getShotStack();

        // Return the active stack
        return !baseStack.isEmpty()
                ? baseStack
                : shotArrowStack;
    }

    /**
     * @return the {@code base stack} for the counter ({@code 1st} priority).
     */
    public static ItemStack getBaseStack() {
        // Return base stack passed through safe check
        baseStack = getSafeStack(baseStack);
        return baseStack;
    }

    /**
     * @return the {@code shot arrow stack} for the counter ({@code 2nd} priority).
     */
    public static ItemStack getShotStack() {
        // Return shot arrow stack passed through safe check
        shotArrowStack = getSafeStack(shotArrowStack);
        return shotArrowStack;
    }

    /**
     * @return a safe stack to display.
     */
    private static ItemStack getSafeStack(ItemStack self) {
        if (displayTimeEnded()) {
            return ItemStack.EMPTY;
        }

        if (self == null) {
            return ItemStack.EMPTY;
        }

        return self;
    }

    /**
     * @return if the animation/display time has ended for the counter.
     */
    private static boolean displayTimeEnded() {
        int currentTick = getCurrentTick();
        return currentTick > getAnimationEndTick();
    }

    /**
     * Calculates the new active tick.
     */
    private static void calculateTick() {
        int currentTick = getCurrentTick();
        expireTick = currentTick + getDisplayTimeInTicks();
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
        return expireTick + getAnimationTimeInTicks(true);
    }

    /**
     * @return the slide-out animation time.
     */
    public static int getAnimationTimeTicks() {
        return getAnimationTimeInTicks(true);
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
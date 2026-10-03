package net.dillon.qualityofqueso.hud.counter;

import net.minecraft.world.item.ItemStack;

/**
 * Holds the final state used to render the item counter.
 */
public record CounterRenderState(
        ItemStack renderStack,
        int count,
        String text,
        int textColor,
        boolean renderOverlay,
        boolean renderWarningIndicator,
        boolean renderMiniCrossbow,
        boolean renderMiniBow,
        boolean renderBundle,
        boolean infinity,
        boolean evenStack,
        int animationYOffset
) {
}
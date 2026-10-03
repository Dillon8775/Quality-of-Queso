package net.dillon.qualityofqueso.hud.counter;

import net.dillon.qualityofqueso.hud.ModHudElement;
import net.dillon.qualityofqueso.option.eum.hud.ItemCounter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.*;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.GuiHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.hud.ModHudElement.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Helper class for the item counter.
 */
public interface CounterHelper {

    /**
     * @return the {@link CounterHudElement} to use for this helper class.
     */
    CounterHudElement counter();

    /**
     * Extracts a fake arrow under certain conditions.
     */
    default void extractFakeArrow(GuiGraphicsExtractor graphics) {
        counter().extractItem(
                graphics,
                fakeArrow(),
                false
        );
    }

    /**
     * @return if stack is a projectile weapon.
     */
    default boolean isProjectileWeapon(Item item) {
        return item instanceof BowItem || item instanceof CrossbowItem;
    }

    /**
     * @return if an item and item components equal an item.
     */
    default boolean itemMatchesInventoryItem(ItemStack mainStack, ItemStack otherStack) {
        return client().itemCounter().onlyCountMatchingItems ? ItemStack.isSameItemSameComponents(mainStack, otherStack) : otherStack.is(mainStack.getItem());
    }

    /**
     * @return if the counter should count all arrows.
     */
    default boolean shouldCountAllArrows(boolean holdingProjectileWeapon, ItemStack countStack) {
        // Count all arrows if desired when holding a projectile weapon, or always show arrow counter enabled
        return (holdingProjectileWeapon || isOnlyShowArrowCounterEnabled() || isAlwaysShowArrowCounterEnabled() || shotArrow())
                && client().itemCounter().countAllArrows
                && isStackArrow(countStack)
                && !mainHandHasArrow();
    }

    /**
     * @return if the main or offhand has infinity.
     */
    default boolean mainOrOffHandHasInfinity() {
        // Automatically does not have infinity if player doesn't have normal arrow in inventory
        if (!minecraft.player.getInventory().contains(stack -> stack.is(Items.ARROW))) {
            return false;
        }

        ItemStack mainHand = getMainHandStack(minecraft.player);
        ItemStack offHand = getOffHandStack(minecraft.player);
        return hasInfinity(mainHand) || hasInfinity(offHand);
    }

    /**
     * @return if the main hand has an arrow in it.
     */
    default boolean mainHandHasArrow() {
        ItemStack mainHand = getMainHandStack(minecraft.player);
        return isStackArrow(mainHand);
    }

    /**
     * @return if the stack is an arrow.
     */
    default boolean isStackArrow(ItemStack stack) {
        return stack.is(ItemTags.ARROWS);
    }

    /**
     * @return a fake arrow stack.
     */
    default ItemStack fakeArrow() {
        return new ItemStack(Items.ARROW);
    }

    /**
     * @return if an arrow was shot.
     */
    default boolean shotArrow() {
        return isStackArrow(ItemTracker.getShotStack());
    }

    /**
     * @return if the specified item can be counted.
     */
    default boolean canCountItem(ItemStack playerHeldItem) {
        if (!client().itemCounter().itemCounter.enabled()) {
            return playerHeldItem.is(ItemTags.SHULKER_BOXES) || playerHeldItem.is(ItemTags.BUNDLES);
        }

        if (playerHeldItem.isStackable()) {
            return true;
        }

        return counter().shouldBeCountingProjectiles(playerHeldItem)
                || playerHeldItem.is(ItemTags.SHULKER_BOXES)
                || playerHeldItem.is(ItemTags.BUNDLES);
    }

    /**
     * @return the stack the count and display.
     */
    default ItemStack getCountStack(ItemStack trackedOrHeldStack) {
        ItemStack nextProjectile = counter().getNextProjectile();

        if (isOnlyShowArrowCounterEnabled()) {
            return nextProjectile.isEmpty() ? fakeArrow() : nextProjectile;
        }

        return trackedOrHeldStack;
    }

    /**
     * @return the item that should be counted.
     */
    default ItemStack getItemToCount(ItemStack playerHeldItem) {
        if (playerHeldItem.getItem() instanceof CrossbowItem) {
            // Count crossbow projectile
            return counter().getCrossbowProjectile(playerHeldItem);
        }

        if (counter().shouldBeCountingProjectiles(playerHeldItem)) {
            // Count projectile from active hand
            ItemStack projectile = counter().getNextProjectile();

            return projectile.isEmpty()
                    ? fakeArrow()
                    : projectile;
        }

        // Count held stack
        return playerHeldItem;
    }

    /**
     * @return the stack that should actually be drawn.
     */
    default ItemStack getRenderStack(ItemCount count, ItemStack trackedOrHeldStack) {
        // Get the tracker stack (from picked up and/or dropped items)
        ItemStack trackerStack = ItemTracker.getActiveStack();

        // Determine if the counter is an arrow counter but count is 0
        boolean arrowAndZero = count.count() == 0
                && count.arrowCounter()
                && isStackArrow(count.stack());

        // Return fake arrow if held bow has infinity and the next projectile is certainly a normal arrow
        if (mainOrOffHandHasInfinity() && counter().getNextProjectile().is(Items.ARROW)) {
            return fakeArrow();
        }

        if (arrowAndZero) {
            // If the tracked stack isn't an arrow but holding a projectile weapon, return a fake arrow
            if (count.projectileWeapon() && !isStackArrow(trackerStack)) {
                return fakeArrow();
            }

            if (isAlwaysShowArrowCounterEnabled() && isStackArrow(trackedOrHeldStack)) {
                return fakeArrow();
            }

            if (!trackerStack.isEmpty()) {
                return trackerStack.copy();
            }

            return fakeArrow();
        }

        ItemStack renderStack = new ItemStack(count.stack().getItem(), Math.max(1, count.count()));
        renderStack.applyComponents(count.stack().getComponents());
        return renderStack;
    }

    /**
     * @return the text displayed by the counter.
     */
    default String getCounterText(ItemCount count, boolean infinity) {
        if (isPositioningElements()) {
            return "3";
        }

        if (infinity) {
            return INFINITY_SYMBOL;
        }

        int amount = count.count();
        int maxCount = count.stack().getMaxStackSize();

        if (amount == 0) {
            return "0";
        }

        if (amount > maxCount) {
            if (client().itemCounter().itemCounter != ItemCounter.TOTAL
                    && count.evenStack()) {
                return amount / maxCount + "x " + maxCount;
            }

            if (client().itemCounter().itemCounter == ItemCounter.STACKS) {
                int fullStacks = amount / maxCount;
                int remainder = amount % maxCount;

                String additional =
                        remainder > 0
                                ? " & " + remainder
                                : "";

                return fullStacks
                        + "x"
                        + (remainder == 0 ? " " : "")
                        + maxCount
                        + additional;
            }
        }

        return String.valueOf(amount);
    }

    /**
     * @return the text color for the counter.
     */
    default int getTextColor(ItemCount count, boolean infinity) {
        // If it's not an arrow counter, always return white
        if (!count.arrowCounter()) {
            return CommonColors.WHITE;
        }

        // Get count
        int amount = count.count();

        // If colored highlighting is off, or stack count is greater than a 2-length number, return white
        if (!client().hud().coloredHighlighting
                || amount > (
                client().itemCounter().itemCounter == ItemCounter.STACKS
                        ? 64
                        : 99
        )) {
            return CommonColors.WHITE;
        }

        // White for infinity or held arrow
        if (infinity || mainHandHasArrow()) {
            return CommonColors.WHITE;
        }

        // Red for 5 or under
        if (amount < 6) {
            return CommonColors.RED;
        }

        // Soft-red for 10 or under
        if (amount < 11) {
            return CommonColors.SOFT_RED;
        }

        // Yellow for 20 or under
        if (amount < 21) {
            return CommonColors.YELLOW;
        }

        // Soft-green for 30 or under
        if (amount < 31) {
            return SOFT_GREEN;
        }

        // Otherwise just return green
        return CommonColors.GREEN;
    }

    /**
     * @return the X position of the counter.
     */
    default int getItemCounterX(String text) {
        // Calculate fixed item x
        int itemX = !counter().isLeftHanded() ? -117 : 101;

        // Increase if offhand is filled
        if (client().itemCounter().moveItemCounterOver
                && !minecraft.player.getOffhandItem().isEmpty()) {
            itemX += counter().increasedBasedOnHand(-29, false);
        }

        // Increased based on text length, manually
        if (text.length() > 4) {
            itemX += counter().increasedBasedOnHand(
                    -3 * (text.length() - 4),
                    false
            );
        }

        // Return item x with desired position
        return itemX + client().itemCounter().itemCounterPosition[0];
    }

    /**
     * Renders the crossbow overlay when charged.
     */
    default void extractCrossbow(GuiGraphicsExtractor graphics, ItemStack renderStack, int itemX, int animationYOffset) {
        drawCrossbowSprite(graphics, CROSSBOW, itemX, animationYOffset);
        drawCrossbowSprite(graphics, getCrossbowArrowSprite(renderStack), itemX, animationYOffset);
    }

    /**
     * Renders the bow overlay when charging up.
     */
    default void extractBow(GuiGraphicsExtractor graphics, int itemX, int animationYOffset) {
        drawBowSprite(graphics, getBowSprite(), itemX, animationYOffset);
        drawBowSprite(graphics, getBowArrowSprite(), itemX, animationYOffset);
        if (counter().getNextProjectile().is(Items.TIPPED_ARROW)) {
            drawBowSprite(graphics, BOW_TIPPED_ARROW_OVERLAY, itemX, animationYOffset);
        }
    }

    /**
     * Renders the bundle overlay when a count contains transportables.
     */
    default void drawBundleSprite(GuiGraphicsExtractor graphics, int itemX, int animationYOffset) {
        drawIndicatorSprite(
                graphics,
                BUNDLE,
                itemX,
                animationYOffset,
                9,
                5,
                14
        );
    }

    /**
     * Draws a part of the crossbow sprite.
     */
    default void drawCrossbowSprite(GuiGraphicsExtractor graphics, Identifier sprite, int itemX, int animationYOffset) {
        drawIndicatorSprite(
                graphics,
                sprite,
                itemX,
                animationYOffset,
                8,
                4,
                13
        );
    }

    /**
     * Draws a part of the bow sprite.
     */
    default void drawBowSprite(GuiGraphicsExtractor graphics, Identifier sprite, int itemX, int animationYOffset) {
        drawIndicatorSprite(
                graphics,
                sprite,
                itemX,
                animationYOffset,
                8,
                5,
                14
        );
    }

    /**
     * Draws a indicator sprite.
     */
    default void drawIndicatorSprite(GuiGraphicsExtractor graphics, Identifier sprite, int itemX, int animationYOffset, int x, int up, int size) {
        drawSprite(
                graphics,
                sprite,
                (getGuiWidth(graphics) + itemX) - x,
                getGuiHeight(graphics) - up
                        + animationYOffset
                        + client().itemCounter().itemCounterPosition[1],
                size,
                size
        );
    }

    /**
     * @return if the held bow item is at a specific pull time.
     */
    default boolean checkBowPullTime(float value) {
        return BowItem.getPowerForTime(ModHudElement.minecraft.player.getTicksUsingItem()) >= value;
    }

    /**
     * @return the mini bow sprite.
     */
    default Identifier getBowSprite() {
        if (checkBowPullTime(1.0F)) {
            return BOW_2;
        } else if (checkBowPullTime(0.65F)) {
            return BOW_1;
        }

        return BOW;
    }

    /**
     * @return the mini bow's arrow.
     */
    default Identifier getBowArrowSprite() {
        ItemStack nextProjectile = counter().getNextProjectile();

        if (checkBowPullTime(1.0F)) {
            return getProjectileSprite(nextProjectile, BOW_ARROW_2, BOW_TIPPED_ARROW_2, BOW_SPECTRAL_ARROW_2, BOW_ARROW_2);
        } else if (checkBowPullTime(0.65F)) {
            return getProjectileSprite(nextProjectile, BOW_ARROW_1, BOW_TIPPED_ARROW_1, BOW_SPECTRAL_ARROW_1, BOW_ARROW_1);
        }

        return getProjectileSprite(nextProjectile, BOW_ARROW, BOW_TIPPED_ARROW, BOW_SPECTRAL_ARROW, BOW_ARROW);
    }

    /**
     * @return the mini crossbow's arrow.
     */
    default Identifier getCrossbowArrowSprite(ItemStack projectile) {
        return getProjectileSprite(projectile, CROSSBOW_FIREWORK, CROSSBOW_TIPPED_ARROW, CROSSBOW_SPECTRAL_ARROW, CROSSBOW_ARROW);
    }

    /**
     * @return a sprite to use based on the projectile type.
     */
    private Identifier getProjectileSprite(ItemStack projectile, Identifier firework, Identifier tippedArrow, Identifier spectralArrow, Identifier arrow) {
        return projectile.is(Items.FIREWORK_ROCKET) ? firework
                : projectile.is(Items.TIPPED_ARROW) ? tippedArrow
                : projectile.is(Items.SPECTRAL_ARROW) ? spectralArrow
                : arrow;
    }
}
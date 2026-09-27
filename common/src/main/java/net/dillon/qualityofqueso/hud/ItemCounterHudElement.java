package net.dillon.qualityofqueso.hud;

import net.dillon.qualityofqueso.helper.EnderChestHelper;
import net.dillon.qualityofqueso.option.ContainerData;
import net.dillon.qualityofqueso.option.eum.hud.ItemCounter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.ItemContainerContents;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.GuiHelper.hasInfinity;
import static net.dillon.qualityofqueso.helper.GuiHelper.isPositioningElements;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.hud.ItemCounterHudTracker.ARROW_OUTLINE;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Holds the {@code item counter} to extract.
 */
public class ItemCounterHudElement extends ModHudElement {

    /**
     * @return if an item can be extracted, and if so, extracts the item with the counter.
     */
    private boolean extractItem(
            GuiGraphicsExtractor graphics,
            ItemStack heldStack,
            boolean trackedItem
    ) {
        ItemStack countStack = getItemToCount(heldStack);

        ItemCount count = countItem(
                countStack,
                heldStack,
                trackedItem
        );

        if (!count.shouldRender()) {
            return false;
        }

        CounterRenderState state = createRenderState(
                count,
                heldStack
        );

        renderCounter(
                graphics,
                state
        );

        return true;
    }

    /**
     * @return the item that should be counted.
     */
    private ItemStack getItemToCount(ItemStack heldStack) {
        if (heldStack.getItem() instanceof CrossbowItem) {
            return getCrossbowProjectile(heldStack);
        }

        if (shouldCountProjectile(heldStack)) {
            return getProjectileFromActiveHand();
        }

        return heldStack;
    }

    /**
     * @return the result of counting the specified item.
     */
    private ItemCount countItem(
            ItemStack countStack,
            ItemStack heldStack,
            boolean trackedItem
    ) {
        int count = 0;
        List<Integer> items = new ArrayList<>();

        if (!canCountItem(heldStack)) {
            return new ItemCount(
                    countStack,
                    0,
                    false,
                    false,
                    false,
                    0,
                    items
            );
        }

        boolean projectile = shouldCountProjectile(heldStack);
        Item projectileItem = countStack.getItem();

        NonNullList<ItemStack> inventory = NonNullList.create();

        for (int i = 0; i < minecraft.player.getInventory().getContainerSize(); i++) {
            ItemStack stack = minecraft.player.getInventory().getItem(i);

            if (!stack.isEmpty()) {
                inventory.add(stack);
            }
        }

        count += iterateThroughInventoryAndAddCount(
                inventory,
                countStack,
                projectile,
                projectileItem,
                items
        );

        if (client().itemCounter().countEnderChest) {
            count += iterateThroughPersistedEnderChestAndAddCount(
                    countStack,
                    items
            );
        }

        boolean trackedArrow =
                client().itemCounter().arrowCounter
                        && isStackArrow(countStack)
                        && (
                        projectile
                                || (
                                isStackArrow(ItemCounterHudTracker.getStack())
                                        && ARROW_OUTLINE
                        )
                );

        boolean alwaysShowArrowFallback =
                isAlwaysShowArrowCounterEnabled()
                        && isStackArrow(heldStack);

        boolean positioningElements = isPositioningElements();

        if (positioningElements) {
            count = 3;
        }

        boolean shouldRender =
                count > 0
                        || trackedArrow
                        || alwaysShowArrowFallback
                        || positioningElements;

        int animationYOffset = 0;

        if (trackedItem && !ItemCounterHudTracker.isWithinDisplayWindow()) {
            animationYOffset = getSpectatorAnimationYOffsetFromTicks(
                    minecraft.player.tickCount,
                    ItemCounterHudTracker.getDisplayExpireTick(),
                    ItemCounterHudTracker.getAnimationTimeTicks()
            );
        }

        return new ItemCount(
                countStack,
                count,
                shouldRender,
                isStackArrow(countStack) || projectile || alwaysShowArrowFallback,
                projectile,
                animationYOffset,
                items
        );
    }

    /**
     * Adds the matching items in the inventory to the count.
     */
    private int iterateThroughInventoryAndAddCount(
            NonNullList<ItemStack> inventory,
            ItemStack countStack,
            boolean holdingProjectileWeapon,
            Item projectileItem,
            List<Integer> items
    ) {
        int count = 0;

        boolean countingAllArrows = holdingProjectileWeapon
                && client().itemCounter().countAllArrows
                && isStackArrow(countStack);

        for (ItemStack invStack : inventory) {
            if (client().itemCounter().countContainers
                    && (
                    invStack.is(ItemTags.SHULKER_BOXES)
                            || invStack.is(ItemTags.BUNDLES)
            )) {
                count += iterateTransportablesAndAddCount(
                        invStack,
                        countStack,
                        items
                );

                continue;
            }

            boolean matchesProjectile = countingAllArrows
                    ? isStackArrow(invStack)
                    : invStack.is(projectileItem);

            boolean matchesNormalItem = !holdingProjectileWeapon
                    && itemMatchesInventoryItem(
                    countStack,
                    invStack
            );

            if (!matchesProjectile && !matchesNormalItem) {
                continue;
            }

            if (holdingProjectileWeapon || matchesNormalItem) {
                count += invStack.getCount();
                items.add(invStack.getCount());
            }
        }

        return count;
    }

    /**
     * Adds matching items stored inside a transportable container to the count.
     */
    private int iterateTransportablesAndAddCount(
            ItemStack invStack,
            ItemStack countStack,
            List<Integer> items
    ) {
        int count = 0;

        ItemContainerContents container = invStack.get(DataComponents.CONTAINER);
        BundleContents bundleContents = invStack.get(DataComponents.BUNDLE_CONTENTS);

        if (container != null) {
            for (ItemStackTemplate containerStack : container.nonEmptyItems()) {
                ItemStack stack = containerStack.create();

                if (itemMatchesInventoryItem(countStack, stack)) {
                    count += containerStack.count();
                    items.add(containerStack.count());
                }
            }
        }

        if (bundleContents != null) {
            for (ItemStackTemplate bundleStack : bundleContents.items()) {
                ItemStack stack = bundleStack.create();

                if (itemMatchesInventoryItem(countStack, stack)) {
                    count += bundleStack.count();
                    items.add(bundleStack.count());
                }
            }
        }

        return count;
    }

    /**
     * Adds matching items stored in the persisted Ender Chest to the count.
     */
    private int iterateThroughPersistedEnderChestAndAddCount(
            ItemStack countStack,
            List<Integer> items
    ) {
        return iterateThroughPersistedEntriesAndAddCount(
                EnderChestHelper.getPersistedEnderChestItemsForCurrentWorld(),
                countStack,
                items
        );
    }

    /**
     * Adds matching persisted items to the count.
     */
    private int iterateThroughPersistedEntriesAndAddCount(
            List<ContainerData.StoredEnderChestStack> entries,
            ItemStack countStack,
            List<Integer> items
    ) {
        int count = 0;

        if (entries == null || entries.isEmpty()) {
            return 0;
        }

        for (ContainerData.StoredEnderChestStack stored : entries) {
            if (stored == null
                    || stored.count <= 0
                    || stored.itemId == null
                    || stored.itemId.isBlank()
            ) {
                continue;
            }

            ItemStack invStack;

            try {
                Identifier identifier = Identifier.parse(stored.itemId);

                Optional<Item> item = BuiltInRegistries.ITEM.getOptional(identifier);

                if (item.isEmpty() || item.get() == Items.AIR) {
                    continue;
                }

                invStack = new ItemStack(item.get(), stored.count);
            } catch (Exception ignored) {
                continue;
            }

            if (client().itemCounter().countContainers
                    && (
                    invStack.is(ItemTags.SHULKER_BOXES)
                            || invStack.is(ItemTags.BUNDLES)
            )) {
                count += iterateThroughPersistedEntriesAndAddCount(
                        stored.containedItems,
                        countStack,
                        items
                );

                continue;
            }

            if (!invStack.is(countStack.getItem())) {
                continue;
            }

            boolean sameComponents =
                    countStack.getComponents().toString().equals(
                            stored.components == null
                                    ? ""
                                    : stored.components
                    );

            boolean matches =
                    client().itemCounter().onlyCountMatchingItems
                            ? sameComponents
                            : itemMatchesInventoryItem(
                            countStack,
                            invStack
                    );

            if (!matches) {
                continue;
            }

            count += stored.count;
            items.add(stored.count);
        }

        return count;
    }

    /**
     * @return if the specified item can be counted.
     */
    private boolean canCountItem(ItemStack heldStack) {
        if (!client().itemCounter().itemCounter.enabled()) {
            return heldStack.is(ItemTags.SHULKER_BOXES) || heldStack.is(ItemTags.BUNDLES);
        }

        if (heldStack.isStackable()) {
            return true;
        }

        return shouldCountProjectile(heldStack)
                || heldStack.is(ItemTags.SHULKER_BOXES)
                || heldStack.is(ItemTags.BUNDLES);
    }

    /**
     * @return the counter render state.
     */
    private CounterRenderState createRenderState(ItemCount count, ItemStack heldStack) {
        boolean infinity = shouldRenderInfinity(
                count,
                heldStack
        );

        String text = getCounterText(
                count,
                infinity
        );

        ItemStack renderStack = getRenderStack(
                count,
                heldStack
        );

        int textColor = getTextColor(
                count,
                infinity
        );

        boolean renderOutline = shouldRenderOutline(
                count,
                infinity
        );

        boolean renderWarning = shouldRenderWarning(
                count,
                infinity
        );

        boolean renderCrossbowProjectile = shouldRenderCrossbowProjectile(
                heldStack,
                renderStack
        );

        return new CounterRenderState(
                renderStack,
                count.count(),
                text,
                textColor,
                renderOutline,
                renderWarning,
                renderCrossbowProjectile,
                infinity,
                count.evenStack(),
                count.animationYOffset()
        );
    }

    /**
     * @return if the counter should render as infinity.
     */
    private boolean shouldRenderInfinity(ItemCount count, ItemStack heldStack) {
        return isStackArrow(count.stack())
                && count.projectileWeapon()
                && hasInfinity(heldStack)
                && getProjectileFromActiveHand().is(Items.ARROW);
    }

    /**
     * @return the stack that should actually be drawn.
     */
    private ItemStack getRenderStack(ItemCount count, ItemStack heldStack) {
        ItemStack trackerStack = ItemCounterHudTracker.getStack();

        boolean arrowAndZero = count.count() == 0
                && count.arrowCounter()
                && isStackArrow(count.stack());

        if (arrowAndZero) {
            if (count.projectileWeapon() && !isStackArrow(trackerStack)) {
                return new ItemStack(Items.ARROW);
            }

            if (isAlwaysShowArrowCounterEnabled() && isStackArrow(heldStack)) {
                return new ItemStack(Items.ARROW);
            }

            if (!trackerStack.isEmpty()) {
                return trackerStack.copy();
            }

            return new ItemStack(Items.ARROW);
        }

        ItemStack renderStack = new ItemStack(count.stack().getItem(), Math.max(1, count.count()));
        renderStack.applyComponents(count.stack().getComponents());
        return renderStack;
    }

    /**
     * @return the text displayed by the counter.
     */
    private String getCounterText(ItemCount count, boolean infinity) {
        if (isPositioningElements()) {
            return "3";
        }

        if (infinity) {
            return "∞";
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
    private int getTextColor(ItemCount count, boolean infinity) {
        if (!count.arrowCounter()) {
            return CommonColors.WHITE;
        }

        int amount = count.count();

        if (!client().hud().coloredHighlighting
                || amount > (
                client().itemCounter().itemCounter == ItemCounter.STACKS
                        ? 64
                        : 99
        )) {
            return CommonColors.WHITE;
        }

        if (infinity) {
            return CommonColors.GREEN;
        }

        if (amount < 6) {
            return CommonColors.RED;
        }

        if (amount < 11) {
            return CommonColors.SOFT_RED;
        }

        if (amount < 21) {
            return CommonColors.YELLOW;
        }

        if (amount < 31) {
            return new Color(0x94FF97).getRGB();
        }

        return CommonColors.GREEN;
    }

    /**
     * @return if the counter outline should be rendered.
     */
    private boolean shouldRenderOutline(ItemCount count, boolean infinity) {
        if (isPositioningElements()) {
            return true;
        }

        if (!client().itemCounter().arrowCounter
                || infinity
                || !count.arrowCounter()) {
            return false;
        }

        return count.count() < (client().itemCounter().itemCounter == ItemCounter.STACKS
                ? 65
                : 100
        );
    }

    /**
     * @return if the warning indicator should be rendered.
     */
    private boolean shouldRenderWarning(ItemCount count, boolean infinity) {
        if (isPositioningElements()) {
            return true;
        }

        if (!client().hud().warningIndicators || infinity || !count.arrowCounter()) {
            return false;
        }

        return count.count() < 6;
    }

    /**
     * @return if the crossbow projectile indicator should be rendered.
     */
    private boolean shouldRenderCrossbowProjectile(ItemStack heldStack, ItemStack renderStack) {
        return heldStack.getItem() instanceof CrossbowItem
                && CrossbowItem.isCharged(heldStack)
                && !renderStack.isEmpty();
    }

    /**
     * Renders the final counter state.
     */
    private void renderCounter(GuiGraphicsExtractor graphics, CounterRenderState state) {
        int itemX = getItemCounterX(state.text());
        int animationYOffset = state.animationYOffset();

        if (state.renderOutline()) {
            renderCounterBackground(
                    graphics,
                    itemX,
                    animationYOffset,
                    state.count()
            );
        }

        if (state.renderCrossbowProjectile()) {
            renderCrossbowProjectile(
                    graphics,
                    state.renderStack(),
                    itemX,
                    animationYOffset
            );
        }

        drawItem(
                graphics,
                state.renderStack(),
                itemX,
                client().itemCounter().itemCounterPosition[1],
                false,
                animationYOffset
        );

        if (state.renderWarning()) {
            renderWarningIndicator(
                    graphics,
                    0,
                    itemX,
                    null,
                    animationYOffset
                            + client().itemCounter().itemCounterPosition[1]
            );
        }

        int textWidth = minecraft.font.width(state.text());
        int textX = itemX - (textWidth / 2) + 11;

        if (state.text().length() == 1) {
            textX += 3;
        }

        graphics.text(
                minecraft.font,
                state.text(),
                (graphics.guiWidth() / 2) + textX,
                graphics.guiHeight()
                        - (state.infinity() ? 9 : 10)
                        + animationYOffset
                        + client().itemCounter().itemCounterPosition[1],
                state.textColor(),
                true
        );

        if (client().itemCounter().displayTotalWithStacks
                && state.count() > 64
                &&
                (
                        state.evenStack() || client().itemCounter().itemCounter == ItemCounter.STACKS
                )
        ) {
            graphics.text(
                    minecraft.font,
                    "(" + String.format("%,d", state.count()) + ")",
                    (graphics.guiWidth() / 2) + textX,
                    graphics.guiHeight()
                            - 22
                            + animationYOffset,
                    state.textColor(),
                    true
            );
        }
    }

    /**
     * @return the X position of the counter.
     */
    private int getItemCounterX(String text) {
        int itemX = !isLeftHanded() ? -117 : 101;

        if (client().itemCounter().moveItemCounterOver
                && !minecraft.player.getOffhandItem().isEmpty()) {
            itemX += increasedBasedOnHand(-29, false);
        }

        if (text.length() > 4) {
            itemX += increasedBasedOnHand(
                    -3 * (text.length() - 4),
                    false
            );
        }

        return itemX + client().itemCounter().itemCounterPosition[0];
    }

    /**
     * Renders the counter background.
     */
    private void renderCounterBackground(GuiGraphicsExtractor graphics, int itemX, int animationYOffset, int count) {
        int x = getGuiWidth(graphics) + itemX;

        // Render the background sprite
        drawSprite(
                graphics,
                ModHudElement.HOTBAR_OFFHAND_SPRITE,
                x - 10,
                getGuiHeight(graphics) - 3
                        + animationYOffset
                        + client().itemCounter().itemCounterPosition[1],
                29,
                24
        );

        // Get the highlighted color (if any)
        Identifier sprite = HOTBAR_SELECTION_SPRITE;
        if (client().hud().coloredHighlighting) {
            sprite = count < 11
                    ? SLOT_CRITICAL
                    : count < 21
                    ? SLOT_AVERAGE
                    : count < 31
                    ? SLOT_DECENT
                    : SLOT_GOOD;
        }

        // Then render the outline sprite
        drawSprite(
                graphics,
                sprite,
                x - 4,
                getGuiHeight(graphics) - 3
                        + animationYOffset
                        + client().itemCounter().itemCounterPosition[1],
                24,
                23
        );
    }

    /**
     * @return if the arrow count can be displayed at all.
     */
    private boolean shouldCountProjectile(ItemStack stack) {
        return client().itemCounter().arrowCounter
                && !minecraft.player.isCreative()
                &&
                (
                        stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem
                );
    }

    /**
     * @return the projectile currently selected by the player.
     */
    private ItemStack getProjectileFromActiveHand() {
        ItemStack offHandItem = minecraft.player.getOffhandItem();

        if (isProjectileWeapon(offHandItem.getItem())) {
            return minecraft.player.getProjectile(offHandItem);
        }

        return minecraft.player.getProjectile(
                minecraft.player.getMainHandItem()
        );
    }

    /**
     * @return if stack is a projectile weapon.
     */
    private boolean isProjectileWeapon(Item item) {
        return item instanceof BowItem || item instanceof CrossbowItem;
    }

    /**
     * @return if an item and item components equal an item.
     */
    private boolean itemMatchesInventoryItem(ItemStack mainStack, ItemStack otherStack) {
        return client().itemCounter().onlyCountMatchingItems ? ItemStack.isSameItemSameComponents(mainStack, otherStack) : otherStack.is(mainStack.getItem());
    }

    /**
     * @return if the stack is an arrow.
     */
    private boolean isStackArrow(ItemStack stack) {
        return stack.is(ItemTags.ARROWS);
    }

    /**
     * @return the projectile loaded into a crossbow.
     */
    private ItemStack getCrossbowProjectile(ItemStack crossbow) {
        // Get projectile from charged crossbow
        if (CrossbowItem.isCharged(crossbow)) {
            ChargedProjectiles projectiles = crossbow.get(DataComponents.CHARGED_PROJECTILES);

            if (projectiles != null) {
                // Create the list of projectiles
                List<ItemStack> items = projectiles.itemCopies().toList();

                if (!items.isEmpty()) {
                    // Get the first projectile in the list, and return if not empty
                    ItemStack projectile = items.getFirst();
                    if (!projectile.isEmpty()) {
                        return projectile.copy();
                    }
                }
            }
        }

        // An uncharged crossbow can use fireworks from the offhand
        // Return the offhand projectile if firework
        ItemStack offHand = minecraft.player.getOffhandItem();
        if (offHand.is(Items.FIREWORK_ROCKET)) {
            return offHand.copy();
        }

        // Otherwise, get the projectile from active hand and return it
        ItemStack projectile = getProjectileFromActiveHand();
        return projectile.isEmpty()
                ? new ItemStack(Items.ARROW)
                : projectile.copy();
    }

    /**
     * Renders the correct projectile when holding a crossbow.
     */
    private void renderCrossbowProjectile(GuiGraphicsExtractor graphics, ItemStack renderStack, int itemX, int animationYOffset) {
        // Get the correct sprite
        Identifier sprite = isStackArrow(renderStack)
                ? MINI_CROSSBOW
                : MINI_CROSSBOW_FIREWORK;

        // Then draw the sprite
        drawSprite(
                graphics,
                sprite,
                (getGuiWidth(graphics) + itemX) - 8,
                getGuiHeight(graphics) - 2
                        + animationYOffset
                        + client().itemCounter().itemCounterPosition[1],
                13,
                13
        );
    }

    /**
     * Extracts the item counter.
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics) {
        // Stop item counter rendering, and render a basic counter for positioning elements
        if (isPositioningElements()) {
            extractItem(graphics, new ItemStack(Items.ARROW), false);
            return;
        }

        ItemStack mainHand = minecraft.player.getMainHandItem();
        ItemStack offHand = minecraft.player.getOffhandItem();
        ItemStack trackedStack = ItemCounterHudTracker.getStack();

        // Try render main hand item
        if (extractItem(graphics, mainHand, false)) {
            return;
        }

        // Try render offhand item
        if (extractItem(graphics, offHand, false)) {
            return;
        }

        // Try render tracked stack
        if (!trackedStack.isEmpty()) {
            extractItem(
                    graphics,
                    trackedStack,
                    true
            );
        }
    }

    /**
     * Holds the result of counting an item.
     */
    private record ItemCount(
            ItemStack stack,
            int count,
            boolean shouldRender,
            boolean arrowCounter,
            boolean projectileWeapon,
            int animationYOffset,
            List<Integer> items
    ) {

        private boolean evenStack() {
            return count != 0
                    && count != 64
                    && count % stack.getMaxStackSize() == 0;
        }
    }

    /**
     * Holds the final state used to render the item counter.
     */
    private record CounterRenderState(
            ItemStack renderStack,
            int count,
            String text,
            int textColor,
            boolean renderOutline,
            boolean renderWarning,
            boolean renderCrossbowProjectile,
            boolean infinity,
            boolean evenStack,
            int animationYOffset
    ) {
    }
}
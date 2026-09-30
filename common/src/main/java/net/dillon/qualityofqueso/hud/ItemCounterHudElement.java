package net.dillon.qualityofqueso.hud;

import net.dillon.qualityofqueso.helper.EnderChestHelper;
import net.dillon.qualityofqueso.option.ContainerData;
import net.dillon.qualityofqueso.option.eum.hud.ItemCounter;
import net.dillon.qualityofqueso.util.ItemHudTracker;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.GuiHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Holds the {@code item counter} to extract.
 */
public class ItemCounterHudElement extends ModHudElement {

    /**
     * Extracts the item counter.
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics) {
        // Stop item counter rendering, and render a basic counter for positioning elements
        if (isPositioningElements()) {
            extractFakeArrow(graphics);
            return;
        }

        ItemStack mainHand = minecraft.player.getMainHandItem();
        ItemStack offHand = minecraft.player.getOffhandItem();
        ItemStack activeStack = ItemHudTracker.getActiveStack();

        // Try render main hand item
        if (extractItem(graphics, mainHand, false)) {
            return;
        }

        // Try render offhand item
        if (extractItem(graphics, offHand, false)) {
            return;
        }

        // Try render active stack
        if (!activeStack.isEmpty()) {
            extractItem(
                    graphics,
                    activeStack,
                    true
            );
            return;
        }

        // Always render item counter as the last resort if desired
        if (isAlwaysShowArrowCounterEnabled()) {
            extractFakeArrow(graphics);
        }
    }

    /**
     * Extracts a fake arrow under certain conditions.
     */
    private void extractFakeArrow(GuiGraphicsExtractor graphics) {
        extractItem(
                graphics,
                fakeArrow(),
                false
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
     * @return a fake arrow stack.
     */
    private ItemStack fakeArrow() {
        return new ItemStack(Items.ARROW);
    }

    /**
     * @return if an item can be extracted, and if so, extracts the item with the counter.
     */
    private boolean extractItem(
            GuiGraphicsExtractor graphics,
            ItemStack heldStack,
            boolean trackedItem
    ) {
        // Create the stack to count
        ItemStack actualStack = isOnlyShowArrowCounterEnabled()
                ? fakeArrow()
                : heldStack;
        ItemStack countStack = getItemToCount(actualStack);

        // Create the count
        ItemCount count = countItem(
                countStack,
                heldStack,
                trackedItem
        );

        // Stop if can't render
        if (!count.shouldRender()) {
            return false;
        }

        // Create the counter render state
        CounterRenderState state = createRenderState(
                count,
                heldStack
        );

        // Render the counter
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
            // Count crossbow projectile
            return getCrossbowProjectile(heldStack);
        }

        if (shouldCountProjectile(heldStack)) {
            // Count projectile from active hand
            return getProjectileFromActiveHand();
        }

        // Count held stack
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
        // Create the total count and list of items to count
        int count = 0;
        List<Integer> items = new ArrayList<>();

        // If we cannot count the item, return a count of 0
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

        // Create an empty list for the inventory
        NonNullList<ItemStack> inventory = NonNullList.create();

        // Iterate through the player's inventory and add non-empty stacks to the list
        for (int i = 0; i < minecraft.player.getInventory().getContainerSize(); i++) {
            ItemStack stack = minecraft.player.getInventory().getItem(i);

            if (!stack.isEmpty()) {
                inventory.add(stack);
            }
        }

        // Determine if we are counting projectiles
        boolean projectile = shouldCountProjectile(heldStack);

        // Count all items in player inventory, which will include transportables
        count += amountInInventory(
                inventory,
                countStack,
                projectile,
                items
        );

        // Count items in ender chest
        if (client().itemCounter().countEnderChest) {
            count += inEnderChest(
                    countStack,
                    items
            );
        }

        // See if the active stack is an arrow
        boolean trackedArrow =
                client().itemCounter().arrowCounter
                        && isStackArrow(countStack)
                        &&
                        (
                                projectile || isStackArrow(ItemHudTracker.getActiveStack())
                        );

        // Create fixed count for positioning hud elements
        if (isPositioningElements()) {
            count = 3;
        }

        // Determine if arrow counter should always show, if there is nothing else to show
        boolean alwaysShowArrowFallback = isAlwaysShowArrowCounterEnabled() && isStackArrow(heldStack);

        // Can only render if count is valid
        boolean shouldRender = count > 0
                || trackedArrow
                || alwaysShowArrowFallback;

        // Create animation for the item counter once display time has expired
        int animationYOffset = 0;
        if (trackedItem && !ItemHudTracker.isWithinDisplayWindow()) {
            animationYOffset = getSpectatorAnimationYOffsetFromTicks(
                    minecraft.player.tickCount,
                    ItemHudTracker.getDisplayExpireTick(),
                    ItemHudTracker.getAnimationTimeTicks()
            );
        }

        // Return the new item count
        return new ItemCount(
                countStack,
                count,
                shouldRender,
                // Arrow counter can be valid if counting stack is an arrow, user is holding a projectile, or should always show arrow counter
                (
                        (isStackArrow(countStack) || countStack.is(Items.FIREWORK_ROCKET))
                                && !ItemHudTracker.getShotStack().isEmpty()
                ) || shouldCountProjectile(getOffHandStack(minecraft.player)) /* offhand check */ || projectile /* main hand check */ || alwaysShowArrowFallback,
                projectile,
                animationYOffset,
                items
        );
    }

    /**
     * Adds the matching items in the inventory to the count.
     */
    private int amountInInventory(
            NonNullList<ItemStack> inventory,
            ItemStack countStack,
            boolean holdingProjectileWeapon,
            List<Integer> items
    ) {
        int count = 0;

        // Count all arrows if desired when holding a projectile weapon, or always show arrow counter enabled
        boolean countingAllArrows = (holdingProjectileWeapon || isAlwaysShowArrowCounterEnabled() || isStackArrow(ItemHudTracker.getShotStack()))
                && client().itemCounter().countAllArrows
                && isStackArrow(countStack);

        // Iterate through the inventory to count
        for (ItemStack invStack : inventory) {
            if (client().itemCounter().countContainers &&
                    (
                            invStack.is(ItemTags.SHULKER_BOXES) || invStack.is(ItemTags.BUNDLES)
                    )
            ) {
                // Begin searching inside the transportables and increment count if desired
                count += amountInTransportable(
                        invStack,
                        countStack,
                        items
                );

                continue;
            }

            boolean matches = itemMatchesInventoryItem(countStack, invStack);

            // Determine if a projectile matches
            boolean matchesProjectile = countingAllArrows
                    ? isStackArrow(invStack)
                    : matches;

            // Determine if an item matches
            boolean matchesNormalItem = matches
                    && !holdingProjectileWeapon;

            // Continue iteration if nothing matches
            if (!matchesProjectile && !matchesNormalItem) {
                continue;
            }

            // Continue if held bow has infinity and inventory stack is an arrow
            if (invStack.is(Items.ARROW) && mainOrOffHandHasInfinity()) {
                continue;
            }

            // Add item to count if matches
            count += invStack.getCount();
            items.add(invStack.getCount());
        }

        return count;
    }

    /**
     * Adds matching items stored inside a transportable container to the count.
     */
    private int amountInTransportable(
            ItemStack transportable,
            ItemStack countStack,
            List<Integer> items
    ) {
        int count = 0;

        ItemContainerContents container = transportable.get(DataComponents.CONTAINER);
        BundleContents bundleContents = transportable.get(DataComponents.BUNDLE_CONTENTS);

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
    private int inEnderChest(
            ItemStack countStack,
            List<Integer> items
    ) {
        return inPersistedData(
                EnderChestHelper.getPersistedEnderChestItemsForCurrentWorld(),
                countStack,
                items
        );
    }

    /**
     * Adds matching items inside persisted data to the count.
     */
    private int inPersistedData(
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

            if (client().itemCounter().countContainers &&
                    (
                            invStack.is(ItemTags.SHULKER_BOXES) || invStack.is(ItemTags.BUNDLES)
                    )
            ) {
                count += inPersistedData(
                        stored.containedItems,
                        countStack,
                        items
                );

                continue;
            }

            if (!invStack.is(countStack.getItem())) {
                continue;
            }

            boolean sameComponents = countStack.getComponents().toString().equals(
                    stored.components == null
                            ? ""
                            : stored.components
            );

            boolean matches = client().itemCounter().onlyCountMatchingItems
                    ? sameComponents
                    : itemMatchesInventoryItem(countStack, invStack);

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

        boolean renderBowProjectile = shouldRenderBowProjectile(
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
                renderBowProjectile,
                infinity,
                count.evenStack(),
                count.animationYOffset()
        );
    }

    /**
     * @return if the counter should render as infinity.
     */
    private boolean shouldRenderInfinity(ItemCount count, ItemStack heldStack) {
        // Projectile must be an arrow to render as infinity
        return isStackArrow(count.stack())
                && count.projectileWeapon()
                && hasInfinity(heldStack)
                && getProjectileFromActiveHand().is(Items.ARROW);
    }

    /**
     * @return the stack that should actually be drawn.
     */
    private ItemStack getRenderStack(ItemCount count, ItemStack heldStack) {
        // Get the tracker stack (from picked up and/or dropped items)
        ItemStack trackerStack = ItemHudTracker.getActiveStack();

        // Determine if the counter is an arrow counter but count is 0
        boolean arrowAndZero = count.count() == 0
                && count.arrowCounter()
                && isStackArrow(count.stack());

        // Return fake arrow if held bow has infinity and the next projectile is certainly a normal arrow
        if (mainOrOffHandHasInfinity() && getProjectileFromActiveHand().is(Items.ARROW)) {
            return fakeArrow();
        }

        if (arrowAndZero) {
            // If the tracked stack isn't an arrow but holding a projectile weapon, return a fake arrow
            if (count.projectileWeapon() && !isStackArrow(trackerStack)) {
                return fakeArrow();
            }

            if (isAlwaysShowArrowCounterEnabled() && isStackArrow(heldStack)) {
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

        // White for infinity
        if (infinity) {
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
     * @return if the counter outline should be rendered.
     */
    private boolean shouldRenderOutline(ItemCount count, boolean infinity) {
        // Always render outlines if positioning elements
        if (isPositioningElements()) {
            return true;
        }

        // Must be valid arrow counter to render an outline for item counter
        if (!client().itemCounter().arrowCounter
                || infinity
                || !count.arrowCounter()) {
            return false;
        }

        // Render outline if stack count is less than a 2-length number on display
        return count.count() < (client().itemCounter().itemCounter == ItemCounter.STACKS
                ? 65
                : 100
        );
    }

    /**
     * @return if the warning indicator should be rendered.
     */
    private boolean shouldRenderWarning(ItemCount count, boolean infinity) {
        // Always render warning indicator if positioning elements
        if (isPositioningElements()) {
            return true;
        }

        // Must be valid arrow counter to render
        if (!client().hud().warningIndicators || infinity || mainOrOffHandHasInfinity() || !count.arrowCounter()) {
            return false;
        }

        // Render if count is less than 6
        return count.count() < 6;
    }

    /**
     * @return if the crossbow projectile indicator should be rendered.
     */
    private boolean shouldRenderCrossbowProjectile(ItemStack heldStack, ItemStack renderStack) {
        // Current render stack must be empty in order to render crossbow projectile
        return heldStack.getItem() instanceof CrossbowItem
                && CrossbowItem.isCharged(heldStack)
                && !renderStack.isEmpty();
    }

    /**
     * @return if the bow projectile indicator should be rendered.
     */
    private boolean shouldRenderBowProjectile(ItemStack heldStack, ItemStack renderStack) {
        // Current render stack must be empty in order to render bow projectile
        return heldStack.getItem() instanceof BowItem
                && BowItem.getPowerForTime(minecraft.player.getTicksUsingItem()) > 0.1F
                && !renderStack.isEmpty();
    }

    /**
     * Renders the final counter state.
     */
    private void renderCounter(GuiGraphicsExtractor graphics, CounterRenderState state) {
        // Get the item counter x from state's text
        int itemX = getItemCounterX(state.text());
        // Get the animation y offset from state's animation
        int animationYOffset = state.animationYOffset();

        // Render the outline for counter (includes background & colored highlighting)
        if (state.renderOutline()) {
            renderCounterBackground(
                    graphics,
                    itemX,
                    animationYOffset,
                    state.count()
            );
        }

        // Render the counter for a crossbow projectile
        if (state.renderCrossbowProjectile()) {
            renderCrossbowProjectile(
                    graphics,
                    state.renderStack(),
                    itemX,
                    animationYOffset
            );
        }

        // Render the counter for a bow projectile
        if (state.renderBowProjectile()) {
            renderBowProjectile(
                    graphics,
                    itemX,
                    animationYOffset
            );
        }

        // Draw the item at the desired position
        drawItem(
                graphics,
                state.renderStack(),
                itemX,
                client().itemCounter().itemCounterPosition[1],
                false,
                animationYOffset
        );

        // Render the warning indicator if needed
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

        // Calculate text width
        int textWidth = minecraft.font.width(state.text());
        int textX = itemX - (textWidth / 2) + 11;
        if (state.text().length() == 1) {
            textX += 3;
        }

        // Draw the text
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

        // Draw total with stacks text if desired
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
        // Calculate fixed item x
        int itemX = !isLeftHanded() ? -117 : 101;

        // Increase if offhand is filled
        if (client().itemCounter().moveItemCounterOver
                && !minecraft.player.getOffhandItem().isEmpty()) {
            itemX += increasedBasedOnHand(-29, false);
        }

        // Increased based on text length, manually
        if (text.length() > 4) {
            itemX += increasedBasedOnHand(
                    -3 * (text.length() - 4),
                    false
            );
        }

        // Return item x with desired position
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
        if (client().hud().coloredHighlighting && !mainOrOffHandHasInfinity()) {
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
                        // Must be bow or crossbow item w/ arrow counter enabled
                        // & player not creative to be able to count a projectile towards the counter
                        stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem
                );
    }

    /**
     * @return the projectile currently selected by the player.
     */
    private ItemStack getProjectileFromActiveHand() {
        ItemStack offHandItem = minecraft.player.getOffhandItem();

        // Tries to get the next projectile from offhand
        if (isProjectileWeapon(offHandItem.getItem())) {
            return minecraft.player.getProjectile(offHandItem);
        }

        // Otherwise, returns projectile in main hand
        return minecraft.player.getProjectile(
                minecraft.player.getMainHandItem()
        );
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
                ? fakeArrow()
                : projectile.copy();
    }

    /**
     * Renders the mini crossbow overlay when charged.
     */
    private void renderCrossbowProjectile(GuiGraphicsExtractor graphics, ItemStack renderStack, int itemX, int animationYOffset) {
        // Don't render if mini-bows are disabled
        if (!client().itemCounter().miniBows) {
            return;
        }

        // Get the correct sprite
        Identifier sprite = isStackArrow(renderStack)
                ? MINI_CROSSBOW
                : MINI_CROSSBOW_FIREWORK;

        // Then draw the sprite
        drawSprite(
                graphics,
                sprite,
                (getGuiWidth(graphics) + itemX) - 8,
                getGuiHeight(graphics) - 3
                        + animationYOffset
                        + client().itemCounter().itemCounterPosition[1],
                13,
                13
        );
    }

    /**
     * Renders the mini bow overlay when charging up.
     */
    private void renderBowProjectile(GuiGraphicsExtractor graphics, int itemX, int animationYOffset) {
        // Don't render if mini-bows are disabled
        if (!client().itemCounter().miniBows) {
            return;
        }

        // Get the correct sprite
        Identifier sprite = BowItem.getPowerForTime(minecraft.player.getTicksUsingItem()) == 1.0F
                ? MINI_BOW_READY
                : MINI_BOW;

        // Then draw the sprite
        drawSprite(
                graphics,
                sprite,
                (getGuiWidth(graphics) + itemX) - 8,
                getGuiHeight(graphics) - 3
                        + animationYOffset
                        + client().itemCounter().itemCounterPosition[1],
                14,
                13
        );
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
            boolean renderBowProjectile,
            boolean infinity,
            boolean evenStack,
            int animationYOffset
    ) {
    }
}
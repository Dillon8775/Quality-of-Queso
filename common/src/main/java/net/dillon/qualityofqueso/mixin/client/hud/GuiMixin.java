package net.dillon.qualityofqueso.mixin.client.hud;

import com.google.common.base.Strings;
import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.helper.DebugHudHelper;
import net.dillon.qualityofqueso.helper.EnderChestHelper;
import net.dillon.qualityofqueso.option.ContainerData;
import net.dillon.qualityofqueso.option.eum.hud.ItemCounter;
import net.dillon.qualityofqueso.util.ItemHudTracker;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static net.dillon.qualityofqueso.helper.ButtonHelper.isStackShulker;
import static net.dillon.qualityofqueso.helper.GuiHelper.*;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.util.ItemHudTracker.ARROW_OUTLINE;
import static net.dillon.qualityofqueso.util.ModConstants.*;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique
    private boolean renderingItem = false;
    @Unique
    private int lastObservedPlayerTick = Integer.MIN_VALUE;

    /**
     * Tries to render an item on the screen.
     */
    @Unique
    private void tryRenderItem(GuiGraphics graphics, ItemStack mainHandItem, ItemStack offHandItem) {
        // Get the persisted data from the player's last known ender chest
        EnderChestHelper.persistEnderChestContentsIfOpen(this.minecraft, this.minecraft.player);
        // Then get the current picked up/dropped item stack
        ItemStack pickedUpOrDroppedStack = ItemHudTracker.getStack();

        // Render only the arrow count, if onlyShowArrowCounter is on
        if (options().itemCounter.onlyShowArrowCounter) {
            this.renderingItem = this.renderItem(graphics, mainHandItem, false)
                    || this.renderItem(graphics, offHandItem, false)
                    || (!pickedUpOrDroppedStack.isEmpty() && this.renderItem(graphics, pickedUpOrDroppedStack, true));
            if (!this.renderingItem && isAlwaysShowArrowCounterEnabled(this.minecraft)) {
                this.renderingItem = this.renderItem(graphics, new ItemStack(Items.ARROW, 1), false);
            }
        } else { // Render the item counter normally
            this.renderingItem = this.renderItem(graphics, mainHandItem, false) || this.renderItem(graphics, offHandItem, false) || (!pickedUpOrDroppedStack.isEmpty() && this.renderItem(graphics, pickedUpOrDroppedStack, true));
            if (!this.renderingItem && isAlwaysShowArrowCounterEnabled(this.minecraft)) {
                this.renderingItem = this.renderItem(graphics, new ItemStack(Items.ARROW, 1), false);
            }
        }
    }

    /**
     * @return the x-pos for each "equipment slot".
     */
    @Unique
    private int getEquipmentSlotX(Minecraft minecraft, EquipmentSlot slot) {
        int base = isLeftHanded(minecraft) ? -275 : 0;
        if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
            base += increasedBasedOnHand(this.minecraft, isLeftHanded(this.minecraft) ? -22 : -24, true);
        }
        return switch (slot) {
            case HEAD -> base + 100;
            case CHEST -> base + 120;
            case LEGS -> base + 140;
            case FEET -> base + 160;
            case OFFHAND -> base + (!isLeftHanded(this.minecraft) ? -117 : 376);
            default -> 0;
        };
    }

    /**
     * @return the x-position that the slot should render under the armor item.
     */
    @Unique
    private int getArmorBarX(Minecraft minecraft, GuiGraphics graphics) {
        return getGuiWidth(graphics) + this.getEquipmentSlotX(minecraft, EquipmentSlot.HEAD) - 3;
    }

    /**
     * @return the x-position that the highlighted slot should render under the armor item.
     */
    @Unique
    private int getHighlightedSlotX(Minecraft minecraft, GuiGraphics graphics, EquipmentSlot slot) {
        return getGuiWidth(graphics) + this.getEquipmentSlotX(minecraft, slot) - 4;
    }

    /**
     * Renders the highlighted texture around a slot.
     */
    @Unique
    @Deprecated
    private void renderHighlightedArmorSlot(Minecraft minecraft, ResourceLocation defaultSprite, GuiGraphics graphics, EquipmentSlot slot, boolean warning, int yOffset, float alpha) {
        if (alpha <= 0.0F || !(options().hud.highlightArmor)) {
            return;
        }

        graphics.blit(
                warning ? SLOT_CRITICAL : getHighlightedSlotTexture(minecraft, defaultSprite, getItemBySlot(minecraft, slot)),
                this.getHighlightedSlotX(minecraft, graphics, slot),
                getGuiHeight(graphics) - 3 + yOffset,
                0.0F,
                0.0F,
                24,
                23,
                24,
                23
        );
    }

    /**
     * Renders the {@code warning texture} around armor items.
     */
    @Unique
    @Deprecated
    private void renderWarningIndicator(Minecraft minecraft, GuiGraphics graphics, int slot, int itemX, EquipmentSlot equipmentSlot, int yOffset) {
        if (!options().hud.warningIndicators) {
            return;
        }

        graphics.blit(
                new ResourceLocation("world_list/error_highlighted"),
                getGuiWidth(graphics) + (itemX != 0 ? itemX : equipmentSlot == null ? -78 + (slot * 20) : this.getEquipmentSlotX(minecraft, equipmentSlot) + 10),
                getGuiHeight(graphics) + 4 + yOffset,
                0.0F,
                0.0F,
                16,
                16,
                16,
                16
        );
    }

    /**
     * Renders lock icons on locked hotbar slots.
     */
    @Unique
    private void renderLockedHotbarSlots(GuiGraphics graphics) {
        if (!options().lockedSlots.enableLockedSlots || !options().lockedSlots.showLock.inHud() || this.minecraft.player == null) {
            return;
        }

        Set<Integer> lockedPlayerSlots = ContainerHelper.getLockedSlots(false);
        if (lockedPlayerSlots.isEmpty()) {
            return;
        }

        for (int slot = 0; slot < 9; slot++) {
            if (!lockedPlayerSlots.contains(slot)) {
                continue;
            }

            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 200.0F);
            graphics.blit(
                    ofQoQ(LOCKED_TEXTURE),
                    getGuiWidth(graphics) - 91 + (slot * 20),
                    getGuiHeight(graphics) + 11,
                    0.0F,
                    0.0F,
                    10,
                    10,
                    10,
                    10
            );
            graphics.pose().popPose();
        }
    }

    /**
     * Renders locked hotbar slots.
     */
    @Inject(method = "renderHotbar", at = @At("TAIL"))
    private void renderAllSprites(float partialTick, GuiGraphics graphics, CallbackInfo ci) {
        this.renderLockedHotbarSlots(graphics);
    }

    /**
     * Implements the {@code armor status} feature.
     */
    @Inject(method = "renderHotbar", at = @At("TAIL"))
    private void renderArmorStatusAndTryRenderItem(float partialTick, GuiGraphics graphics, CallbackInfo ci) {
        if (!modEnabled(this.minecraft) || this.minecraft.player == null) {
            return;
        }

        int tick = this.minecraft.player.tickCount;
        if (tick < this.lastObservedPlayerTick) {
            for (int timerIndex = 0; timerIndex < ARMOR_TIMERS.length; timerIndex++) {
                ARMOR_TIMERS[timerIndex] = 0;
                LAST_ARMOR_STACKS[timerIndex] = null;
            }
        }
        this.lastObservedPlayerTick = tick;
        int animationTimeTicks = getAnimationTimeInTicks(this.minecraft, false);
        int i = 0;
        for (EquipmentSlot slot : equipmentSlots()) {
            ItemStack current = getItemBySlot(this.minecraft, slot);
            if (LAST_ARMOR_STACKS[i] == null) {
                LAST_ARMOR_STACKS[i] = current.copy();
                i++;
                continue;
            }
            if (armorChanged(i, current)) {
                ARMOR_TIMERS[i] = tick + getDisplayTimeInTicks();
                LAST_ARMOR_STACKS[i] = current.copy();
            }
            i++;
        }

        boolean armorStatusOnUpdate = options().hud.armorStatus.onUpdate();
        boolean armorStatusAlways = !armorStatusOnUpdate && !options().hud.armorStatus.off();
        boolean anyArmorTimerActive = false;
        int latestArmorTimer = 0;
        for (int armorTimer : ARMOR_TIMERS) {
            if (isBeforeTick(tick, armorTimer)) {
                anyArmorTimerActive = true;
            }
            if (armorTimer > latestArmorTimer) {
                latestArmorTimer = armorTimer;
            }
        }

        boolean syncArmorAnimating = options().hud.armorHotbar
                && armorStatusOnUpdate
                && !anyArmorTimerActive
                && latestArmorTimer > 0
                && isWithinTickWindow(tick, latestArmorTimer, animationTimeTicks);
        int syncArmorAnimationYOffset = syncArmorAnimating
                ? getSpectatorAnimationYOffsetFromTicks(tick, latestArmorTimer, animationTimeTicks)
                : 0;

        CAN_ACTUALLY_RENDER_ARMOR_HOTBAR = anyArmorTimerActive;

        boolean elytraWarning = options().elytraAlarm.enableElytraAlarm.enabled() && SHOULD_WARN_OF_ELYTRA;
        boolean canRenderArmorHotbar = options().hud.armorHotbar && (!options().hud.armorStatus.off() || elytraWarning);
        boolean renderingTheHotbar = armorStatusAlways || anyArmorTimerActive || syncArmorAnimating;
        if (canRenderArmorHotbar && renderingTheHotbar) {
            graphics.blit(
                    ofQoQ("textures/gui/sprites/hud/armor_hotbar.png"),
                    this.getArmorBarX(this.minecraft, graphics),
                    getGuiHeight(graphics) - 2 + syncArmorAnimationYOffset,
                    0.0F,
                    0.0F,
                    82,
                    22,
                    82,
                    22
            );
        }

        i = 0;
        for (EquipmentSlot slot : equipmentSlots()) {
            if (!options().hud.armorStatus.off()) {
                if (slot != EquipmentSlot.CHEST || !SHOULD_WARN_OF_ELYTRA) {
                    boolean timerActive = isBeforeTick(tick, ARMOR_TIMERS[i]);
                    boolean shouldRenderSlot = armorStatusAlways;
                    int armorYOffset = 0;

                    boolean slotAnimating = ARMOR_TIMERS[i] > 0 && isWithinTickWindow(tick, ARMOR_TIMERS[i], animationTimeTicks);
                    if (armorStatusOnUpdate) {
                        if (options().hud.armorHotbar) {
                            shouldRenderSlot = anyArmorTimerActive || syncArmorAnimating;
                            if (syncArmorAnimating) {
                                armorYOffset = syncArmorAnimationYOffset;
                            }
                        } else {
                            shouldRenderSlot = timerActive || slotAnimating;
                            if (slotAnimating) {
                                armorYOffset = getSpectatorAnimationYOffsetFromTicks(tick, ARMOR_TIMERS[i], animationTimeTicks);
                            }
                        }
                    }

                    if (shouldRenderSlot) {
                        if (options().hud.emptySlots && getItemBySlot(this.minecraft, slot).isEmpty()) {
                            String name = switch (slot) {
                                case CHEST -> "chestplate";
                                case LEGS -> "leggings";
                                case FEET -> "boots";
                                default -> "helmet";
                            };
                            graphics.blit(
                                    new ResourceLocation("textures/item/empty_armor_slot_" + name + ".png"),
                                    this.getHighlightedSlotX(minecraft, graphics, slot) + 4,
                                    getGuiHeight(graphics) + armorYOffset + 1,
                                    0.0F,
                                    0.0F,
                                    16,
                                    16,
                                    16,
                                    16
                            );
                        }
                        drawItem(this.minecraft, graphics, getItemBySlot(this.minecraft, slot), this.getEquipmentSlotX(this.minecraft, slot), true, armorYOffset);
                    }
                    boolean animating = !options().hud.armorStatus.always() && slotAnimating;
                    boolean fadeAnimating = !timerActive && slotAnimating;
                    float slotHighlightAlpha = 1.0F;
                    if (fadeAnimating && animationTimeTicks > 0 && (options().hud.armorStatus.always() || options().hud.armorHotbar)) {
                        int elapsedTicks = tick - ARMOR_TIMERS[i];
                        slotHighlightAlpha = Mth.clamp(1.0F - ((float) elapsedTicks / animationTimeTicks), 0.0F, 1.0F);
                    }
                    if (shouldRenderSlot && getItemHealthPercentage(getItemBySlot(this.minecraft, slot)) < 0.11F) {
                        this.renderWarningIndicator(this.minecraft, graphics, 0, 0, slot, armorYOffset);
                    }
                }
            }

            if (slot == EquipmentSlot.CHEST && elytraWarning) {
                drawItem(this.minecraft, graphics, new ItemStack(Items.ELYTRA), this.getEquipmentSlotX(this.minecraft, EquipmentSlot.CHEST), true);
            }
            i++;
        }

        ItemStack mainHandItem = getItemBySlot(this.minecraft, EquipmentSlot.MAINHAND);
        ItemStack offHandItem = getItemBySlot(this.minecraft, EquipmentSlot.OFFHAND);

        this.tryRenderItem(graphics, mainHandItem, offHandItem);
    }

    /**
     * Renders custom debug HUD lines regardless of F3 state.
     * <p>See {@link DebugScreenOverlay#renderLines(GuiGraphics, List, boolean)}</p>
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void renderAlwaysOnDebugHud(GuiGraphics graphics, float partialTick, CallbackInfo ci) {
        if (!modEnabled(this.minecraft) || this.minecraft.options.hideGui || this.minecraft.options.renderDebug) {
            return;
        }

        List<String> lines = new ArrayList<>();
        DebugHudHelper.appendDebugHudLines(this.minecraft, lines);
        if (lines.isEmpty()) {
            return;
        }

        Collections.reverse(lines);
        int i = 9;

        for (int j = 0; j < lines.size(); j++) {
            String s = lines.get(j);
            if (!Strings.isNullOrEmpty(s)) {
                int k = this.minecraft.font.width(s);
                int l = graphics.guiWidth() - 2 - k;
                int i1 = 2 + i * j;
                graphics.fill(l - 1, i1 - 1, l + k + 1, i1 + i - 1, -1873784752);
            }
        }

        for (int j1 = 0; j1 < lines.size(); j1++) {
            String s1 = lines.get(j1);
            if (!Strings.isNullOrEmpty(s1)) {
                int k1 = this.minecraft.font.width(s1);
                int l1 = graphics.guiWidth() - 2 - k1;
                int i2 = 2 + i * j1;
                graphics.drawString(this.minecraft.font, s1, l1, i2, 14737632, false);
            }
        }

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            int x = graphics.guiWidth() - 2 - this.minecraft.font.width(line);
            int y = 2 + index * 9;
            graphics.drawString(this.minecraft.font, line, x, y, CommonColors.WHITE, false);
        }
    }

    /**
     * Renders an item.
     */
    @Unique
    private boolean renderItem(GuiGraphics graphics, ItemStack heldStack, boolean trackedItem) {
        boolean holdingArrowDisplayableProjectileWeapon = holdingArrowDisplayableProjectileWeapon(this.minecraft, heldStack);
        boolean trackedArrowDisplay = options().itemCounter.arrowCounter && isStackArrow(ItemHudTracker.getStack()) && ARROW_OUTLINE;
        boolean shouldCountArrows = holdingArrowDisplayableProjectileWeapon || (trackedItem && trackedArrowDisplay);
        if (!options().itemCounter.enableItemCounter.enabled() || (!heldStack.isStackable() && !holdingArrowDisplayableProjectileWeapon)) {
            if (!isStackShulker(heldStack)) {
                return false;
            }
        }

        if (options().itemCounter.onlyShowArrowCounter && !isStackArrow(heldStack) && !holdingArrowDisplayableProjectileWeapon) {
            return false;
        }

        int count = 0;
        List<Integer> items = new ArrayList<>();
        LocalPlayer player = this.minecraft.player;

        NonNullList<ItemStack> playerItems = NonNullList.create();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (!itemStack.isEmpty()) {
                playerItems.add(player.getInventory().getItem(i));
            }
        }
        count += this.iterateThroughInventoryAndAddCount(playerItems, heldStack, shouldCountArrows, items);
        if (options().itemCounter.countEnderChest) {
            count += this.iterateThroughPersistedEnderChestAndAddCount(shouldCountArrows ? new ItemStack(Items.ARROW, 1) : heldStack, items);
        }

        boolean trackedArrow = shouldCountArrows;
        boolean alwaysShowArrowFallback = isAlwaysShowArrowCounterEnabled(this.minecraft) && isStackArrow(heldStack);
        int maxCount = trackedArrow ? 64 : heldStack.getMaxStackSize();

        if (count <= 0 && !trackedArrow && !alwaysShowArrowFallback) {
            return false;
        }

        int fullStacks;
        if (!heldStack.isEmpty() || trackedArrow) {
            String text = String.valueOf(count);

            boolean hasInfinity = false;
            if (hasInfinity(heldStack) && getProjectileFromActiveHand(this.minecraft).is(Items.ARROW) && holdingArrowDisplayableProjectileWeapon && trackedArrow) {
                text = "∞";
                hasInfinity = true;
            }

            String maxItemCount = String.valueOf(maxCount);
            boolean evenStack = count != 0 && count != 64 && count % maxCount == 0;
            if (!hasInfinity && count > maxCount) {
                if (options().itemCounter.enableItemCounter != ItemCounter.TOTAL && evenStack) {
                    text = count / maxCount + "x " + maxItemCount;
                } else if (options().itemCounter.enableItemCounter == ItemCounter.STACKS) {
                    int totalCount = 0;
                    for (int i : items) {
                        totalCount += i;
                    }

                    fullStacks = totalCount / maxCount;
                    int remainder = totalCount % maxCount;

                    String additional = remainder > 0 ? " & " + remainder : "";

                    text = fullStacks + "x" + (remainder == 0 ? " " : "") + maxItemCount + additional;
                }
            }
            CompoundTag components = heldStack.getOrCreateTag();
            ItemStack newStack = new ItemStack(heldStack.getItem(), count);
            newStack.setTag(components);

            boolean isArrow = (isStackArrow(newStack) && ARROW_OUTLINE) || alwaysShowArrowFallback;
            boolean arrowDisplayValid = (isArrow || holdingArrowDisplayableProjectileWeapon) && options().itemCounter.enableItemCounter == ItemCounter.STACKS ? count < 65 : count < 100;
            boolean shouldRenderArrowUi = options().itemCounter.arrowCounter && !hasInfinity && arrowDisplayValid && (holdingArrowDisplayableProjectileWeapon || alwaysShowArrowFallback || !this.renderingItem);

            int itemX = !isLeftHanded(this.minecraft) ? -117 : 101;
            int negIncrease;
            if (!getOffHandStack(player).isEmpty()) {
                negIncrease = -29;
                itemX += increasedBasedOnHand(this.minecraft, negIncrease, false);
            }
            int textLength = text.length();
            int textWidth = this.minecraft.font.width(text);
            if (textLength > 4) {
                negIncrease = -3 * (textLength - 4);
                itemX += increasedBasedOnHand(this.minecraft, negIncrease, false);
            }

            int itemAnimationYOffset = 0;
            boolean animateTrackedArrow = trackedItem && trackedArrowDisplay && !holdingArrowDisplayableProjectileWeapon;
            if ((trackedItem || animateTrackedArrow) && !ItemHudTracker.isWithinDisplayWindow()) {
                itemAnimationYOffset = getSpectatorAnimationYOffsetFromTicks(player.tickCount, ItemHudTracker.getDisplayExpireTick(), ItemHudTracker.getAnimationTimeTicks());
            }

            boolean arrowAndZero = count == 0 && (trackedArrow || alwaysShowArrowFallback);
            ItemStack playerProjectile = getProjectileFromActiveHand(this.minecraft);
            ItemStack stackToRender = newStack;
            ItemStack arrow = new ItemStack(Items.ARROW, 1);
            if (arrowAndZero) {
                if (holdingArrowDisplayableProjectileWeapon && !isStackArrow(ItemHudTracker.getStack())) {
                    stackToRender = arrow.copy();
                } else if (alwaysShowArrowFallback) {
                    stackToRender = arrow.copy();
                } else {
                    stackToRender = ItemHudTracker.getStack();
                }
            } else if (trackedItem && trackedArrowDisplay && !holdingArrowDisplayableProjectileWeapon) {
                stackToRender = arrow.copy();
            } else if (holdingArrowDisplayableProjectileWeapon) {
                if (isProjectileWeapon(ItemHudTracker.getStack().getItem())) {
                    stackToRender = arrow.copy();
                } else {
                    CompoundTag playerProjectileComponents = playerProjectile.getOrCreateTag();
                    if (!playerProjectile.isEmpty()) {
                        stackToRender = new ItemStack(playerProjectile.getItem(), count);
                        stackToRender.setTag(playerProjectileComponents);
                    } else if (isAlwaysShowArrowCounterEnabled(this.minecraft)) {
                        stackToRender = arrow.copy();
                    }
                }
            }
            drawItem(this.minecraft, graphics, stackToRender, itemX, false, itemAnimationYOffset);

            int color = CommonColors.WHITE;
            boolean validArrow = isArrow || arrowAndZero || holdingArrowDisplayableProjectileWeapon;
            if (shouldRenderArrowUi && validArrow && !hasInfinity) {
                color = getCountColor(count);
            }

            int textX = itemX - (textWidth / 2) + 11;
            if (textLength == 1) {
                textX += 3;
            }

            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 200);

            graphics.drawString(this.minecraft.font, text, ((graphics.guiWidth() / 2) + textX), graphics.guiHeight() - (hasInfinity ? 9 : 10) + itemAnimationYOffset, color, true);
            if (options().itemCounter.displayTotalWithStacks && count > 64 && (evenStack || options().itemCounter.enableItemCounter == ItemCounter.STACKS)) {
                graphics.drawString(this.minecraft.font, "(" + String.format("%,d", count) + ")", ((graphics.guiWidth() / 2) + textX), graphics.guiHeight() - 22 + itemAnimationYOffset, color, true);
            }

            graphics.pose().popPose();

            return true;
        }
        return false;
    }

    /**
     * Iterates through persisted ender chest contents, and returns a count to add.
     */
    @Unique
    private int iterateThroughPersistedEnderChestAndAddCount(ItemStack heldStack, List<Integer> items) {
        return this.iterateThroughPersistedEntriesAndAddCount(EnderChestHelper.getPersistedEnderChestItemsForCurrentWorld(), heldStack, items);
    }

    /**
     * Iterates through an inventory, to check for additional count to add to the item count.
     */
    @Unique
    private int iterateThroughPersistedEntriesAndAddCount(List<ContainerData.StoredEnderChestStack> entries, ItemStack heldStack, List<Integer> items) {
        int count = 0;
        if (entries == null || entries.isEmpty()) {
            return 0;
        }

        for (ContainerData.StoredEnderChestStack stored : entries) {
            if (stored == null || stored.count <= 0 || stored.itemId == null || stored.itemId.isBlank()) {
                continue;
            }

            ItemStack invStack;
            try {
                ResourceLocation resourceLocation = new ResourceLocation(stored.itemId);
                Optional<Item> item = BuiltInRegistries.ITEM.getOptional(resourceLocation);
                if (item.isEmpty() || item.get() == Items.AIR) {
                    continue;
                }
                invStack = new ItemStack(item.get(), stored.count);
            } catch (Exception ignored) {
                continue;
            }

            if (options().itemCounter.countContainers && isStackShulker(invStack)) {
                count += this.iterateThroughPersistedEntriesAndAddCount(stored.containedItems, heldStack, items);
                continue;
            }

            if (!(invStack.is(heldStack.getItem()))) {
                continue;
            }

            boolean sameComponents = heldStack.getOrCreateTag().toString().equals(stored.components == null ? "" : stored.components);
            boolean matches = options().itemCounter.onlyCountMatchingItems
                    ? invStack.is(heldStack.getItem()) && sameComponents
                    : itemMatchesInventoryItem(heldStack, invStack);

            if (matches) {
                count += stored.count;
                items.add(stored.count);
            }
        }
        return count;
    }

    /**
     * Iterates through all possible inventories, and returns a count to add.
     */
    @Unique
    private int iterateThroughInventoryAndAddCount(NonNullList<ItemStack> inventory, ItemStack heldStack, boolean countingArrows, List<Integer> items) {
        int count = 0;
        ItemStack targetStack = countingArrows ? new ItemStack(Items.ARROW, 1) : heldStack;

        for (ItemStack invStack : inventory) {
            if (options().itemCounter.countContainers && isStackShulker(invStack)) {
                count += iterateTransportablesAndAddCount(invStack, targetStack, items);
            } else if (
                    (options().itemCounter.countAllArrows && options().itemCounter.arrowCounter && (isStackArrow(invStack) && isStackArrow(ItemHudTracker.getStack()) && !this.renderingItem))
                            || (countingArrows ? !options().itemCounter.countAllArrows ? invStack.is(getProjectileFromActiveHand(this.minecraft).getItem()) && isStackArrow(invStack) : isStackArrow(invStack) : invStack.is(heldStack.getItem()))) {
                if (itemMatchesInventoryItem(targetStack, invStack) || countingArrows || (options().itemCounter.countAllArrows && isStackArrow(ItemHudTracker.getStack()))) {
                    count += invStack.getCount();
                    items.add(invStack.getCount());
                }
            }
        }

        return count;
    }
}

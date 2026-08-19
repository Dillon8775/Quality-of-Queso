package net.dillon.qualityofqueso.mixin.client.hud;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.helper.EnderChestHelper;
import net.dillon.qualityofqueso.option.ContainerData;
import net.dillon.qualityofqueso.option.eum.hud.ItemCounter;
import net.dillon.qualityofqueso.util.ItemHudTracker;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static net.dillon.dillonlib.task.ClientTasks.getGuiHeight;
import static net.dillon.dillonlib.task.ClientTasks.getGuiWidth;
import static net.dillon.qualityofqueso.helper.GuiHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.util.ItemHudTracker.ARROW_OUTLINE;

@Mixin(Hud.class)
public class HudMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private static Identifier HOTBAR_SELECTION_SPRITE;
    @Shadow
    @Final
    private static Identifier HOTBAR_OFFHAND_RIGHT_SPRITE;
    @Unique
    private boolean renderingItem = false;
    @Unique
    private int lastObservedPlayerTick = Integer.MIN_VALUE;

    /**
     * Tries to render an item on the screen.
     */
    @Unique
    private void tryRenderItem(GuiGraphicsExtractor graphics, ItemStack mainHandItem, ItemStack offHandItem) {
        // Get the persisted data from the player's last known ender chest
        EnderChestHelper.persistEnderChestContentsIfOpen(this.minecraft.player);
        // Then get the current picked up/dropped item stack
        ItemStack pickedUpOrDroppedStack = ItemHudTracker.getStack();

        // Render template if positioning elements
        if (isPositioningElements(this.minecraft)) {
            this.renderingItem = this.renderItem(graphics, new ItemStack(Items.ARROW, 3), false);
            return;
        }

        // Render only the arrow count, if onlyShowArrowCounter is on
        if (client().itemCounter().onlyShowArrowCounter) {
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

        if (slot != EquipmentSlot.OFFHAND) {
            base += client().hud().armorStatusPosition[0];
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
    private int getArmorBarX(Minecraft minecraft, GuiGraphicsExtractor graphics) {
        return getGuiWidth(graphics) + this.getEquipmentSlotX(minecraft, EquipmentSlot.HEAD) - 3;
    }

    /**
     * @return the x-position that the highlighted slot should render under the armor item.
     */
    @Unique
    private int getHighlightedSlotX(Minecraft minecraft, GuiGraphicsExtractor graphics, EquipmentSlot slot) {
        return getGuiWidth(graphics) + this.getEquipmentSlotX(minecraft, slot) - 4;
    }

    /**
     * Renders the highlighted texture around a slot.
     */
    @Unique
    private void renderHighlightedArmorSlot(Minecraft minecraft, Identifier defaultSprite, GuiGraphicsExtractor graphics, EquipmentSlot slot, boolean warning, int yOffset, float alpha) {
        if (alpha <= 0.0F || !(client().hud().highlightArmor)) {
            return;
        }

        int yModifier = slot != EquipmentSlot.OFFHAND ? client().hud().armorStatusPosition[1] : client().hud().otherElementsY;
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                warning ? SLOT_CRITICAL : getHighlightedSlotTexture(minecraft, defaultSprite, getItemBySlot(minecraft, slot), slot),
                this.getHighlightedSlotX(minecraft, graphics, slot),
                (getGuiHeight(graphics) - 3 + yOffset) + yModifier,
                24,
                23,
                alpha
        );
    }

    /**
     * Renders the {@code warning texture} around armor items.
     */
    @Unique
    private void renderWarningIndicator(Minecraft minecraft, GuiGraphicsExtractor graphics, int slot, int itemX, EquipmentSlot equipmentSlot, int yOffset) {
        if (!client().hud().warningIndicators) {
            return;
        }

        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                Identifier.withDefaultNamespace("world_list/error_highlighted"),
                getGuiWidth(graphics) + (itemX != 0 ? itemX : equipmentSlot == null ? -78 + (slot * 20) : this.getEquipmentSlotX(minecraft, equipmentSlot) + 10),
                getGuiHeight(graphics) + 4 + yOffset,
                16,
                16
        );
    }

    /**
     * Renders lock icons on locked hotbar slots.
     */
    @Unique
    private void renderLockedHotbarSlots(GuiGraphicsExtractor graphics) {
        if (!isPositioningElements(this.minecraft) && (!client().lockedSlots().lockedSlots || !client().lockedSlots().showLock.inHud() || this.minecraft.player == null)) {
            return;
        }

        Set<Integer> lockedPlayerSlots = ContainerHelper.getLockedSlots(false);
        boolean positioningElements = isPositioningElements(this.minecraft);
        if (!positioningElements && lockedPlayerSlots.isEmpty()) {
            return;
        }

        for (int slot = 0; slot < 9; slot++) {
            if (!lockedPlayerSlots.contains(slot)) {
                if (positioningElements) {
                    this.renderWarningIndicator(this.minecraft, graphics, slot, 0, null, client().hud().otherElementsY);
                }
                continue;
            }

            if (!positioningElements && this.minecraft.player.getInventory().getItem(slot).isEmpty()) {
                continue;
            }

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    qoqIdentifier(LOCKED_TEXTURE),
                    getGuiWidth(graphics) - 91 + (slot * 20),
                    (getGuiHeight(graphics) + 11) + client().hud().otherElementsY,
                    10,
                    10
            );
        }
    }

    /**
     * Renders the modified selection slot.
     */
    @ModifyArg(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 1), index = 1)
    private Identifier modifyHighlightedSlot(Identifier original) {
        if (!modEnabled(this.minecraft) || this.minecraft.player == null) {
            return original;
        }
        return getHighlightedSlotTexture(this.minecraft, HOTBAR_SELECTION_SPRITE, this.minecraft.player.getInventory().getItem(this.minecraft.player.getInventory().getSelectedSlot()), null);
    }

    /**
     * Renders things overtop of everything.
     */
    @Inject(method = "extractItemHotbar", at = @At("TAIL"))
    private void renderAllSprites(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        for (int i = 0; i < this.minecraft.player.getInventory().getContainerSize() - 34; i++) {
            ItemStack item = this.minecraft.player.getInventory().getItem(i);
            if (getItemHealthPercentage(item) < 0.11F) {
                this.renderWarningIndicator(this.minecraft, graphics, i, 0, null, client().hud().otherElementsY);
            }
        }

        ItemStack offHandItem = this.minecraft.player.getOffhandItem();
        if (!offHandItem.isEmpty()) {
            if (client().hud().coloredHighlighting && getItemHealthPercentage(offHandItem) < 0.41F) {
                this.renderHighlightedArmorSlot(this.minecraft, HOTBAR_SELECTION_SPRITE, graphics, EquipmentSlot.OFFHAND, false, 0, 1.0F);
            }
            if (client().hud().warningIndicators && getItemHealthPercentage(offHandItem) < 0.11F) {
                this.renderWarningIndicator(this.minecraft, graphics, 0, 0, EquipmentSlot.OFFHAND, client().hud().otherElementsY);
            }
        }

        this.renderLockedHotbarSlots(graphics);
    }

    /**
     * Implements the {@code armor status} feature.
     */
    @Inject(method = "extractItemHotbar", at = @At("HEAD"))
    private void renderArmorStatusAndTryRenderItem(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!modEnabled(this.minecraft) || this.minecraft.player == null) {
            return;
        }

        if (isPositioningElements(this.minecraft) || (client().visualTime().overrideClientTime && client().visualTime().displayVisualClock)) {
            int clockX = graphics.guiWidth() - 23 + client().visualTime().visualClockPosition[0];
            int clockY = getGuiHeight(graphics) - 3 + client().visualTime().visualClockPosition[1];
            drawVisualTimeClock(
                    graphics,
                    this.minecraft,
                    clockX,
                    clockY,
                    18
            );
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

        boolean armorStatusOnUpdate = client().hud().armorStatus.onUpdate();
        boolean armorStatusAlways = !armorStatusOnUpdate && !client().hud().armorStatus.off();
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

        boolean syncArmorAnimating = client().hud().armorHotbar
                && armorStatusOnUpdate
                && !anyArmorTimerActive
                && latestArmorTimer > 0
                && isWithinTickWindow(tick, latestArmorTimer, animationTimeTicks);
        int syncArmorAnimationYOffset = syncArmorAnimating
                ? getSpectatorAnimationYOffsetFromTicks(tick, latestArmorTimer, animationTimeTicks)
                : 0;

        CAN_ACTUALLY_RENDER_ARMOR_HOTBAR = anyArmorTimerActive;

        boolean elytraWarning = client().elytraAlarm().elytraAlarm.enabled() && SHOULD_WARN_OF_ELYTRA;
        boolean canRenderArmorHotbar = client().hud().armorHotbar && (!client().hud().armorStatus.off() || elytraWarning);
        boolean renderingTheHotbar = armorStatusAlways || anyArmorTimerActive || syncArmorAnimating;
        if (isPositioningElements(this.minecraft) || (canRenderArmorHotbar && renderingTheHotbar)) {
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    getArmorHotbarTexture(),
                    this.getArmorBarX(this.minecraft, graphics),
                    (getGuiHeight(graphics) - 2 + syncArmorAnimationYOffset) + client().hud().armorStatusPosition[1],
                    82,
                    22
            );
        }

        i = 0;
        for (EquipmentSlot slot : equipmentSlots()) {
            if (!client().hud().armorStatus.off()) {
                if (slot != EquipmentSlot.CHEST || !SHOULD_WARN_OF_ELYTRA) {
                    boolean timerActive = isBeforeTick(tick, ARMOR_TIMERS[i]);
                    boolean shouldRenderSlot = armorStatusAlways;
                    int armorYOffset = 0;

                    boolean slotAnimating = ARMOR_TIMERS[i] > 0 && isWithinTickWindow(tick, ARMOR_TIMERS[i], animationTimeTicks);
                    if (armorStatusOnUpdate) {
                        if (client().hud().armorHotbar) {
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

                    if (isPositioningElements(this.minecraft) || shouldRenderSlot) {
                        if (client().hud().emptySlots && getItemBySlot(this.minecraft, slot).isEmpty()) {
                            String name = switch (slot) {
                                case CHEST -> "chestplate";
                                case LEGS -> "leggings";
                                case FEET -> "boots";
                                default -> "helmet";
                            };
                            graphics.blitSprite(
                                    RenderPipelines.GUI_TEXTURED,
                                    Identifier.withDefaultNamespace("container/slot/" + name),
                                    this.getHighlightedSlotX(minecraft, graphics, slot) + 4,
                                    (getGuiHeight(graphics) + armorYOffset + 1) + client().hud().armorStatusPosition[1],
                                    16,
                                    16
                            );
                        }
                        drawItem(this.minecraft, graphics, getItemBySlot(this.minecraft, slot), this.getEquipmentSlotX(this.minecraft, slot), client().hud().armorStatusPosition[1], true, armorYOffset);
                    }
                    boolean animating = !client().hud().armorStatus.always() && slotAnimating;
                    boolean fadeAnimating = !timerActive && slotAnimating;
                    float slotHighlightAlpha = 1.0F;
                    if (fadeAnimating && animationTimeTicks > 0 && (client().hud().armorStatus.always() || client().hud().armorHotbar)) {
                        int elapsedTicks = tick - ARMOR_TIMERS[i];
                        slotHighlightAlpha = Mth.clamp(1.0F - ((float) elapsedTicks / animationTimeTicks), 0.0F, 1.0F);
                    }
                    if ((isPositioningElements(this.minecraft) && client().hud().highlightArmor) || timerActive || animating || fadeAnimating) {
                        this.renderHighlightedArmorSlot(this.minecraft, HOTBAR_SELECTION_SPRITE, graphics, equipmentSlots()[i], false, armorYOffset, slotHighlightAlpha);
                    }
                    if (shouldRenderSlot && getItemHealthPercentage(getItemBySlot(this.minecraft, slot)) < 0.11F) {
                        this.renderWarningIndicator(this.minecraft, graphics, 0, 0, slot, armorYOffset + client().hud().armorStatusPosition[1]);
                    }
                }
            }

            if (slot == EquipmentSlot.CHEST && elytraWarning) {
                drawItem(this.minecraft, graphics, new ItemStack(Items.ELYTRA), this.getEquipmentSlotX(this.minecraft, EquipmentSlot.CHEST), client().hud().armorStatusPosition[1], true);
                this.renderHighlightedArmorSlot(this.minecraft, HOTBAR_SELECTION_SPRITE, graphics, slot, true, 0, 1.0F);
                this.renderWarningIndicator(this.minecraft, graphics, 0, 0, slot, client().hud().armorStatusPosition[1]);
            }
            i++;
        }

        ItemStack mainHandItem = getItemBySlot(this.minecraft, EquipmentSlot.MAINHAND);
        ItemStack offHandItem = getItemBySlot(this.minecraft, EquipmentSlot.OFFHAND);

        this.tryRenderItem(graphics, mainHandItem, offHandItem);
    }

    /**
     * Renders an item.
     */
    @Unique
    private boolean renderItem(GuiGraphicsExtractor graphics, ItemStack heldStack, boolean trackedItem) {
        boolean holdingArrowDisplayableProjectileWeapon = holdingArrowDisplayableProjectileWeapon(this.minecraft, heldStack);
        if (!client().itemCounter().itemCounter.enabled() || (!heldStack.isStackable() && !holdingArrowDisplayableProjectileWeapon)) {
            if (!heldStack.is(ItemTags.SHULKER_BOXES) && !heldStack.is(ItemTags.BUNDLES)) {
                return false;
            }
        }

        if (client().itemCounter().onlyShowArrowCounter && !isStackArrow(heldStack) && !holdingArrowDisplayableProjectileWeapon) {
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
        count += this.iterateThroughInventoryAndAddCount(playerItems, heldStack, holdingArrowDisplayableProjectileWeapon, items);
        if (client().itemCounter().countEnderChest) {
            count += this.iterateThroughPersistedEnderChestAndAddCount(heldStack, items);
        }

        boolean trackedArrow = client().itemCounter().arrowCounter && (holdingArrowDisplayableProjectileWeapon || (isStackArrow(ItemHudTracker.getStack()) && ARROW_OUTLINE));
        boolean alwaysShowArrowFallback = isAlwaysShowArrowCounterEnabled(this.minecraft) && isStackArrow(heldStack);
        int maxCount = trackedArrow ? 64 : heldStack.getMaxStackSize();

        boolean positioningElements = isPositioningElements(this.minecraft);
        if (positioningElements) {
            count = 3;
        }

        if (count <= 0 && !trackedArrow && !alwaysShowArrowFallback) {
            return false;
        }

        int fullStacks;
        if (positioningElements || !heldStack.isEmpty() || trackedArrow) {
            String text = String.valueOf(count);

            boolean hasInfinity = false;
            if (hasInfinity(heldStack) && getProjectileFromActiveHand(this.minecraft).is(Items.ARROW) && holdingArrowDisplayableProjectileWeapon && trackedArrow) {
                text = "∞";
                hasInfinity = true;
            }

            String maxItemCount = String.valueOf(maxCount);
            boolean evenStack = count != 0 && count != 64 && count % maxCount == 0;
            if (!hasInfinity && count > maxCount) {
                if (client().itemCounter().itemCounter != ItemCounter.TOTAL && evenStack) {
                    text = count / maxCount + "x " + maxItemCount;
                } else if (client().itemCounter().itemCounter == ItemCounter.STACKS) {
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
            DataComponentMap components = heldStack.getComponents();
            ItemStack newStack = new ItemStack(heldStack.getItem(), count);
            newStack.applyComponents(components);

            boolean isArrow = (isStackArrow(newStack) && ARROW_OUTLINE) || alwaysShowArrowFallback;
            boolean arrowDisplayValid = (isArrow || holdingArrowDisplayableProjectileWeapon) && client().itemCounter().itemCounter == ItemCounter.STACKS ? count < 65 : count < 100;
            boolean shouldRenderArrowUi = client().itemCounter().arrowCounter && !hasInfinity && arrowDisplayValid && (holdingArrowDisplayableProjectileWeapon || alwaysShowArrowFallback || !this.renderingItem);

            int itemX = !isLeftHanded(this.minecraft) ? -117 : 101;
            int negIncrease;
            if (client().itemCounter().moveItemCounterOver && !getOffHandStack(player).isEmpty()) {
                negIncrease = -29;
                itemX += increasedBasedOnHand(this.minecraft, negIncrease, false);
            }
            int textLength = text.length();
            int textWidth = this.minecraft.font.width(text);
            if (textLength > 4) {
                negIncrease = -3 * (textLength - 4);
                itemX += increasedBasedOnHand(this.minecraft, negIncrease, false);
            }

            itemX += client().itemCounter().itemCounterPosition[0];

            int itemAnimationYOffset = 0;
            if (trackedItem && !ItemHudTracker.isWithinDisplayWindow()) {
                itemAnimationYOffset = getSpectatorAnimationYOffsetFromTicks(player.tickCount, ItemHudTracker.getDisplayExpireTick(), ItemHudTracker.getAnimationTimeTicks());
            }

            if (positioningElements || (shouldRenderArrowUi && (isArrow || holdingArrowDisplayableProjectileWeapon))) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_RIGHT_SPRITE,
                        getGuiWidth(graphics) + itemX - 10,
                        (getGuiHeight(graphics) - 3 + itemAnimationYOffset) + client().itemCounter().itemCounterPosition[1],
                        29,
                        24
                );
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                        client().hud().coloredHighlighting
                                ? count < 11 ? SLOT_CRITICAL
                                  : count < 21 ? SLOT_AVERAGE
                                    : count < 31 ? SLOT_DECENT
                                      : SLOT_GOOD
                                : HOTBAR_SELECTION_SPRITE,
                        getGuiWidth(graphics) + itemX - 4,
                        getGuiHeight(graphics) - 3 + itemAnimationYOffset + client().itemCounter().itemCounterPosition[1],
                        24,
                        23
                );
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
            } else if (holdingArrowDisplayableProjectileWeapon) {
                if (isProjectileWeapon(ItemHudTracker.getStack().getItem())) {
                    stackToRender = arrow.copy();
                } else {
                    DataComponentMap playerProjectileComponents = playerProjectile.getComponents();
                    if (!playerProjectile.isEmpty()) {
                        stackToRender = new ItemStack(playerProjectile.getItem(), count);
                        stackToRender.applyComponents(playerProjectileComponents);
                    } else if (isAlwaysShowArrowCounterEnabled(this.minecraft)) {
                        stackToRender = arrow.copy();
                    }
                }
            }
            drawItem(this.minecraft, graphics, stackToRender, itemX, client().itemCounter().itemCounterPosition[1], false, itemAnimationYOffset);

            int color = CommonColors.WHITE;
            boolean validArrow = isArrow || arrowAndZero || holdingArrowDisplayableProjectileWeapon;
            if (validArrow) {
                color = getArrowCounterTextColor(count, hasInfinity);
            }
            if (positioningElements) {
                color = CommonColors.GREEN;
            }

            if (positioningElements || (!hasInfinity && shouldRenderArrowUi && client().hud().warningIndicators && validArrow && count < 6)) {
                this.renderWarningIndicator(this.minecraft, graphics, 0, itemX, null, itemAnimationYOffset + client().itemCounter().itemCounterPosition[1]);
            }

            int textX = itemX - (textWidth / 2) + 11;
            if (textLength == 1) {
                textX += 3;
            }
            graphics.text(this.minecraft.font, text, ((graphics.guiWidth() / 2) + textX), (graphics.guiHeight() - (hasInfinity ? 9 : 10) + itemAnimationYOffset) + client().itemCounter().itemCounterPosition[1], color, true);
            if (client().itemCounter().displayTotalWithStacks && count > 64 && (evenStack || client().itemCounter().itemCounter == ItemCounter.STACKS)) {
                graphics.text(this.minecraft.font, "(" + String.format("%,d", count) + ")", ((graphics.guiWidth() / 2) + textX), graphics.guiHeight() - 22 + itemAnimationYOffset, color, true);
            }

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
                Identifier identifier = Identifier.parse(stored.itemId);
                Optional<Item> item = BuiltInRegistries.ITEM.getOptional(identifier);
                if (item.isEmpty() || item.get() == Items.AIR) {
                    continue;
                }
                invStack = new ItemStack(item.get(), stored.count);
            } catch (Exception ignored) {
                continue;
            }

            if (client().itemCounter().countContainers && (invStack.is(ItemTags.SHULKER_BOXES) || invStack.is(ItemTags.BUNDLES))) {
                count += this.iterateThroughPersistedEntriesAndAddCount(stored.containedItems, heldStack, items);
                continue;
            }

            if (!(invStack.is(heldStack.getItem()))) {
                continue;
            }

            boolean sameComponents = heldStack.getComponents().toString().equals(stored.components == null ? "" : stored.components);
            boolean matches = client().itemCounter().onlyCountMatchingItems
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
    private int iterateThroughInventoryAndAddCount(NonNullList<ItemStack> inventory, ItemStack heldStack, boolean holdingArrowDisplayableProjectileWeapon, List<Integer> items) {
        int count = 0;

        for (ItemStack invStack : inventory) {
            if (client().itemCounter().countContainers && (invStack.is(ItemTags.SHULKER_BOXES) || invStack.is(ItemTags.BUNDLES))) {
                count += iterateTransportablesAndAddCount(invStack, heldStack, items);
            } else if (
                    (client().itemCounter().countAllArrows && client().itemCounter().arrowCounter && (isStackArrow(invStack) && isStackArrow(ItemHudTracker.getStack()) && !this.renderingItem))
                            || (holdingArrowDisplayableProjectileWeapon ? !client().itemCounter().countAllArrows ? invStack.is(getProjectileFromActiveHand(this.minecraft).getItem()) && isStackArrow(invStack) : isStackArrow(invStack) : invStack.is(heldStack.getItem()))) {
                if (itemMatchesInventoryItem(heldStack, invStack) || holdingArrowDisplayableProjectileWeapon || (client().itemCounter().countAllArrows && isStackArrow(ItemHudTracker.getStack()))) {
                    count += invStack.getCount();
                    items.add(invStack.getCount());
                }
            }
        }

        return count;
    }
}
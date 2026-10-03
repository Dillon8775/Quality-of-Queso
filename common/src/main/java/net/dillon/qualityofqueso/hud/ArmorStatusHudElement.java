package net.dillon.qualityofqueso.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static net.dillon.dillonlib.task.ClientTasks.drawSprite;
import static net.dillon.dillonlib.task.ClientTasks.getGuiHeight;
import static net.dillon.qualityofqueso.helper.GuiHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModHelper.equipmentSlots;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Holds the {@code Armor Status} to extract.
 */
public class ArmorStatusHudElement extends ModHudElement {
    private int lastObservedPlayerTick = Integer.MIN_VALUE;

    /**
     * @return if an armor slot was changed at all.
     */
    private boolean armorChanged(int slotIndex, ItemStack current) {
        ItemStack previous = LAST_ARMOR_STACKS[slotIndex];
        return !ItemStack.isSameItemSameComponents(previous, current);
    }

    /**
     * @return if the current tick is before the target tick.
     */
    private boolean isBeforeTick(int currentTick, int targetTick) {
        return currentTick - targetTick < 0;
    }

    /**
     * @return if the tick is valid for the duration ticks.
     */
    private boolean isWithinTickWindow(int currentTick, int startTick, int durationTicks) {
        if (durationTicks <= 0) {
            return false;
        }
        int elapsed = currentTick - startTick;
        return elapsed >= 0 && elapsed < durationTicks;
    }

    /**
     * Extracts the armor status.
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics) {
        if (minecraft.player == null) {
            return;
        }

        int tick = minecraft.player.tickCount;
        if (tick < lastObservedPlayerTick) {
            for (int timerIndex = 0; timerIndex < ARMOR_TIMERS.length; timerIndex++) {
                ARMOR_TIMERS[timerIndex] = 0;
                LAST_ARMOR_STACKS[timerIndex] = null;
            }
        }
        lastObservedPlayerTick = tick;
        int animationTimeTicks = getAnimationTimeInTicks(false);
        int i = 0;
        for (EquipmentSlot slot : equipmentSlots()) {
            ItemStack current = getItemBySlot(minecraft, slot);
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
        boolean canEverRenderArmorHotbar = isPositioningElements() && !client().hud().armorStatus.off();
        boolean renderingTheHotbar = armorStatusAlways || anyArmorTimerActive || syncArmorAnimating;
        if ((canEverRenderArmorHotbar && client().hud().armorHotbar) || (canRenderArmorHotbar && renderingTheHotbar)) {
            drawSprite(
                    graphics,
                    getArmorHotbarTexture(),
                    getArmorBarX(graphics),
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

                    if (canEverRenderArmorHotbar || shouldRenderSlot) {
                        if (client().hud().emptySlots && getItemBySlot(minecraft, slot).isEmpty()) {
                            String name = switch (slot) {
                                case CHEST -> "chestplate";
                                case LEGS -> "leggings";
                                case FEET -> "boots";
                                default -> "helmet";
                            };
                            drawSprite(
                                    graphics,
                                    Identifier.withDefaultNamespace("container/slot/" + name),
                                    getHighlightedSlotX(graphics, slot) + 4,
                                    (getGuiHeight(graphics) + armorYOffset + 1) + client().hud().armorStatusPosition[1],
                                    16,
                                    16
                            );
                        }

                        drawItem(graphics, getItemBySlot(minecraft, slot), getEquipmentSlotX(slot), client().hud().armorStatusPosition[1], true, armorYOffset);
                    }

                    boolean animating = !client().hud().armorStatus.always() && slotAnimating;
                    boolean fadeAnimating = !timerActive && slotAnimating;
                    float slotHighlightAlpha = 1.0F;
                    if (fadeAnimating && animationTimeTicks > 0 && (client().hud().armorStatus.always() || client().hud().armorHotbar)) {
                        int elapsedTicks = tick - ARMOR_TIMERS[i];
                        slotHighlightAlpha = Mth.clamp(1.0F - ((float) elapsedTicks / animationTimeTicks), 0.0F, 1.0F);
                    }
                    if ((canEverRenderArmorHotbar && client().hud().highlightArmor) || timerActive || animating || fadeAnimating) {
                        renderHighlightedSlot(graphics, equipmentSlots()[i], false, armorYOffset, slotHighlightAlpha);
                    }
                    if (shouldRenderSlot && getItemHealthPercentage(getItemBySlot(minecraft, slot)) < 0.11F) {
                        renderWarningIndicator(graphics, 0, 0, slot, armorYOffset + client().hud().armorStatusPosition[1]);
                    }
                }
            }

            if (slot == EquipmentSlot.CHEST && elytraWarning) {
                drawItem(graphics, new ItemStack(Items.ELYTRA), getEquipmentSlotX(EquipmentSlot.CHEST), client().hud().armorStatusPosition[1], true);
                renderHighlightedSlot(graphics, slot, true, 0, 1.0F);
                renderWarningIndicator(graphics, 0, 0, slot, client().hud().armorStatusPosition[1]);
            }
            i++;
        }
    }
}
package net.dillon.qualityofqueso.mixin.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dillon.qualityofqueso.option.eum.hud.ItemCounter;
import net.dillon.qualityofqueso.util.ItemHudTracker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
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

import static net.dillon.qualityofqueso.util.ButtonUtil.isStackShulker;
import static net.dillon.qualityofqueso.util.GuiUtil.*;
import static net.dillon.qualityofqueso.util.ItemHudTracker.ARROW_OUTLINE;
import static net.dillon.qualityofqueso.util.ModUtil.*;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private static ResourceLocation HOTBAR_SELECTION_SPRITE;
    @Shadow
    @Final
    private static ResourceLocation HOTBAR_OFFHAND_RIGHT_SPRITE;
    @Unique
    private static final int DISPLAY_TICKS = 80;
    @Unique
    private boolean renderingHeldItem = false;

    /**
     * Renders the modified selection slot.
     */
    @ModifyArg(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1), index = 0)
    private ResourceLocation modifyHighlightedSlot(ResourceLocation original) {
        if (this.minecraft.player == null) {
            return original;
        }
        return getHighlightedSlotTexture(HOTBAR_SELECTION_SPRITE, this.minecraft.player.getInventory().getSelected());
    }

    /**
     * Renders things overtop of everything.
     */
    @Inject(method = "renderItemHotbar", at = @At("TAIL"))
    private void renderAllWarningIndicators(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        for (int i = 0; i < this.minecraft.player.getInventory().getContainerSize() - 34; i++) {
            ItemStack item = this.minecraft.player.getInventory().getItem(i);
            if (getItemHealthPercentage(item) < 0.11F) {
                this.renderWarningIndicator(this.minecraft, graphics, i, 0, null);
            }
        }

        ItemStack offHandItem = this.minecraft.player.getOffhandItem();
        if (!offHandItem.isEmpty()) {
            if (options().hud.coloredHighlighting && getItemHealthPercentage(offHandItem) < 0.41F) {
                this.renderHighlightedArmorSlot(this.minecraft, HOTBAR_SELECTION_SPRITE, graphics, EquipmentSlot.OFFHAND, false);
            }
            if (options().hud.warningIndicators && getItemHealthPercentage(offHandItem) < 0.11F) {
                this.renderWarningIndicator(this.minecraft, graphics, 0, 0, EquipmentSlot.OFFHAND);
            }
        }
    }

    /**
     * Implements the {@code armor status} feature.
     */
    @Inject(method = "renderItemHotbar", at = @At("HEAD"))
    private void renderArmorStatus(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!modEnabled(this.minecraft) || this.minecraft.player == null) {
            return;
        }

        int i = 0;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack current = getItemBySlot(this.minecraft, slot);
            if (LAST_ARMOR_STACKS[i] == null) {
                LAST_ARMOR_STACKS[i] = current.copy();
                i++;
                continue;
            }
            if (armorChanged(i, current)) {
                ARMOR_TIMERS[i] = this.minecraft.player.tickCount + DISPLAY_TICKS;
                LAST_ARMOR_STACKS[i] = current.copy();
            }
            i++;
        }

        boolean canRenderArmorHotbar = options().hud.armorHotbar && (!options().hud.armorStatus.off() || (options().elytraAlarm.elytraAlarm.enabled() && SHOULD_WARN_OF_ELYTRA));

        if (canRenderArmorHotbar) {
            CAN_ACTUALLY_RENDER_ARMOR_HOTBAR = false;
            for (int armorTimer : ARMOR_TIMERS) {
                if (this.minecraft.player.tickCount < armorTimer) {
                    CAN_ACTUALLY_RENDER_ARMOR_HOTBAR = true;
                    break;
                }
            }
            if (CAN_ACTUALLY_RENDER_ARMOR_HOTBAR || !options().hud.armorStatus.onUpdate()) {
                graphics.blitSprite(
                        ofQoQ("hud/armor_hotbar"),
                        this.getArmorBarX(this.minecraft, graphics),
                        getGuiHeight(graphics) - 2,
                        82,
                        22
                );
            }
        }

        i = 0;
        for (EquipmentSlot slot : SLOTS) {
            if (!options().hud.armorStatus.off()) {
                if (slot != EquipmentSlot.CHEST || !SHOULD_WARN_OF_ELYTRA) {
                    boolean bl = this.minecraft.player.tickCount < ARMOR_TIMERS[i];
                    if ((bl || CAN_ACTUALLY_RENDER_ARMOR_HOTBAR) || !options().hud.armorStatus.onUpdate()) {
                        drawItem(this.minecraft, graphics, getItemBySlot(this.minecraft, slot), this.getEquipmentSlotX(this.minecraft, slot), true);
                    }
                    if (bl) {
                        this.renderHighlightedArmorSlot(this.minecraft, HOTBAR_SELECTION_SPRITE, graphics, SLOTS[i], false);
                    }
                    if (getItemHealthPercentage(getItemBySlot(this.minecraft, slot)) < 0.11F) {
                        this.renderWarningIndicator(this.minecraft, graphics, 0, 0, slot);
                    }
                }
            }

            if (slot == EquipmentSlot.CHEST && options().elytraAlarm.elytraAlarm.enabled() && SHOULD_WARN_OF_ELYTRA) {
                drawItem(this.minecraft, graphics, new ItemStack(Items.ELYTRA), this.getEquipmentSlotX(this.minecraft, EquipmentSlot.CHEST), true);
                this.renderHighlightedArmorSlot(this.minecraft, HOTBAR_SELECTION_SPRITE, graphics, slot, true);
                this.renderWarningIndicator(this.minecraft, graphics, 0, 0, slot);
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
    private boolean renderItem(GuiGraphics graphics, ItemStack stack) {
        boolean holdingArrowDisplayableProjectileWeapon = holdingArrowDisplayableProjectileWeapon(this.minecraft, stack);
        if (!options().hud.itemCounter.enabled() || (!stack.isStackable() && !holdingArrowDisplayableProjectileWeapon)) {
            if (!isStackShulker(stack)) {
                return false;
            }
        }

        int count = 0;
        List<Integer> items = new ArrayList<>();
        for (ItemStack invStack : this.minecraft.player.getInventory().items) {

            boolean isStackShulker = false;
            for (Item item : shulkerBoxes) {
                if (invStack.is(item)) {
                    isStackShulker = true;
                    break;
                }
            }

            if (options().hud.countContainers && isStackShulker) {
                ItemContainerContents container = invStack.get(DataComponents.CONTAINER);

                if (container != null) {
                    for (ItemStack containerStack : container.nonEmptyItems()) {
                        if (itemMatchesInventoryItem(stack, containerStack.copy())) {
                            count += containerStack.getCount();
                            items.add(containerStack.getCount());
                        }
                    }
                }
            } else if (
                    (options().hud.countAllArrows && options().hud.showArrowCounter && (isStackArrow(invStack) && isStackArrow(ItemHudTracker.getStack()) && !this.renderingHeldItem))
                            || (holdingArrowDisplayableProjectileWeapon ? !options().hud.countAllArrows ? invStack.is(getProjectileFromActiveHand(this.minecraft).getItem()) && isStackArrow(invStack) : isStackArrow(invStack) : invStack.is(stack.getItem()))) {
                if (itemMatchesInventoryItem(stack, invStack) || holdingArrowDisplayableProjectileWeapon || (options().hud.countAllArrows && isStackArrow(ItemHudTracker.getStack()))) {
                    count += invStack.getCount();
                    items.add(invStack.getCount());
                }
            }
        }

        boolean trackedArrow = options().hud.showArrowCounter && (holdingArrowDisplayableProjectileWeapon || (isStackArrow(ItemHudTracker.getStack()) && ARROW_OUTLINE));
        int maxCount = trackedArrow ? 64 : stack.getMaxStackSize();

        if (count <= 0 && !trackedArrow) {
            return false;
        }

        int fullStacks;
        if (!stack.isEmpty() || trackedArrow) {
            String text = String.valueOf(count);

            boolean hasInfinity = false;
            if (hasInfinity(stack) && getProjectileFromActiveHand(this.minecraft).is(Items.ARROW) && holdingArrowDisplayableProjectileWeapon && trackedArrow) {
                text = "∞";
                hasInfinity = true;
            }

            String maxItemCount = String.valueOf(maxCount);
            boolean evenStack = count != 0 && count != 64 && count % maxCount == 0;
            if (!hasInfinity && count > maxCount) {
                if (options().hud.itemCounter != ItemCounter.TOTAL && evenStack) {
                    text = count / maxCount + "x " + maxItemCount;
                } else if (options().hud.itemCounter == ItemCounter.STACKS) {
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
            DataComponentMap components = stack.getComponents();
            ItemStack newStack = new ItemStack(stack.getItem(), count);
            newStack.applyComponents(components);

            boolean isArrow = isStackArrow(newStack) && ARROW_OUTLINE;
            boolean arrowDisplayValid = (isArrow || holdingArrowDisplayableProjectileWeapon) && options().hud.itemCounter == ItemCounter.STACKS ? count < 65 : count < 100;
            boolean shouldRenderArrowUi = options().hud.showArrowCounter && !hasInfinity && arrowDisplayValid && (holdingArrowDisplayableProjectileWeapon || !this.renderingHeldItem);

            int itemX = !isLeftHanded(this.minecraft) ? -117 : 101;
            int negIncrease;
            if (!this.minecraft.player.getOffhandItem().isEmpty()) {
                negIncrease = -29;
                itemX += increasedBasedOnHand(this.minecraft, negIncrease);
            }
            int textLength = text.length();
            int textWidth = this.minecraft.font.width(text);
            if (textLength > 4) {
                negIncrease = -3 * (textLength - 4);
                itemX += increasedBasedOnHand(this.minecraft, negIncrease);
            }

            if (shouldRenderArrowUi && (isArrow || holdingArrowDisplayableProjectileWeapon)) {
                graphics.blitSprite(HOTBAR_OFFHAND_RIGHT_SPRITE,
                        getGuiWidth(graphics) + itemX - 10,
                        getGuiHeight(graphics) - 3,
                        29,
                        24
                );
                graphics.blitSprite(
                        options().hud.coloredHighlighting
                                ? count < 11 ? ofQoQ("hud/slot_bad")
                                  : count < 21 ? ofQoQ("hud/slot_ok")
                                    : ofQoQ("hud/slot_good")
                                : HOTBAR_SELECTION_SPRITE,
                        getGuiWidth(graphics) + itemX - 4,
                        getGuiHeight(graphics) - 3,
                        24,
                        23
                );
            }

            boolean arrowAndZero = trackedArrow && count == 0;
            ItemStack playerProjectile = getProjectileFromActiveHand(this.minecraft);
            ItemStack stackToRender = newStack;
            ItemStack arrow = new ItemStack(Items.ARROW, 1);
            if (arrowAndZero) {
                if (holdingArrowDisplayableProjectileWeapon && !isStackArrow(ItemHudTracker.getStack())) {
                    stackToRender = arrow.copy();
                } else {
                    stackToRender = ItemHudTracker.getStack();
                }
            } else if (holdingArrowDisplayableProjectileWeapon) {
                if (isProjectileWeapon(ItemHudTracker.getStack().getItem())) {
                    stackToRender = arrow.copy();
                } else {
                    DataComponentMap playerProjectileComponents = playerProjectile.getComponents();
                    stackToRender = new ItemStack(playerProjectile.getItem(), count);
                    stackToRender.applyComponents(playerProjectileComponents);
                }
            }

            drawItem(this.minecraft, graphics, stackToRender, itemX, false);

            int color = hasInfinity ? CommonColors.GREEN : CommonColors.WHITE;
            boolean validArrow = isArrow || arrowAndZero || holdingArrowDisplayableProjectileWeapon;
            if (shouldRenderArrowUi && validArrow && !hasInfinity) {
                color = getCountColor(count);
            }

            if (!hasInfinity && shouldRenderArrowUi && options().hud.warningIndicators && validArrow && count < 6) {
                this.renderWarningIndicator(this.minecraft, graphics, 0, itemX, null);
            }

            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 200);

            int textX = itemX - (textWidth / 2) + 11;
            if (textLength == 1) {
                textX += 3;
            }
            graphics.drawString(this.minecraft.font, text, ((graphics.guiWidth() / 2) + textX), graphics.guiHeight() - (hasInfinity ? 9 : 10), color, true);
            if (options().accessibility.displayTotalWithStacks && count > 64 && (evenStack || options().hud.itemCounter == ItemCounter.STACKS)) {
                graphics.drawString(this.minecraft.font, "(" + String.format("%,d", count) + ")", ((graphics.guiWidth() / 2) + textX), graphics.guiHeight() - 22, color, true);
            }

            graphics.pose().popPose();

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            return true;
        }
        return false;
    }

    /**
     * Tries to render an item on the screen.
     */
    @Unique
    private void tryRenderItem(GuiGraphics graphics, ItemStack mainHandItem, ItemStack offHandItem) {
        this.renderingHeldItem = this.renderItem(graphics, mainHandItem) || this.renderItem(graphics, offHandItem);
    }

    /**
     * @return the x-pos for each armor slot.
     */
    @Unique
    private int getEquipmentSlotX(Minecraft minecraft, EquipmentSlot slot) {
        int base = isLeftHanded(minecraft) ? -277 : 1;
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
    private void renderHighlightedArmorSlot(Minecraft minecraft, ResourceLocation defaultSprite, GuiGraphics graphics, EquipmentSlot slot, boolean warning) {
        graphics.blitSprite(
                warning ? ofQoQ("hud/slot_bad") : getHighlightedSlotTexture(defaultSprite, getItemBySlot(minecraft, slot)),
                this.getHighlightedSlotX(minecraft, graphics, slot),
                getGuiHeight(graphics) - 3,
                24,
                23
        );
    }

    /**
     * Renders the {@code warning texture} around armor items.
     */
    @Unique
    private void renderWarningIndicator(Minecraft minecraft, GuiGraphics graphics, int slot, int itemX, EquipmentSlot equipmentSlot) {
        if (!options().hud.warningIndicators) {
            return;
        }

        graphics.blitSprite(
                ResourceLocation.withDefaultNamespace("world_list/error_highlighted"),
                getGuiWidth(graphics) + (itemX != 0 ? itemX : equipmentSlot == null ? -78 + (slot * 20) : this.getEquipmentSlotX(minecraft, equipmentSlot) + 10),
                getGuiHeight(graphics) + 4,
                16,
                16
        );
    }
}
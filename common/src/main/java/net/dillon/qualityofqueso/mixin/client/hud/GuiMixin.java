package net.dillon.qualityofqueso.mixin.client.hud;

import net.dillon.qualityofqueso.option.ItemCount;
import net.dillon.qualityofqueso.util.ItemHudTracker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
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

import static net.dillon.qualityofqueso.util.GuiUtil.*;
import static net.dillon.qualityofqueso.util.ModUtil.*;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow @Final
    private Minecraft minecraft;
    @Shadow @Final
    private static Identifier HOTBAR_SELECTION_SPRITE;
    @Shadow
    @Final
    private static Identifier HOTBAR_OFFHAND_RIGHT_SPRITE;
    @Unique
    private static final EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    @Unique
    private static final int DISPLAY_TICKS = 80;
    @Unique
    private boolean moveArmorOver = false;
    @Unique
    private boolean isRenderingItem = false;
    @Unique
    private boolean isDisplayingExactStack = false;
    @Unique
    private boolean isDisplayingRemainder = false;
    @Unique
    private boolean renderingHeldItem = false;

    /**
     * Renders the modified selection slot.
     */
    @ModifyArg(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 1), index = 1)
    private Identifier modifyHighlightedSlot(Identifier original) {
        if (this.minecraft.player == null) {
            return original;
        }
        return getHighlightedSlotTexture(HOTBAR_SELECTION_SPRITE, this.minecraft.player.getInventory().getItem(this.minecraft.player.getInventory().getSelectedSlot()));
    }

    /**
     * Renders things overtop of everything.
     */
    @Inject(method = "extractItemHotbar", at = @At("TAIL"))
    private void renderAllWarningIndicators(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        for (int i = 0; i < this.minecraft.player.getInventory().getContainerSize() - 34; i++) {
            ItemStack item = this.minecraft.player.getInventory().getItem(i);
            if (getItemHealthPercentage(item) < 0.11F) {
                this.renderWarningIndicator(this.minecraft, graphics, i, null);
            }
        }

        ItemStack offHandItem = this.minecraft.player.getOffhandItem();
        if (!offHandItem.isEmpty()) {
            if (options().hud.coloredHighlighting && getItemHealthPercentage(offHandItem) < 1.0F) {
                this.renderHighlightedArmorSlot(this.minecraft, HOTBAR_SELECTION_SPRITE, graphics, EquipmentSlot.OFFHAND, false);
            }
            if (options().hud.warningIndicators && getItemHealthPercentage(offHandItem) < 0.11F) {
                this.renderWarningIndicator(this.minecraft, graphics, 0, EquipmentSlot.OFFHAND);
            }
        }
    }

    /**
     * Implements the {@code armor status} feature.
     */
    @Inject(method = "extractItemHotbar", at = @At("HEAD"))
    private void renderArmorStatus(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!modEnabled(this.minecraft) || this.minecraft.player == null) {
            return;
        }

        ItemStack mainHandItem = getItemBySlot(this.minecraft, EquipmentSlot.MAINHAND);
        ItemStack offHandItem = getItemBySlot(this.minecraft, EquipmentSlot.OFFHAND);

        this.tryRenderItem(graphics, mainHandItem, offHandItem);

        int i = 0;
        for (EquipmentSlot slot : slots) {
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

        boolean canRenderArmorHotbar = options().accessibility.armorSlotOutlines && (options().hud.armorStatus || (options().misc.elytraAlarm && SHOULD_WARN_OF_ELYTRA));

        if (canRenderArmorHotbar) {
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    ofQoQ("hud/armor_hotbar"),
                    this.getArmorBarX(this.minecraft, graphics),
                    getGuiHeight(graphics) - 2,
                    82,
                    22
            );
        }

        i = 0;
        for (EquipmentSlot slot : slots) {
            if (options().hud.armorStatus) {
                if (slot != EquipmentSlot.CHEST || !SHOULD_WARN_OF_ELYTRA) {
                    drawItem(this.minecraft, graphics, getItemBySlot(this.minecraft, slot), this.getArmorX(this.minecraft, slot), true);
                    if (this.minecraft.player.tickCount < ARMOR_TIMERS[i]) {
                        this.renderHighlightedArmorSlot(this.minecraft, HOTBAR_SELECTION_SPRITE, graphics, slots[i], false);
                    }
                    if (getItemHealthPercentage(getItemBySlot(this.minecraft, slot)) < 0.11F) {
                        this.renderWarningIndicator(this.minecraft, graphics, 0, slot);
                    }
                }
            }

            if (slot == EquipmentSlot.CHEST && options().misc.elytraAlarm && SHOULD_WARN_OF_ELYTRA) {
                drawItem(this.minecraft, graphics, new ItemStack(Items.ELYTRA), this.getArmorX(this.minecraft, EquipmentSlot.CHEST), true);
                this.renderHighlightedArmorSlot(this.minecraft, HOTBAR_SELECTION_SPRITE, graphics, slot, true);
                this.renderWarningIndicator(this.minecraft, graphics, 0, slot);
            }
            i++;
        }
    }

    /**
     * Renders an item.
     */
    @Unique
    private boolean renderItem(GuiGraphicsExtractor graphics, ItemStack stack) {
        boolean holdingArrowDisplayableProjectileWeapon = holdingArrowDisplayableProjectileWeapon(this.minecraft, stack);
        if (!options().hud.itemCount.enabled() || (!stack.isStackable() && !holdingArrowDisplayableProjectileWeapon)) {
            if (!stack.is(ItemTags.SHULKER_BOXES) && !stack.is(ItemTags.BUNDLES)) {
                return false;
            }
        }

        int count = 0;
        List<Integer> items = new ArrayList<>();
        for (ItemStack invStack : this.minecraft.player.getInventory()) {

            if (options().hud.countContainers && (invStack.is(ItemTags.SHULKER_BOXES) || invStack.is(ItemTags.BUNDLES))) {
                ItemContainerContents container = invStack.get(DataComponents.CONTAINER);
                BundleContents bundleContents = invStack.get(DataComponents.BUNDLE_CONTENTS);

                if (container != null) {
                    for (ItemStackTemplate containerStack : container.nonEmptyItems()) {
                        if (itemMatchesInventoryItem(stack, containerStack.create())) {
                            count += containerStack.count();
                            items.add(containerStack.count());
                        }
                    }
                }
                if (bundleContents != null) {
                    for (ItemStackTemplate bundleStack : bundleContents.items()) {
                        if (itemMatchesInventoryItem(stack, bundleStack.create())) {
                            count += bundleStack.count();
                            items.add(bundleStack.count());
                        }
                    }
                }
            } else if (
                    (options().hud.countAllArrows && options().hud.showArrowCount && (isStackArrow(invStack) && isStackArrow(ItemHudTracker.getStack()) && !this.renderingHeldItem))
                            || (holdingArrowDisplayableProjectileWeapon ? !options().hud.countAllArrows ? invStack.is(getProjectileFromActiveHand(this.minecraft).getItem()) && isStackArrow(invStack) : isStackArrow(invStack) : invStack.is(stack.getItem()))) {
                if (itemMatchesInventoryItem(stack, invStack) || holdingArrowDisplayableProjectileWeapon || (options().hud.countAllArrows && isStackArrow(ItemHudTracker.getStack()))) {
                    count += invStack.getCount();
                    items.add(invStack.getCount());
                }
            }
        }

        boolean trackedArrow = options().hud.showArrowCount && (holdingArrowDisplayableProjectileWeapon || isStackArrow(ItemHudTracker.getStack()));
        int maxCount = trackedArrow ? 64 : stack.getMaxStackSize();

        this.isDisplayingExactStack = false;
        this.isDisplayingRemainder = false;

        if (count <= 0 && !trackedArrow) {
            return false;
        }

        int fullStacks = 0;
        if (!stack.isEmpty() || trackedArrow) {
            String text = String.valueOf(count);

            boolean hasInfinity = false;
            if (hasInfinity(stack) && getProjectileFromActiveHand(this.minecraft).is(Items.ARROW) && holdingArrowDisplayableProjectileWeapon && trackedArrow) {
                text = "∞";
                hasInfinity = true;
            }

            String maxItemCount = String.valueOf(maxCount);
            boolean evenStack = count != 0 && count != 64 && count % maxCount == 0;
            boolean displayStack = options().hud.itemCount == ItemCount.STACKS && evenStack;
            this.isDisplayingExactStack = evenStack;
            if (!hasInfinity && count > maxCount) {
                if (displayStack) {
                    text = count / maxCount + "x " + maxItemCount;
                } else if (options().hud.itemCount == ItemCount.REMAINDER) {
                    int totalCount = 0;
                    for (int i : items) {
                        totalCount += i;
                    }

                    fullStacks = totalCount / maxCount;
                    int remainder = totalCount % maxCount;
                    this.isDisplayingRemainder = remainder != 0;

                    String additional = remainder > 0 ? " & " + remainder : "";

                    text = fullStacks + "x" + (remainder == 0 ? " " : "") + maxItemCount + additional;

                    this.moveArmorOver = fullStacks > 99;
                }
            }
            DataComponentMap components = stack.getComponents();
            ItemStack newStack = new ItemStack(stack.getItem(), count);
            newStack.applyComponents(components);

            HumanoidArm offhandArm = this.minecraft.player.getMainArm().getOpposite();
            boolean isArrow = isStackArrow(newStack);
            boolean arrowDisplayValid = (isArrow || holdingArrowDisplayableProjectileWeapon) && options().hud.itemCount == ItemCount.REMAINDER ? count < 65 : count < 100;
            boolean shouldRenderArrowUi = options().hud.showArrowCount && !hasInfinity && arrowDisplayValid && (holdingArrowDisplayableProjectileWeapon || !this.renderingHeldItem);
            if (shouldRenderArrowUi && (isArrow || holdingArrowDisplayableProjectileWeapon)) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_RIGHT_SPRITE,
                        getGuiWidth(graphics) + 90,
                        getGuiHeight(graphics) - 3,
                        29,
                        24
                );
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                        options().hud.coloredHighlighting
                                ? count < 11 ? ofQoQ("hud/slot_bad")
                                : count < 21 ? ofQoQ("hud/slot_ok")
                                : ofQoQ("hud/slot_good")
                                : HOTBAR_SELECTION_SPRITE,
                        getGuiWidth(graphics) + 96,
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
            drawItem(this.minecraft, graphics, stackToRender, offhandArm == HumanoidArm.RIGHT ? -129 : 100, false);

            int x = 105;
            if (hasInfinity) {
                x += 5;
            } else if (count < 10) {
                x += 6;
            } else if (count > 999) {
                x -= 8;
            } else if (count > 99 || (options().hud.itemCount == ItemCount.REMAINDER && this.isDisplayingRemainder)) {
                x -= 5;
            }
            if (isLeftHanded(this.minecraft)) {
                if (count > 64 && options().hud.itemCount == ItemCount.REMAINDER) {
                    x += 25;
                    if (fullStacks > 99) {
                        x += 14;
                    } else if (fullStacks > 9) {
                        x += 5;
                    }
                } else if (count / maxCount > 9 && options().hud.itemCount == ItemCount.STACKS) {
                    x += 20;
                } else if (count > 999) {
                    x += 8;
                } else if (count > 99) {
                    x += 5;
                }
            }
            int color = hasInfinity ? CommonColors.GREEN : CommonColors.WHITE;
            boolean validArrow = isArrow || arrowAndZero || holdingArrowDisplayableProjectileWeapon;
            if (shouldRenderArrowUi && validArrow && !hasInfinity) {
                if (count < 6) {
                    color = CommonColors.RED;
                } else if (count < 11) {
                    color = CommonColors.SOFT_RED;
                } else if (count < 21) {
                    color = CommonColors.YELLOW;
                } else {
                    color = CommonColors.GREEN;
                }
            }
            if (!hasInfinity && shouldRenderArrowUi && options().hud.warningIndicators && validArrow && count < 6) {
                this.renderWarningIndicator(this.minecraft, graphics, (int)(9 * 1.01), null);
            }
            graphics.text(this.minecraft.font, text, ((graphics.guiWidth() / 2) + (isLeftHanded(this.minecraft) ? -x - 18 : x)), graphics.guiHeight() - (hasInfinity ? 9 : 10), color, true);
            if (options().accessibility.displayTotalWithStacks && count > 64 && (displayStack || options().hud.itemCount == ItemCount.REMAINDER)) {
                graphics.text(this.minecraft.font, "(" + String.format("%,d", count) + ")", ((graphics.guiWidth() / 2) + ((isLeftHanded(this.minecraft) ? -x - 16 : x) + 5)), graphics.guiHeight() - 22, color, true);
            }
            return true;
        }
        return false;
    }

    /**
     * Tries to render an item on the screen.
     */
    @Unique
    private void tryRenderItem(GuiGraphicsExtractor graphics, ItemStack mainHandItem, ItemStack offHandItem) {
        ItemStack stack = ItemHudTracker.getStack();
        this.renderingHeldItem = this.renderItem(graphics, mainHandItem) || this.renderItem(graphics, offHandItem);
        this.isRenderingItem = this.renderingHeldItem || (!stack.isEmpty() && this.renderItem(graphics, stack));
    }

    /**
     * @return the x-pos for each armor slot.
     */
    @Unique
    private int getArmorX(Minecraft minecraft, EquipmentSlot slot) {
        int base = !options().hud.itemCount.enabled() ? 0 : 32;
        if (this.isRenderingItem && !isLeftHanded(minecraft)) {
            if (options().hud.itemCount == ItemCount.STACKS && this.isDisplayingExactStack) {
                base += 14;
            } else if (options().hud.itemCount == ItemCount.REMAINDER) {
                base += this.isDisplayingRemainder ? 32 : this.isDisplayingExactStack ? 14 : 0;
                if (this.moveArmorOver) {
                    base += 4;
                }
            }
        }
        return switch (slot) {
            case HEAD -> base + 100;
            case CHEST -> base + 120;
            case LEGS -> base + 140;
            case FEET -> base + 160;
            case OFFHAND -> base + (isLeftHanded(this.minecraft) ? 69 : -149);
            default -> 0;
        };
    }

    /**
     * @return the x-position that the slot should render under the armor item.
     */
    @Unique
    private int getArmorBarX(Minecraft minecraft, GuiGraphicsExtractor graphics) {
        return getGuiWidth(graphics) + this.getArmorX(minecraft, EquipmentSlot.HEAD) - 3;
    }

    /**
     * @return the x-position that the highlighted slot should render under the armor item.
     */
    @Unique
    private int getHighlightedSlotX(Minecraft minecraft, GuiGraphicsExtractor graphics, EquipmentSlot slot) {
        return getGuiWidth(graphics) + this.getArmorX(minecraft, slot) - 4;
    }

    /**
     * Renders the highlighted texture around a slot.
     */
    @Unique
    private void renderHighlightedArmorSlot(Minecraft minecraft, Identifier defaultSprite, GuiGraphicsExtractor graphics, EquipmentSlot slot, boolean warning) {
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
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
    private void renderWarningIndicator(Minecraft minecraft, GuiGraphicsExtractor graphics, int slot, EquipmentSlot equipmentSlot) {
        if (!options().hud.warningIndicators) {
            return;
        }

        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                Identifier.withDefaultNamespace("world_list/error_highlighted"),
                getGuiWidth(graphics) + (equipmentSlot == null ? -78 + (slot * 20) : this.getArmorX(minecraft, equipmentSlot) + 10),
                getGuiHeight(graphics) + 4,
                16,
                16
        );
    }
}

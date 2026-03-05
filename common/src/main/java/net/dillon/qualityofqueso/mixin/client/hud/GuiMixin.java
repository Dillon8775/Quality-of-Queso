package net.dillon.qualityofqueso.mixin.client.hud;

import net.dillon.qualityofqueso.option.ArmorStatus;
import net.dillon.qualityofqueso.option.ItemCount;
import net.dillon.qualityofqueso.util.PickupHudTracker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.dillon.qualityofqueso.util.ModUtil.modEnabled;
import static net.dillon.qualityofqueso.util.ModUtil.options;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow @Final
    private Minecraft minecraft;
    @Unique
    private final Map<EquipmentSlot, ItemStack> lastArmorStacks = new HashMap<>();
    @Unique
    private final Map<EquipmentSlot, Integer> armorTimers = new HashMap<>();
    @Unique
    private static final EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    @Unique
    private static final int ARMOR_RENDER_TICKS = 2000;
    @Unique
    private boolean moveArmorOver = false;

//    /**
//     * Implements the {@link ArmorStatus} feature.
//     */
//    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"))
//    private void renderArmorStatus(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
//        if (!modEnabled(this.minecraft) || this.minecraft.player == null) {
//            return;
//        }
//
//        ItemStack mainHandItem = this.getItemBySlot(EquipmentSlot.MAINHAND);
//        ItemStack offHandItem = this.getItemBySlot(EquipmentSlot.OFFHAND);
//        if (!this.renderItem(context, mainHandItem)) {
//            if (!this.renderItem(context, offHandItem)) {
//                ItemStack stack = PickupHudTracker.getStack();
//                if (!stack.isEmpty()) {
//                    this.renderItem(context, stack);
//                }
//            }
//        }
//
//        if (!options().armorStatus.enabled()) {
//            return;
//        }
//
//        for (EquipmentSlot slot : this.slots) {
//            ItemStack current = this.getItemBySlot(slot);
//
//            if (this.armorChanged(slot, current)) {
//                this.armorTimers.put(slot, ARMOR_RENDER_TICKS);
//                this.lastArmorStacks.put(slot, current.copy());
//            }
//
//            int timer = this.armorTimers.getOrDefault(slot, 0);
//            if (timer > 0 || options().armorStatus == ArmorStatus.ALWAYS) {
//                this.drawItem(context, this.getItemBySlot(slot), getArmorX(slot), true);
//                this.armorTimers.put(slot, timer - 1);
//            }
//        }
//    }

    /**
     * Renders an item.
     */
    @Unique
    private boolean renderItem(GuiGraphics context, ItemStack stack) {
        if (!options().itemCount.enabled() || !stack.isStackable()) {
            if (!stack.is(ItemTags.SHULKER_BOXES) && !stack.is(ItemTags.BUNDLES)) {
                return false;
            }
        }

        int count = 0;
        List<Integer> items = new ArrayList<>();
        for (ItemStack invStack : this.minecraft.player.getInventory()) {
            if (options().countContainers && (invStack.is(ItemTags.SHULKER_BOXES) || invStack.is(ItemTags.BUNDLES))) {
                ItemContainerContents container = invStack.get(DataComponents.CONTAINER);
                BundleContents bundleContents = invStack.get(DataComponents.BUNDLE_CONTENTS);

                if (container != null) {
                    for (ItemStackTemplate containerStack : container.nonEmptyItems()) {
                        if (containerStack.is(stack.getItem())) {
                            count += containerStack.count();
                            items.add(containerStack.count());
                        }
                    }
                }
                if (bundleContents != null) {
                    for (ItemStackTemplate bundleStack : bundleContents.items()) {
                        if (bundleStack.is(stack.getItem())) {
                            count += bundleStack.count();
                            items.add(bundleStack.count());
                        }
                    }
                }
            } else if (invStack.is(stack.getItem())) {
                count += invStack.getCount();
                items.add(invStack.getCount());
            }
        }

        if (count <= 0) {
            return false;
        }

        int maxCount = stack.getMaxStackSize();
        if (!stack.isEmpty()) {
            String text = String.valueOf(count);
            String maxItemCount = String.valueOf(maxCount);
            boolean displayStack = options().itemCount == ItemCount.STACKS && count % maxCount == 0;
            if (count > maxCount) {
                if (displayStack) {
                    text = count / maxCount + "x " + maxItemCount;
                } else if (options().itemCount == ItemCount.REMAINDER) {
                    int totalCount = 0;
                    for (int i : items) {
                        totalCount += i;
                    }

                    int fullStacks = totalCount / maxCount;
                    int remainder = totalCount % maxCount;

                    String additional = remainder > 0 ? " & " + remainder : "";

                    text = fullStacks + "x" + (remainder == 0 ? " " : "") + maxItemCount + additional;

                    this.moveArmorOver = fullStacks > 99;
                }
            }
            ItemStack newStack = new ItemStack(stack.getItem(), count);
            this.drawItem(context, newStack, 100, false);
            int x = 105;
            if (count < 10) {
                x += 6;
            } else if (count > 999) {
                x -= 8;
            } else if (count > 99) {
                x -= 5;
            }
            context.drawString(this.minecraft.font, text, ((context.guiWidth() / 2) + x), context.guiHeight() - 11, CommonColors.WHITE, true);
            if (options().displayTotalWithStacks && count > 64 && (displayStack || options().itemCount == ItemCount.REMAINDER)) {
                context.drawString(this.minecraft.font, "(" + String.format("%,d", count) + ")", ((context.guiWidth() / 2) + (x + 5)), context.guiHeight() - 23, CommonColors.WHITE, true);
            }
            return true;
        }
        return false;
    }

    /**
     * @return if a armor slot was changed at all.
     */
    @Unique
    private boolean armorChanged(EquipmentSlot slot, ItemStack current) {
        ItemStack previous = this.lastArmorStacks.get(slot);

        if (previous == null) {
            return true;
        }

        if (!ItemStack.isSameItem(previous, current)) {
            return true;
        }

        return previous.getDamageValue() != current.getDamageValue();
    }

    /**
     * @return the x-pos for each armor slot.
     */
    @Unique
    private int getArmorX(EquipmentSlot slot) {
        int base = !options().itemCount.enabled() ? 0 : 20;
        if (options().itemCount == ItemCount.STACKS) {
            base += 14;
        } else if (options().itemCount == ItemCount.REMAINDER) {
            base += 36;
            if (this.moveArmorOver) {
                base += 4;
            }
        }
        return switch (slot) {
            case HEAD -> base + 100;
            case CHEST -> base + 120;
            case LEGS -> base + 140;
            case FEET -> base + 160;
            default -> 0;
        };
    }

    /**
     * Draws an equipped stack item.
     */
    @Unique
    private void drawItem(GuiGraphics context, ItemStack stack, int x, boolean overlay) {
        int i = context.guiWidth() / 2;
        int y = context.guiHeight() - 20;
        int fx = i + x;
        if (!stack.isEmpty()) {
            context.renderItem(stack, fx, y);
            if (overlay) {
                context.renderItemDecorations(this.minecraft.font, stack, fx, y, null);
            }
        }
    }

    /**
     * @return the item in the given slot.
     */
    @Unique
    private ItemStack getItemBySlot(EquipmentSlot slot) {
        return this.minecraft.player.getItemBySlot(slot);
    }
}
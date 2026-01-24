package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.option.ArmorStatus;
import net.dillon.qualityofqueso.option.ItemCount;
import net.dillon.qualityofqueso.util.PickupHudTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Colors;
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

import static net.dillon.qualityofqueso.main.QoQ.modEnabled;
import static net.dillon.qualityofqueso.main.QoQ.options;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow @Final
    private MinecraftClient client;
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

    /**
     * Implements the {@link ArmorStatus} feature.
     */
    @Inject(method = "renderMainHud", at = @At("HEAD"))
    private void renderArmorStatus(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!modEnabled(this.client) || this.client.player == null) {
            return;
        }

        ItemStack mainHandItem = this.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack offHandItem = this.getItemBySlot(EquipmentSlot.OFFHAND);
        if (!this.renderItem(context, mainHandItem)) {
            if (!this.renderItem(context, offHandItem)) {
                ItemStack stack = PickupHudTracker.getStack();
                if (!stack.isEmpty()) {
                    this.renderItem(context, stack);
                }
            }
        }

        if (!options().armorStatus.enabled()) {
            return;
        }

        for (EquipmentSlot slot : slots) {
            ItemStack current = this.getItemBySlot(slot);

            if (this.armorChanged(slot, current)) {
                this.armorTimers.put(slot, ARMOR_RENDER_TICKS);
                this.lastArmorStacks.put(slot, current.copy());
            }

            int timer = this.armorTimers.getOrDefault(slot, 0);
            if (timer > 0 || options().armorStatus == ArmorStatus.ALWAYS) {
                this.drawItem(context, this.getItemBySlot(slot), getArmorX(slot), true);
                this.armorTimers.put(slot, timer - 1);
            }
        }
    }

    /**
     * Renders an item.
     */
    @Unique
    private boolean renderItem(DrawContext context, ItemStack stack) {
        if (!options().itemCount.enabled() || !stack.isStackable()) {
            if (!stack.isIn(ItemTags.SHULKER_BOXES) && !stack.isIn(ItemTags.BUNDLES)) {
                return false;
            }
        }

        int count = 0;
        List<Integer> items = new ArrayList<>();
        for (ItemStack invStack : this.client.player.getInventory()) {
            if (options().countContainers && (invStack.isIn(ItemTags.SHULKER_BOXES) || invStack.isIn(ItemTags.BUNDLES))) {
                ContainerComponent container = invStack.get(DataComponentTypes.CONTAINER);
                BundleContentsComponent bundleContents = invStack.get(DataComponentTypes.BUNDLE_CONTENTS);

                if (container != null) {
                    for (ItemStack containerStack : container.iterateNonEmpty()) {
                        if (containerStack.isOf(stack.getItem())) {
                            count += containerStack.getCount();
                            items.add(containerStack.getCount());
                        }
                    }
                }
                if (bundleContents != null) {
                    for (ItemStack bundleStack : bundleContents.iterate()) {
                        if (bundleStack.isOf(stack.getItem())) {
                            count += bundleStack.getCount();
                            items.add(bundleStack.getCount());
                        }
                    }
                }
            } else if (invStack.isOf(stack.getItem())) {
                count += invStack.getCount();
                items.add(invStack.getCount());
            }
        }

        if (count <= 0) {
            return false;
        }

        int maxCount = stack.getMaxCount();
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
            context.drawText(this.client.textRenderer, text, ((context.getScaledWindowWidth() / 2) + x), context.getScaledWindowHeight() - 11, Colors.WHITE, true);
            if (options().displayTotalWithStacks && count > 64 && (displayStack || options().itemCount == ItemCount.REMAINDER)) {
                context.drawText(this.client.textRenderer, "(" + String.format("%,d", count) + ")", ((context.getScaledWindowWidth() / 2) + (x + 5)), context.getScaledWindowHeight() - 23, Colors.WHITE, true);
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

        if (!ItemStack.areItemsEqual(previous, current)) {
            return true;
        }

        return previous.getDamage() != current.getDamage();
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
    private void drawItem(DrawContext context, ItemStack stack, int x, boolean overlay) {
        int i = context.getScaledWindowWidth() / 2;
        int y = context.getScaledWindowHeight() - 20;
        int fx = i + x;
        if (!stack.isEmpty()) {
            context.drawItem(stack, fx, y);
            if (overlay) {
                context.drawStackOverlay(this.client.textRenderer, stack, fx, y, null);
            }
        }
    }

    /**
     * @return the item in the given slot.
     */
    @Unique
    private ItemStack getItemBySlot(EquipmentSlot slot) {
        return this.client.player.getEquippedStack(slot);
    }
}
package net.dillon.qualityofqueso.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;
import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * Utility class for GUI-related things.
 */
public class GuiUtil {
    public static double PLAYER_FALL_DISTANCE;
    public static boolean SHOULD_WARN_OF_ELYTRA;
    public static final int[] ARMOR_TIMERS = new int[4];
    public static final ItemStack[] LAST_ARMOR_STACKS = new ItemStack[4];

    /**
     * Draws an equipped stack item.
     */
    public static void drawItem(Minecraft minecraft, GuiGraphics context, ItemStack stack, int x, boolean overlay) {
        int i = getGuiWidth(context);
        int y = getGuiHeight(context) + 1;
        int fx = i + x;
        if (!stack.isEmpty()) {
            context.renderItem(stack, fx, y);
            if (overlay) {
                context.renderItemDecorations(minecraft.font, stack, fx, y, null);
            }
        }
    }

    /**
     * @return if a armor slot was changed at all.
     */
    public static boolean armorChanged(int slotIndex, ItemStack current) {
        ItemStack previous = LAST_ARMOR_STACKS[slotIndex];
        return !ItemStack.isSameItemSameComponents(previous, current);
    }

    /**
     * @return if the stack is an arrow.
     */
    public static boolean isStackArrow(ItemStack stack) {
        return stack.is(ItemTags.ARROWS);
    }

    /**
     * @return if projectile from either of the player's hand.
     */
    public static ItemStack getProjectileFromActiveHand(Minecraft minecraft) {
        ItemStack offHandItem = minecraft.player.getOffhandItem();
        if (isProjectileWeapon(offHandItem.getItem())) {
            return minecraft.player.getProjectile(offHandItem);
        } else {
            return minecraft.player.getProjectile(minecraft.player.getMainHandItem());
        }
    }

    /**
     * @return if stack is a projectile weapon.
     */
    public static boolean isProjectileWeapon(Item item) {
        return item instanceof BowItem || item instanceof CrossbowItem;
    }

    /**
     * @return if the arrow count can be displayed at all.
     */
    public static boolean holdingArrowDisplayableProjectileWeapon(Minecraft minecraft, ItemStack stack) {
        return options().showArrowCount && !minecraft.player.isCreative() && (stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem);
    }

    /**
     * @return the correct sprite to use.
     */
    public static Identifier getHighlightedSlotTexture(Identifier defaultSprite, ItemStack stack) {
        float healthPercentage = getItemHealthPercentage(stack);

        if (!options().coloredHighlighting) {
            return defaultSprite;
        } else if (healthPercentage < 0.21F) {
            return ofQoQ("hud/slot_bad");
        } else if (healthPercentage < 0.41F) {
            return ofQoQ("hud/slot_average");
        } else if (healthPercentage < 0.61F) {
            return ofQoQ("hud/slot_ok");
        } else if (healthPercentage < 1.0F) {
            return ofQoQ("hud/slot_good");
        }
        return defaultSprite;
    }

    /**
     * @return the health percentage (or durability %) of an item.
     */
    public static float getItemHealthPercentage(ItemStack stack) {
        int maxDamage = stack.getMaxDamage();
        return Math.max(0.0F, ((float)maxDamage - stack.getDamageValue()) / maxDamage);
    }

    /**
     * @return the item in the given slot.
     */
    public static ItemStack getItemBySlot(Minecraft minecraft, EquipmentSlot slot) {
        return minecraft.player.getItemBySlot(slot);
    }

    /**
     * @return the GUI width.
     */
    public static int getGuiWidth(GuiGraphics context) {
        return context.guiWidth() / 2;
    }

    /**
     * @return the GUI height.
     */
    public static int getGuiHeight(GuiGraphics context) {
        return context.guiHeight() - 20;
    }
}
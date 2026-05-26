package net.dillon.qualityofqueso.instance.management;

import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.quicklyEquippables;
import static net.dillon.qualityofqueso.helper.ModKeybindHelper.hasSelectSlotsKeyDown;

/**
 * Handles quick equip feature and methods.
 */
public class QuickEquipInstance extends ManagementInstance {

    public QuickEquipInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * @return {@code true} if the hovered item is a {@code quickly equippable item.}
     */
    public boolean isQuicklyEquippable(ItemStack stack) {
        for (TagKey<Item> quicklyEquippable : quicklyEquippables().keySet()) {
            if (stack.is(quicklyEquippable) || stack.is(Items.ELYTRA)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Quickly swaps two items in the player's fromInventory.
     */
    public void quickSwap(int sourceSlot, EquipmentSlot slot) {
        // Slot index for armor - see PlayerScreenHandler for proof of these values
        int slotIndex = slot == EquipmentSlot.HEAD ? 5 : slot == EquipmentSlot.CHEST ? 6 : slot == EquipmentSlot.LEGS ? 7 : slot == EquipmentSlot.FEET ? 8 : 6;
        sendSwapSlotPacket(sourceSlot, slotIndex);
    }

    /**
     * Quickly equips an item.
     */
    public void quickEquip() {
        if (clientOptionsInstance().getManagementOptions().dragSorting && hasSelectSlotsKeyDown()) {
            return;
        }

        if (clientOptionsInstance().getMiscOptions().quickEquip && instance().getScreensHoveredSlot() != null && (instance().getScreensHoveredSlot().index >= 5) && (instance().getScreen() instanceof InventoryScreen || instance().getScreen() instanceof CreativeModeInventoryScreen)) {
            ItemStack stack = instance().getScreensHoveredSlot().getItem();
            EquipmentSlot targetSlot = null;

            for (TagKey<Item> quicklyEquippable : quicklyEquippables().keySet()) {
                if (stack.is(quicklyEquippable)) {
                    targetSlot = quicklyEquippables().get(quicklyEquippable);
                }
            }

            if (stack.is(Items.ELYTRA)) {
                targetSlot = EquipmentSlot.CHEST;
            }

            if (targetSlot != null) {
                Minecraft client = Minecraft.getInstance();
                ItemStack equippedStack = client.player.getItemBySlot(targetSlot);
                if (equippedStack.isEmpty()) {
                    sendClickSlotPacket(instance().getScreensHoveredSlot().index, ClickType.QUICK_MOVE);
                } else {
                    quickSwap(instance().getScreensHoveredSlot().index, targetSlot);
                }
            }
        }
    }
}
package net.dillon.qualityofqueso.instance.management;

import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
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
        return stack.getItem() instanceof Equipable || isNotEquippable(stack);
    }

    /**
     * @return if {@code stack} is not equippable, but preferred in the offhand.
     */
    public boolean isNotEquippable(ItemStack stack) {
        return stack.is(Items.TOTEM_OF_UNDYING) || stack.is(Items.FILLED_MAP);
    }

    /**
     * Finds the equippable slot for a {@code player.}
     */
    private int findEquipmentSlotForPlayer(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 5;
            case CHEST -> 6;
            case LEGS -> 7;
            case FEET -> 8;
            case OFFHAND -> 45;
            default -> -1;
        };
    }

    /**
     * Finds the equippable slot for a {@code mountable entity.}
     */
    private int findEquipmentSlotForMountable(ItemStack stack) {
        return isMountableSlot(stack) ? 1 : -1;
    }

    /**
     * @return if the equipment slot is a mountable equipment slot.
     */
    private boolean isMountableSlot(ItemStack stack) {
        return stack.getItem() instanceof HorseArmorItem;
    }

    /**
     * @return the slot index of the item to equip.
     */
    private int findSlotToEquip(AbstractContainerMenu menu, ItemStack stack, EquipmentSlot slot) {
        return isHorseMenu(menu) ? findEquipmentSlotForMountable(stack) : findEquipmentSlotForPlayer(slot);
    }

    /**
     * Quickly equips the hovered item, based on the current menu.
     */
    public void quickEquipItem() {
        Minecraft minecraft = Minecraft.getInstance();
        AbstractContainerMenu menu = instance().getScreenMenu();
        if (!clientOptionsInstance().getManagementOptions().quickEquip || instance().getScreensHoveredSlot() == null || !isValidMenuForQuickEquipping(menu) || (clientOptionsInstance().getManagementOptions().dragSorting && hasSelectSlotsKeyDown())) {
            return;
        }

        // Get the screens hovered slot
        Slot hoveredSlot = instance().getScreensHoveredSlot();
        ItemStack stack = hoveredSlot.getItem();

        // Check if item isn't equippable
        boolean notEquippable = isNotEquippable(stack);

        // Get the target equipment slot for the hovered item
        EquipmentSlot targetEquipmentSlot = EquipmentSlot.OFFHAND;
        if (stack.getItem() instanceof Equipable equipable) {
            targetEquipmentSlot = equipable.getEquipmentSlot();
        }

        // Get the player's currently equipped stack, based on hovered item
        ItemStack equippedStack = minecraft.player.getItemBySlot(targetEquipmentSlot);

        if (equippedStack.isEmpty() && !isMountableSlot(stack) && (isInventoryScreen(instance().getScreen()) || isCreativeInventoryScreen(instance().getScreen()))) {
            // Default to quick move if slot is empty, only do this if valid equipment slot for player (excluding body and saddle)
            sendClickSlotPacket(hoveredSlot.index, ClickType.QUICK_MOVE);
        } else {
            // Get target equipment slot for either player or mountable
            int targetSlot = findSlotToEquip(menu, stack, targetEquipmentSlot);
            // Ensure we only quick equip if the slot is valid, and the hovered slot is not an equipment slot
            if (targetSlot != -1 && targetSlot != hoveredSlot.index) {
                sendSwapSlotPacket(hoveredSlot.index, targetSlot);
            }
        }

        // Play sound if not equippable
        if (notEquippable) {
            playQuickEquipSound(minecraft);
        }
    }
}
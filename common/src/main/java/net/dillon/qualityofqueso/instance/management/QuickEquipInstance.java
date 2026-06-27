package net.dillon.qualityofqueso.instance.management;

import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.hasSelectSlotsKeyDown;

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
        return stack.has(DataComponents.EQUIPPABLE) || isNotEquippable(stack);
    }

    /**
     * @return if {@code stack} is not equippable, but preferred in the offhand.
     */
    public boolean isNotEquippable(ItemStack stack) {
        return stack.has(DataComponents.DEATH_PROTECTION) || stack.has(DataComponents.MAP_DECORATIONS);
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
    private int findEquipmentSlotForMountable(EquipmentSlot slot) {
        return switch (slot) {
            case SADDLE -> 0;
            case BODY -> 1;
            default -> -1;
        };
    }

    /**
     * @return if the equipment slot is a mountable equipment slot.
     */
    private boolean isMountableSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.BODY || slot == EquipmentSlot.SADDLE;
    }

    /**
     * @return the slot index of the item to equip.
     */
    private int findSlotToEquip(AbstractContainerMenu menu, EquipmentSlot slot) {
        return isMountingMenu(menu) ? findEquipmentSlotForMountable(slot) : findEquipmentSlotForPlayer(slot);
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
        EquipmentSlot targetEquipmentSlot = notEquippable ? EquipmentSlot.OFFHAND : stack.get(DataComponents.EQUIPPABLE).slot();

        // Get the player's currently equipped stack, based on hovered item
        ItemStack equippedStack = minecraft.player.getItemBySlot(targetEquipmentSlot);

        if (equippedStack.isEmpty() && !notEquippable && !isMountableSlot(targetEquipmentSlot) && !isMountingMenu(menu)) {
            // Default to quick move if slot is empty, only do this if valid equipment slot for player (excluding body and saddle), and excluding death protection items
            sendClickSlotPacket(hoveredSlot.index, ClickType.QUICK_MOVE);
        } else {
            // Get target equipment slot for either player or mountable
            int targetSlot = findSlotToEquip(menu, targetEquipmentSlot);
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
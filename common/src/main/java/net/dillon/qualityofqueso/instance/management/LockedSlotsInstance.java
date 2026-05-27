package net.dillon.qualityofqueso.instance.management;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Set;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.key;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;
import static net.dillon.qualityofqueso.helper.ModKeybindHelper.*;
import static net.dillon.qualityofqueso.keybind.ModKeybinds.LOCK_SLOT;

/**
 * Handles locked slot colors, overlays, and functions.
 */
public class LockedSlotsInstance extends ManagementInstance {

    public LockedSlotsInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * @return the set of locked container slots.
     */
    public Set<Integer> getLockedContainerSlots() {
        return clientOptionsInstance().getLockedSlotOptions().lockedSlots ? ContainerHelper.getLockedSlots(true) : Collections.emptySet();
    }

    /**
     * @return the set of locked player slots.
     */
    public Set<Integer> getLockedPlayerSlots() {
        return clientOptionsInstance().getLockedSlotOptions().lockedSlots ? ContainerHelper.getLockedSlots(false) : Collections.emptySet();
    }

    /**
     * @return if the slot passed in is "locked".
     */
    public boolean isLockedSlot(int slotIndex) {
        boolean containerSlot = isContainerScreen(instance().getScreen()) && slotIndex < getContainerSize();
        if (containerSlot) {
            return getLockedContainerSlots().contains(slotIndex);
        } else {
            int playerSlotId = toPlayerLockSlotId(slotIndex);
            return playerSlotId != -1 && getLockedPlayerSlots().contains(playerSlotId);
        }
    }

    /**
     * @return if the user is attempting to drop an entire locked slot stack.
     */
    public boolean droppingEntireLockedSlotStack() {
        return Screen.hasControlDown() && hasDropOnlyOneItemKeyDown() && instance().getScreensHoveredSlot() != null && lockedSlotsInstance().isLockedSlot(instance().getScreensHoveredSlot().index);
    }

    /**
     * @return if a locked slot drop full stack is valid.
     */
    @Deprecated
    public boolean shouldCancelDrop() {
        if (lockedSlotsInstance().droppingEntireLockedSlotStack()
                ? lockedSlotsInstance().droppingEntireLockedSlotStack() && canScrollMoveAndHasScrollModifierDown()
                : canScrollMoveAndHasScrollModifierDown() && !hasDropOnlyOneItemKeyDown()) {
            return !(canScrollMoveAndHasScrollModifierDown() && hasDropOnlyOneItemKeyDown() && Screen.hasAltDown());
        }
        return false;
    }

    /**
     * Renders the unlocked slot texture over slots.
     */
    public void renderUnlockedSlot(GuiGraphics graphics, boolean isSlotLocked, int mouseX, int mouseY) {
        int xy = 10;
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 400.0F);
        graphics.blit(ofQoQ("textures/gui/sprites/locked_slot/" + (isSlotLocked ? "key" : "unlock") + ".png"), mouseX - 6, mouseY + 2, 0.0F, 0.0F, xy, xy, xy, xy);
        graphics.pose().popPose();
    }

    /**
     * Renders a slot as "locked".
     */
    public void renderLockedSlot(GuiGraphics graphics, Slot slot, boolean lockOnly) {
        boolean containerSlot = isContainerScreen(instance().getScreen()) && slot.index < getContainerSize();
        boolean locked;
        if (containerSlot) {
            locked = getLockedContainerSlots().contains(slot.index);
        } else {
            int playerSlotId = toPlayerLockSlotId(slot.index);
            locked = playerSlotId != -1 && getLockedPlayerSlots().contains(playerSlotId);
        }
        if (locked) {
            if (lockOnly) {
                if (slot.hasItem()) {
                    int xy = 10;
                    graphics.pose().pushPose();
                    graphics.pose().translate(0.0F, 0.0F, 400.0F);
                    graphics.blit(ofQoQ("textures/gui/sprites/locked_slot/locked.png"), slot.x - 3, slot.y + 9, 0.0F, 0.0F, xy, xy, xy, xy);
                    graphics.pose().popPose();
                }
            } else {
                graphics.fill(slot.x - 1, slot.y - 1, slot.x + 17, slot.y + 17, clientOptionsInstance().getLockedSlotOptions().lockedSlotColor);
            }
        }
    }

    /**
     * Maps a screen-specific slot index to a stable player lock-slot id.
     * Hotbar slots map to 0-8 and main inventory to 9-35.
     *
     * @return -1 for non-player-storage slots (armor/crafting/offhand/etc).
     */
    public int toPlayerLockSlotId(int slotIndex) {
        if (isInventoryScreen(instance().getScreen())) {
            if (slotIndex >= 36 && slotIndex <= 44) {
                return slotIndex - 36;
            }
            if (slotIndex >= 9 && slotIndex <= 35) {
                return slotIndex;
            }
            return -1;
        }

        int totalSlots = getTotalSlots();
        int hotbarStart = totalSlots - 9;
        int playerMainStart = totalSlots - 36;
        if (slotIndex >= hotbarStart && slotIndex < totalSlots) {
            return slotIndex - hotbarStart;
        }
        if (slotIndex >= playerMainStart && slotIndex < hotbarStart) {
            return 9 + (slotIndex - playerMainStart);
        }
        return -1;
    }

    /**
     * Selects and/or locks slots in a container or player inventory.
     */
    public void selectOrLockSlot(int bl, CallbackInfoReturnable<Boolean> cir) {
        Slot slot = instance().getScreensHoveredSlot();

        boolean notExcluding = Screen.hasAltDown() && !Screen.hasShiftDown();
        boolean lockingSlot = bl == key(LOCK_SLOT).getValue() && Screen.hasAltDown();

        if (slot == null) {
            return;
        }

        if (isValidScreen(instance().getScreen()) && clientOptionsInstance().getLockedSlotOptions().lockedSlots && notExcluding && hasLockSlotModifierDown() && lockingSlot) {
            if (instance().getLastLockedSlotIndex() != slot.index) {
                if (instance().getLockDragAction() == 0) {
                    instance().setLockDragAction(isLockedSlot(slot.index) ? -1 : 1);
                }

                boolean shouldLock = instance().getLockDragAction() == 1;
                boolean slotLocked = isLockedSlot(slot.index);
                boolean containerSlot = isContainerScreen(instance().getScreen()) && slot.index < getContainerSize();
                boolean shouldToggle = shouldLock != slotLocked;

                if (shouldToggle) {
                    if (containerSlot) {
                        ContainerHelper.toggleLockedSlot(true, slot.index);
                    } else {
                        int playerSlotId = toPlayerLockSlotId(slot.index);
                        if (playerSlotId != -1) {
                            ContainerHelper.toggleLockedSlot(false, playerSlotId);
                        }
                    }
                }

                instance().setLastLockedSlotIndex(slot.index);
            }
        }

        if (!clientOptionsInstance().getManagementOptions().dragSorting) {
            return;
        } else if (!lockingSlot) {
            if (notExcluding && !instance().getExcludedAll()) {
                for (Slot s : instance().getScreenMenu().slots) {
                    instance().getExcludedSlots().add(s.index);
                }
                instance().setExcludedAll(true);
            } else if (bl == 1) {
                if (notExcluding) {
                    instance().getExcludedSlots().add(slot.index);
                } else {
                    instance().getExcludedSlots().remove(slot.index);
                }
            } else if (bl == 0) {
                if (notExcluding) {
                    instance().getExcludedSlots().remove(slot.index);
                } else {
                    instance().getExcludedSlots().add(slot.index);
                }
            }
        }

        cir.setReturnValue(true);
    }
}

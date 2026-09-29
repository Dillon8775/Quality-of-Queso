package net.dillon.qualityofqueso.event.management;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Set;

import static net.dillon.dillonlib.task.ClientTasks.blitTexture;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.kumaMousePressed;
import static net.dillon.qualityofqueso.helper.ModConstants.LOCKED_SLOT_TEXTURE;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.*;
import static net.dillon.qualityofqueso.keybind.ModKeyMappings.LOCK_SLOT;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles locked slot colors, overlays, and functions.
 */
public class LockedSlotEvents extends ManagementEvents {

    public LockedSlotEvents(QuesoScreenHolder screen) {
        super(screen);
    }

    /**
     * @return the set of locked container slots.
     */
    public Set<Integer> getLockedContainerSlots() {
        return client().lockedSlots().lockedSlots ? ContainerHelper.getLockedSlots(true) : Collections.emptySet();
    }

    /**
     * @return the set of locked player slots.
     */
    public Set<Integer> getLockedPlayerSlots() {
        return client().lockedSlots().lockedSlots ? ContainerHelper.getLockedSlots(false) : Collections.emptySet();
    }

    /**
     * @return if the slot passed in is "locked".
     */
    public boolean isLockedSlot(int slotIndex) {
        boolean containerSlot = isContainerScreen(holder().screen()) && slotIndex < getContainerSize();
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
        return Minecraft.getInstance().hasControlDown() && hasDropOnlyOneItemModifierDown() && holder().screensHoveredSlot() != null && lockedSlotEvents().isLockedSlot(holder().screensHoveredSlot().index);
    }

    /**
     * @return if a locked slot drop full stack is valid.
     */
    public boolean shouldCancelDrop() {
        if (lockedSlotEvents().droppingEntireLockedSlotStack()
                ? lockedSlotEvents().droppingEntireLockedSlotStack() && canScrollMoveAndHasScrollModifierDown()
                : canScrollMoveAndHasScrollModifierDown() && !hasDropOnlyOneItemModifierDown()) {
            return !(canScrollMoveAndHasScrollModifierDown() && hasDropOnlyOneItemModifierDown() && Minecraft.getInstance().hasAltDown());
        }
        return false;
    }

    /**
     * Renders the unlocked slot texture over slots.
     */
    public void renderUnlockedSlot(GuiGraphicsExtractor graphics, boolean isSlotLocked, int mouseX, int mouseY) {
        int xy = 10;
        blitTexture(
                graphics,
                qoqIdentifier("textures/gui/sprites/slot/" + (isSlotLocked ? "slot_key" : "unlock_slot") + ".png"),
                mouseX - 8,
                mouseY + 1,
                xy,
                xy
        );
    }

    /**
     * Renders a slot as "locked".
     */
    public void renderLockedSlot(GuiGraphicsExtractor graphics, Slot slot, boolean lockOnly) {
        boolean containerSlot = isContainerScreen(holder().screen()) && slot.index < getContainerSize();
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
                    blitTexture(
                            graphics,
                            qoqIdentifier("textures/gui/sprites/" + LOCKED_SLOT_TEXTURE + ".png"),
                            slot.x - 3,
                            slot.y + 9,
                            xy, xy
                    );
                }
            } else {
                graphics.fill(slot.x - 1, slot.y - 1, slot.x + 17, slot.y + 17, client().lockedSlots().lockedSlotColor);
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
        if (isInventoryScreen(holder().screen())) {
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
    public void selectOrLockSlot(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        Slot slot = holder().screensHoveredSlot();

        boolean notExcluding = Minecraft.getInstance().hasAltDown() && !Minecraft.getInstance().hasShiftDown();
        boolean lockingSlot = kumaMousePressed(LOCK_SLOT, event);

        if (slot == null) {
            return;
        }

        if (isValidScreen(holder().screen()) && client().lockedSlots().lockedSlots && notExcluding && hasLockSlotModifierDown() && lockingSlot) {
            if (holder().getLastLockedSlotIndex() != slot.index) {
                if (holder().getLockDragAction() == 0) {
                    holder().setLockDragAction(isLockedSlot(slot.index) ? -1 : 1);
                }

                boolean shouldLock = holder().getLockDragAction() == 1;
                boolean slotLocked = isLockedSlot(slot.index);
                boolean containerSlot = isContainerScreen(holder().screen()) && slot.index < getContainerSize();
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

                holder().setLastLockedSlotIndex(slot.index);
            }
        }

        if (!client().management().dragSorting) {
            return;
        } else if (!lockingSlot) {
            if (notExcluding && !holder().getExcludedAll()) {
                for (Slot s : holder().menu().slots) {
                    holder().excludedSlots().add(s.index);
                }
                holder().setExcludedAll(true);
            } else if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
                if (notExcluding) {
                    holder().excludedSlots().add(slot.index);
                } else {
                    holder().excludedSlots().remove(slot.index);
                }
            } else if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
                if (notExcluding) {
                    holder().excludedSlots().remove(slot.index);
                } else {
                    holder().excludedSlots().add(slot.index);
                }
            }
        }

        cir.setReturnValue(true);
    }
}
package net.dillon.qualityofqueso.instance.management;

import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.mixin.client.accessor.MerchantScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.helper.MethodHelper.performClickSlot;
import static net.dillon.qualityofqueso.helper.ModHelper.options;

/**
 * Handles clicking slots.
 */
public class ClickSlotInstance extends ManagementInstance {
    private static ActiveTradeAllTask activeTradeAllTask;
    private static boolean processingQueuedTradeAllClick;

    private record ActiveTradeAllTask(int containerId, int selectedTradeIndex, int remainingClicks, int emptyResultTicks) {}

    public ClickSlotInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Quickly closes the GUI screen by clicking anywhere outside the screen.
     */
    public void quickGuiClose(Slot slot, int buttonNum) {
        if (options().misc.quickGuiExit && getCursorStack().isEmpty() && buttonNum == 0 && slot == null) {
            instance().getScreen().onClose();
        }
    }

    /**
     * Hard-locks slots and prevents any interaction on them.
     */
    public void handleHardLockedSlots(Slot slot, CallbackInfo ci) {
        // Skip further injection if hard lock slots are disabled, but continue if always quick move is enabled (they are by default)
        if (slot == null || !options().lockedSlots.enableLockedSlots || (!options().lockedSlots.hardLockSlots && !options().isAlwaysQuickMove())) {
            return;
        }

        // Prevents any interaction on locked slots
        if (lockedSlotsInstance().isLockedSlot(slot.index)) {
            ci.cancel();
        }
    }

    /**
     * Trades with all resources for the selected trade.
     */
    public void tradeAllForSelectedOffer(int slotId, ClickType containerInput, CallbackInfo ci) {
        if (processingQueuedTradeAllClick) {
            return;
        }

        if ((!options().management.tradeAll || !options().buttonDisplayOptions.displayTradeAll) || !options().buttonDisplayOptions.displayTradeAll || !(instance().getScreen() instanceof MerchantScreen merchantScreen)) {
            return;
        }

        // Result slot in MerchantMenu
        if (slotId != 2) {
            return;
        }

        // Only when player actually tries to take result
        if (containerInput != ClickType.PICKUP && containerInput != ClickType.QUICK_MOVE) {
            return;
        }

        MerchantMenu merchantMenu = merchantScreen.getMenu();
        int selected = ((MerchantScreenAccessor) merchantScreen).getSelectedTradeIndex();

        // If invalid selection, return out
        if (selected < 0 || selected >= merchantMenu.getOffers().size()) {
            return;
        }

        if (instance().getMinecraft().player == null || instance().getMinecraft().gameMode == null || !merchantMenu.getSlot(2).hasItem()) {
            return;
        }

        // Cancel the original click and process trade-all via paced client ticks.
        ci.cancel();
        activeTradeAllTask = new ActiveTradeAllTask(merchantMenu.containerId, selected, 256, 0);
    }

    /**
     * Ticks any active "trade all" task.
     */
    public static void tickTradeAllTask() {
        Minecraft client = Minecraft.getInstance();

        if (activeTradeAllTask == null || client.player == null || client.gameMode == null) {
            return;
        }

        if (!(client.screen instanceof MerchantScreen merchantScreen)) {
            activeTradeAllTask = null;
            return;
        }

        MerchantMenu merchantMenu = merchantScreen.getMenu();
        if (merchantMenu.containerId != activeTradeAllTask.containerId()) {
            activeTradeAllTask = null;
            return;
        }

        int selected = ((MerchantScreenAccessor) merchantScreen).getSelectedTradeIndex();
        if (selected != activeTradeAllTask.selectedTradeIndex() || selected < 0 || selected >= merchantMenu.getOffers().size()) {
            activeTradeAllTask = null;
            return;
        }

        if (!merchantMenu.getSlot(2).hasItem()) {
            // Ask the server to (re)apply selected offer and refill payment slots from inventory.
            merchantMenu.setSelectionHint(selected);
            if (client.getConnection() != null) {
                client.getConnection().send(new ServerboundSelectTradePacket(selected));
            }

            int emptyResultTicks = activeTradeAllTask.emptyResultTicks() + 1;
            activeTradeAllTask = emptyResultTicks > 10
                    ? null
                    : new ActiveTradeAllTask(activeTradeAllTask.containerId(), activeTradeAllTask.selectedTradeIndex(), activeTradeAllTask.remainingClicks(), emptyResultTicks);
            return;
        }

        processingQueuedTradeAllClick = true;
        try {
            performClickSlot(merchantScreen, merchantMenu.getSlot(2), 2, 0, ClickType.QUICK_MOVE);
        } finally {
            processingQueuedTradeAllClick = false;
        }

        int remainingClicks = activeTradeAllTask.remainingClicks() - 1;
        activeTradeAllTask = remainingClicks > 0
                ? new ActiveTradeAllTask(activeTradeAllTask.containerId(), activeTradeAllTask.selectedTradeIndex(), remainingClicks, 0)
                : null;
    }
}
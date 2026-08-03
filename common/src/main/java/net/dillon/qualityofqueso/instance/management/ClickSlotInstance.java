package net.dillon.qualityofqueso.instance.management;

import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.mixin.client.accessor.MerchantScreenAccessor;
import net.dillon.qualityofqueso.mixin.client.accessor.RecipeBookComponentAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.qualityofqueso.helper.ManagementHelper.isCraftingScreen;
import static net.dillon.qualityofqueso.helper.ManagementHelper.isInventoryScreen;
import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;
import static net.dillon.qualityofqueso.helper.MethodHelper.performClickSlot;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

/**
 * Handles clicking slots.
 */
public class ClickSlotInstance extends ManagementInstance {
    private static ActiveTradeAllTask activeTradeAllTask;
    private static boolean processingQueuedTradeAllClick;
    private static ActiveBulkCraftTask activeBulkCraftTask;
    private static boolean processingQueuedBulkCraftClick;

    private record ActiveTradeAllTask(int containerId, int selectedTradeIndex, int remainingClicks, int emptyResultTicks) {}
    private record ActiveBulkCraftTask(int containerId, @Nullable RecipeDisplayId recipeId, int remainingClicks, int emptyResultTicks) {}

    public ClickSlotInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Quickly closes the GUI screen by clicking anywhere outside the screen.
     */
    public void quickGuiClose(Slot slot, int buttonNum) {
        if (clientOptionsInstance().getMiscOptions().quickGuiExit && getCursorStack().isEmpty() && buttonNum == 0 && slot == null) {
            instance().getScreen().onClose();
        }
    }

    /**
     * Hard-locks slots and prevents any interaction on them.
     */
    public void handleHardLockedSlots(Slot slot, CallbackInfo ci) {
        // Skip further injection if hard lock slots are disabled, but continue if always quick move is enabled (they are by default)
        if (slot == null || !clientOptionsInstance().getLockedSlotOptions().lockedSlots || (!clientOptionsInstance().getLockedSlotOptions().hardLockSlots && !clientOptionsInstance().isAlwaysQuickMove())) {
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
    public void tradeAllForSelectedOffer(int slotId, ContainerInput containerInput, CallbackInfo ci) {
        if (processingQueuedTradeAllClick) {
            return;
        }

        if ((!clientOptionsInstance().getManagementOptions().bulkTrade || !clientOptionsInstance().getButtonDisplayOptions().displayBulkTrade) || !clientOptionsInstance().getButtonDisplayOptions().displayBulkTrade || !(instance().getScreen() instanceof MerchantScreen merchantScreen)) {
            return;
        }

        // Result slot in MerchantMenu
        if (slotId != 2) {
            return;
        }

        // Only when player actually tries to take result
        if (containerInput != ContainerInput.PICKUP && containerInput != ContainerInput.QUICK_MOVE) {
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
     * Crafts all resources for the selected recipe.
     */
    public void bulkCraftForSelectedRecipe(int slotId, ContainerInput containerInput, CallbackInfo ci) {
        if (processingQueuedBulkCraftClick) {
            return;
        }

        if ((!clientOptionsInstance().getManagementOptions().bulkCraft || !clientOptionsInstance().getButtonDisplayOptions().displayBulkCraft) || !(isCraftingScreen(instance().getScreen()) || isInventoryScreen(instance().getScreen()))) {
            return;
        }

        // Result slot in CraftingMenu
        if (slotId != 0) {
            return;
        }

        // Only when player actually tries to take result
        if (containerInput != ContainerInput.PICKUP && containerInput != ContainerInput.QUICK_MOVE) {
            return;
        }

        AbstractContainerMenu menu = instance().getScreenMenu();
        if (instance().getMinecraft().player == null || instance().getMinecraft().gameMode == null || !menu.getSlot(0).hasItem()) {
            return;
        }

        if (!(instance().getScreen() instanceof AbstractRecipeBookScreen<?> recipeBookScreen)) {
            return;
        }

        RecipeDisplayId recipeId = ((RecipeBookComponentAccessor) getRecipeBookComponent(recipeBookScreen)).getLastRecipe();
        if (recipeId == null) {
            // No selected recipe: preserve vanilla behavior (normal click, shift-click quick move, etc.).
            return;
        }

        // Cancel the original click and process craft-all via paced client ticks.
        ci.cancel();
        activeBulkCraftTask = new ActiveBulkCraftTask(menu.containerId, recipeId, 256, 0);
    }

    /**
     * Ticks any active "trade all" task.
     */
    public static void tickTradeAllTask() {
        Minecraft client = Minecraft.getInstance();

        if (activeTradeAllTask == null || client.player == null || client.gameMode == null) {
            return;
        }

        if (!(getScreen() instanceof MerchantScreen merchantScreen)) {
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
            performClickSlot(merchantScreen, merchantMenu.getSlot(2), 2, 0, ContainerInput.QUICK_MOVE);
        } finally {
            processingQueuedTradeAllClick = false;
        }

        int remainingClicks = activeTradeAllTask.remainingClicks() - 1;
        activeTradeAllTask = remainingClicks > 0
                ? new ActiveTradeAllTask(activeTradeAllTask.containerId(), activeTradeAllTask.selectedTradeIndex(), remainingClicks, 0)
                : null;
    }

    /**
     * Ticks any active "bulk craft" task.
     */
    public static void tickBulkCraftTask() {
        Minecraft client = Minecraft.getInstance();

        if (activeBulkCraftTask == null || client.player == null || client.gameMode == null) {
            return;
        }

        if (!isCraftingScreen(getScreen()) && !isInventoryScreen(getScreen())) {
            activeBulkCraftTask = null;
            return;
        }

        if (!(getScreen() instanceof AbstractContainerScreen<?> screen)) {
            activeBulkCraftTask = null;
            return;
        }

        AbstractContainerMenu menu = screen.getMenu();
        if (menu.containerId != activeBulkCraftTask.containerId()) {
            activeBulkCraftTask = null;
            return;
        }

        if (!menu.getSlot(0).hasItem()) {
            if (activeBulkCraftTask.recipeId() != null) {
                client.gameMode.handlePlaceRecipe(menu.containerId, activeBulkCraftTask.recipeId(), true);
            }

            int emptyResultTicks = activeBulkCraftTask.emptyResultTicks() + 1;
            activeBulkCraftTask = emptyResultTicks > 10
                    ? null
                    : new ActiveBulkCraftTask(activeBulkCraftTask.containerId(), activeBulkCraftTask.recipeId(), activeBulkCraftTask.remainingClicks(), emptyResultTicks);
            return;
        }

        processingQueuedBulkCraftClick = true;
        try {
            performClickSlot(screen, menu.getSlot(0), 0, 0, ContainerInput.QUICK_MOVE);
        } finally {
            processingQueuedBulkCraftClick = false;
        }

        int remainingClicks = activeBulkCraftTask.remainingClicks() - 1;
        activeBulkCraftTask = remainingClicks > 0
                ? new ActiveBulkCraftTask(activeBulkCraftTask.containerId(), activeBulkCraftTask.recipeId(), remainingClicks, 0)
                : null;
    }
}
package net.dillon.qualityofqueso.event.management;

import net.dillon.dillonlib.mixin.accessor.ScreenInvoker;
import net.dillon.qualityofqueso.event.QuesoScreen;
import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.widget.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.helper.ManagementHelper.isContainerScreen;
import static net.dillon.qualityofqueso.helper.ManagementHelper.isInventoryScreen;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModHelper.sendClientPreferencesToServer;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;

/**
 * Initializes buttons and widgets.
 */
public class WidgetHandlerEvents extends ManagementEvents {
    public final List<GuiEventListener> dynamicButtons = new ArrayList<>();

    public WidgetHandlerEvents(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Adds a widget to the screen, and adds it to the dynamic buttons for reference.
     */
    public <T extends GuiEventListener & NarratableEntry> T addWidget(T widget) {
        ((ScreenInvoker) holder().screen()).addModWidget(widget);
        this.dynamicButtons.add(widget);
        return widget;
    }

    /**
     * @return the {@code transfer container} button.
     */
    public QuesoButton createTransferContainer() {
        return new QuesoButton(
                holder().menu(),
                holder().mc().font,
                searchInstance().getSearchFieldText(),
                TRANSFER_CONTAINER_BUTTON_PATH,
                TRANSFER_CONTAINER_BUTTON_NAME,
                true,
                b -> transferInstance().transferItems(true, true),
                () -> !isContainerFull(true) && shouldButtonBeActive(false, null, true, true));
    }

    /**
     * @return the {@code transfer inventory} button.
     */
    public QuesoButton createTransferInventory() {
        return new QuesoButton(
                holder().menu(),
                holder().mc().font,
                searchInstance().getSearchFieldText(),
                TRANSFER_INVENTORY_BUTTON_PATH,
                TRANSFER_INVENTORY_BUTTON_NAME,
                true,
                b -> transferInstance().transferItems(false, true),
                () -> !isContainerFull(false) && shouldButtonBeActive(true, holder().mc().player.getInventory(), true, true)
        );
    }

    /**
     * @return the {@code include hotbar} button.
     */
    public QuesoButton createIncludeHotbar() {
        return new IncludeHotbarButton(
                holder().menu(),
                holder().mc().font,
                searchInstance().getSearchFieldText(),
                "include_hotbar",
                b -> {
                    updateClient(client -> {
                        client.management().includingHotbar = !client.management().includingHotbar;
                    });
                    sendClientPreferencesToServer();
                });
    }

    /**
     * @return the {@code always quick move} button.
     */
    public QuesoButton createAlwaysQuickMove() {
        return new AlwaysQuickMoveButton(
                holder().menu(),
                holder().mc().font,
                searchInstance().getSearchFieldText(),
                "always_quick_move",
                b -> updateClient(ModClientOptions::toggleAlwaysQuickMove)
        );
    }

    /**
     * @return the move matching items button.
     */
    public QuesoButton createFiltering() {
        return new FilteringButton(
                holder().menu(),
                holder().mc().font,
                searchInstance().getSearchFieldText(),
                "fill_whats_present",
                b -> {
                    if (!ContainerHelper.isTrackedFilteringActive()) {
                        updateClient(ModClientOptions::cycleFilteringMode);
                    }
                },
                holder().mc(),
                holder().screen());
    }

    /**
     * @return the {@code sort} button.
     */
    public QuesoButton createSort() {
        return new SortButton(
                holder().menu(),
                holder().mc().font,
                searchInstance().getSearchFieldText(),
                "sort/",
                "sort",
                b -> sortingInstance().trySort(),
                sortingInstance()::canSort
        );
    }

    /**
     * @return the {@code search transportables} button.
     */
    public QuesoButton createSearchTransportables() {
        return new SearchTransportablesButton(
                holder().menu(),
                holder().mc().font,
                searchInstance().getSearchFieldText(),
                "search_transportables",
                b -> SEARCHING_TRANSPORTABLES = !SEARCHING_TRANSPORTABLES
        );
    }

    /**
     * @return the {@code quick drop} button.
     */
    public QuesoButton createQuickDrop() {
        boolean containerScreen = isContainerScreen(holder().screen());
        return new QuickDropButton(
                holder().menu(),
                holder().mc().font,
                searchInstance().getSearchFieldText(),
                "quick_drop/",
                "quick_drop",
                b -> transferInstance().dropItems(!containerScreen),
                () -> (isInventoryScreen(holder().screen()) ?
                        isAnySlotFilled(true, 9, 36) :
                        isAnySlotFilled(false, 0, getContainerSize()))
                        && getCursorStack().isEmpty()
                        && shouldButtonBeActive(!containerScreen, containerScreen ? null : holder().mc().player.getInventory())
        );
    }

    /**
     * @return the {@code swap} button.
     */
    public QuesoButton createSwap() {
        return new SwapButton(
                holder().menu(),
                holder().mc().font,
                searchInstance().getSearchFieldText(),
                "swap/",
                "swap",
                b -> transferInstance().trySwap(),
                transferInstance()::canSwap
        );
    }

    /**
     * @return the {@code clear excluded slots} button.
     */
    public QuesoButton createClearExcludedSlots() {
        return new ClearExcludedSlotsButton(
                holder().menu(),
                holder().mc().font,
                searchInstance().getSearchFieldText(),
                "clear_excluded_slots",
                b -> {
                    holder().excludedSlots().clear();
                    holder().setExcludedAll(false);
                }
        );
    }

    /**
     * @return the {@code trade all} button.
     */
    public QuesoButton createTradeAll() {
        return new BulkTradeButton(
                holder().menu(),
                holder().mc().font,
                searchInstance().getSearchFieldText(),
                "trade_all",
                b -> updateClient(client -> {
                    client.management().bulkTrade = !client.management().bulkTrade;
                })
        );
    }

    /**
     * @return the {@code bulk craft} button.
     */
    public QuesoButton createBulkCraft() {
        return new BulkCraftButton(
                holder().menu(),
                holder().mc().font,
                searchInstance().getSearchFieldText(),
                "craft_all",
                b -> updateClient(client -> {
                    client.management().bulkCraft = !client.management().bulkCraft;
                })
        );
    }

    /**
     * @return the {@code lock inventory} button.
     */
    public QuesoButton createLockInventory() {
        return new LockInventoryButton(
                holder().menu(),
                holder().mc().font,
                searchInstance().getSearchFieldText(),
                "lock_inventory",
                b -> {
                    updateClient(ModClientOptions::cycleLockedInventoryMode);
                    sendClientPreferencesToServer();
                }
        );
    }
}
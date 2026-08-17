package net.dillon.qualityofqueso.instance.management;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.mixin.client.accessor.ScreenAccessor;
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

/**
 * Initializes buttons and widgets.
 */
public class WidgetHandlerInstance extends ManagementInstance {
    public final List<GuiEventListener> dynamicButtons = new ArrayList<>();

    public WidgetHandlerInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Adds a widget to the screen, and adds it to the dynamic buttons for reference.
     */
    public <T extends GuiEventListener & NarratableEntry> T addWidget(T widget) {
        ((ScreenAccessor)instance().getScreen()).addModWidget(widget);
        this.dynamicButtons.add(widget);
        return widget;
    }

    /**
     * @return the {@code transfer container} button.
     */
    public QuesoButton createTransferContainer() {
        return new QuesoButton(
                instance().getScreenMenu(),
                instance().getMinecraft().font,
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
                instance().getScreenMenu(),
                instance().getMinecraft().font,
                searchInstance().getSearchFieldText(),
                TRANSFER_INVENTORY_BUTTON_PATH,
                TRANSFER_INVENTORY_BUTTON_NAME,
                true,
                b -> transferInstance().transferItems(false, true),
                () -> !isContainerFull(false) && shouldButtonBeActive(true, instance().getMinecraft().player.getInventory(), true, true)
        );
    }

    /**
     * @return the {@code include hotbar} button.
     */
    public QuesoButton createIncludeHotbar() {
        return new IncludeHotbarButton(
                instance().getScreenMenu(),
                instance().getMinecraft().font,
                searchInstance().getSearchFieldText(),
                "include_hotbar",
                b -> {
                    ModClientOptions.INSTANCE.update(options -> {
                        options.management().includingHotbar = !options.management().includingHotbar;
                    });
                    sendClientPreferencesToServer();
                });
    }

    /**
     * @return the {@code always quick move} button.
     */
    public QuesoButton createAlwaysQuickMove() {
        return new AlwaysQuickMoveButton(
                instance().getScreenMenu(),
                instance().getMinecraft().font,
                searchInstance().getSearchFieldText(),
                "always_quick_move",
                b -> ModClientOptions.INSTANCE.update(ModClientOptions::toggleAlwaysQuickMove)
        );
    }

    /**
     * @return the move matching items button.
     */
    public QuesoButton createFiltering() {
        return new FilteringButton(
                instance().getScreenMenu(),
                instance().getMinecraft().font,
                searchInstance().getSearchFieldText(),
                "fill_whats_present",
                b -> {
                    if (!ContainerHelper.isTrackedFilteringActive()) {
                        ModClientOptions.INSTANCE.update(ModClientOptions::cycleFilteringMode);
                    }
                },
                instance().getMinecraft(),
                instance().getScreen());
    }

    /**
     * @return the {@code sort} button.
     */
    public QuesoButton createSort() {
        return new SortButton(
                instance().getScreenMenu(),
                instance().getMinecraft().font,
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
                instance().getScreenMenu(),
                instance().getMinecraft().font,
                searchInstance().getSearchFieldText(),
                "search_transportables",
                b -> ModClientOptions.INSTANCE.update(options -> {
                    SEARCHING_TRANSPORTABLES = !SEARCHING_TRANSPORTABLES;
                })
        );
    }

    /**
     * @return the {@code quick drop} button.
     */
    public QuesoButton createQuickDrop() {
        boolean containerScreen = isContainerScreen(instance().getScreen());
        return new QuickDropButton(
                instance().getScreenMenu(),
                instance().getMinecraft().font,
                searchInstance().getSearchFieldText(),
                "quick_drop/",
                "quick_drop",
                b -> transferInstance().dropItems(!containerScreen),
                () -> (isInventoryScreen(instance().getScreen()) ?
                        isAnySlotFilled(true, 9, 36) :
                        isAnySlotFilled(false, 0, getContainerSize()))
                        && getCursorStack().isEmpty()
                        && shouldButtonBeActive(!containerScreen, containerScreen ? null : instance().getMinecraft().player.getInventory())
        );
    }

    /**
     * @return the {@code swap} button.
     */
    public QuesoButton createSwap() {
        return new SwapButton(
                instance().getScreenMenu(),
                instance().getMinecraft().font,
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
                instance().getScreenMenu(),
                instance().getMinecraft().font,
                searchInstance().getSearchFieldText(),
                "clear_excluded_slots",
                b -> {
                    instance().getExcludedSlots().clear();
                    instance().setExcludedAll(false);
                }
        );
    }

    /**
     * @return the {@code trade all} button.
     */
    public QuesoButton createTradeAll() {
        return new BulkTradeButton(
                instance().getScreenMenu(),
                instance().getMinecraft().font,
                searchInstance().getSearchFieldText(),
                "trade_all",
                b -> ModClientOptions.INSTANCE.update(options -> {
                    options.management().bulkTrade = !options.management().bulkTrade;
                })
        );
    }

    /**
     * @return the {@code bulk craft} button.
     */
    public QuesoButton createBulkCraft() {
        return new BulkCraftButton(
                instance().getScreenMenu(),
                instance().getMinecraft().font,
                searchInstance().getSearchFieldText(),
                "craft_all",
                b -> ModClientOptions.INSTANCE.update(options -> {
                    options.management().bulkCraft = !options.management().bulkCraft;
                })
        );
    }

    /**
     * @return the {@code lock inventory} button.
     */
    public QuesoButton createLockInventory() {
        return new LockInventoryButton(
                instance().getScreenMenu(),
                instance().getMinecraft().font,
                searchInstance().getSearchFieldText(),
                "lock_inventory",
                b -> {
                    ModClientOptions.INSTANCE.update(options -> {
                        ModClientOptions.INSTANCE.update(ModClientOptions::cycleLockedInventoryMode);
                    });
                    sendClientPreferencesToServer();
                }
        );
    }
}
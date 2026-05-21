package net.dillon.qualityofqueso.instance.context;

import net.dillon.qualityofqueso.widget.TransferButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all management buttons.
 */
public class ManagementButtons {
    private TransferButton transferContainer;
    private TransferButton transferInventory;
    private TransferButton includeHotbar;
    private TransferButton filtering;
    private TransferButton quickDrop;
    private TransferButton swap;
    private TransferButton sort;
    private TransferButton searchTransportables;
    private TransferButton alwaysQuickMove;
    private TransferButton clearExcludedSlots;
    private TransferButton tradeAll;
    private TransferButton bulkCraft;
    private TransferButton lockInventory;

    public TransferButton transferContainer() {
        return this.transferContainer;
    }

    public void initTransferContainer(TransferButton button) {
        this.transferContainer = button;
    }

    public TransferButton transferInventory() {
        return this.transferInventory;
    }

    public void initTransferInventory(TransferButton button) {
        this.transferInventory = button;
    }

    public TransferButton includeHotbar() {
        return this.includeHotbar;
    }

    public void initIncludeHotbar(TransferButton button) {
        this.includeHotbar = button;
    }

    public TransferButton filtering() {
        return this.filtering;
    }

    public void initFiltering(TransferButton button) {
        this.filtering = button;
    }

    public TransferButton quickDrop() {
        return this.quickDrop;
    }

    public void initQuickDrop(TransferButton button) {
        this.quickDrop = button;
    }

    public TransferButton swap() {
        return this.swap;
    }

    public void initSwap(TransferButton button) {
        this.swap = button;
    }

    public TransferButton sort() {
        return this.sort;
    }

    public void initSort(TransferButton button) {
        this.sort = button;
    }

    public TransferButton searchTransportables() {
        return this.searchTransportables;
    }

    public void initSearchTransportables(TransferButton button) {
        this.searchTransportables = button;
    }

    public TransferButton alwaysQuickMove() {
        return this.alwaysQuickMove;
    }

    public void initAlwaysQuickMove(TransferButton button) {
        this.alwaysQuickMove = button;
    }

    public TransferButton clearExcludedSlots() {
        return this.clearExcludedSlots;
    }

    public void initClearExcludedSlots(TransferButton button) {
        this.clearExcludedSlots = button;
    }

    public TransferButton tradeAll() {
        return this.tradeAll;
    }

    public void initTradeAll(TransferButton button) {
        this.tradeAll = button;
    }

    public TransferButton bulkCraft() {
        return this.bulkCraft;
    }

    public void initBulkCraft(TransferButton button) {
        this.bulkCraft = button;
    }

    public TransferButton lockInventory() {
        return this.lockInventory;
    }

    public void initLockInventory(TransferButton button) {
        this.lockInventory = button;
    }

    /**
     * @return this.all non-null management buttons.
     */
    public List<TransferButton> all() {
        List<TransferButton> buttons = new ArrayList<>();

        if (this.transferContainer != null) {
            buttons.add(this.transferContainer);
        }
        if (this.transferInventory != null) {
            buttons.add(this.transferInventory);
        }
        if (this.includeHotbar != null) {
            buttons.add(this.includeHotbar);
        }
        if (this.filtering != null) {
            buttons.add(this.filtering);
        }
        if (this.quickDrop != null) {
            buttons.add(this.quickDrop);
        }
        if (this.swap != null) {
            buttons.add(this.swap);
        }
        if (this.sort != null) {
            buttons.add(this.sort);
        }
        if (this.searchTransportables != null) {
            buttons.add(this.searchTransportables);
        }
        if (this.alwaysQuickMove != null) {
            buttons.add(this.alwaysQuickMove);
        }
        if (this.clearExcludedSlots != null) {
            buttons.add(this.clearExcludedSlots);
        }
        if (this.tradeAll != null) {
            buttons.add(this.tradeAll);
        }
        if (this.bulkCraft != null) {
            buttons.add(this.bulkCraft);
        }
        if (this.lockInventory != null) {
            buttons.add(this.lockInventory);
        }

        return buttons;
    }

    /**
     * Clears all button references.
     */
    public void clear() {
        this.transferContainer = null;
        this.transferInventory = null;
        this.includeHotbar = null;
        this.filtering = null;
        this.quickDrop = null;
        this.swap = null;
        this.sort = null;
        this.searchTransportables = null;
        this.alwaysQuickMove = null;
        this.clearExcludedSlots = null;
        this.tradeAll = null;
        this.bulkCraft = null;
        this.lockInventory = null;
    }
}
package net.dillon.qualityofqueso.instance.context;

import net.dillon.qualityofqueso.widget.QuesoButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all management buttons.
 */
public class ManagementButtons {
    private QuesoButton transferContainer;
    private QuesoButton transferInventory;
    private QuesoButton includeHotbar;
    private QuesoButton filtering;
    private QuesoButton quickDrop;
    private QuesoButton swap;
    private QuesoButton sort;
    private QuesoButton searchTransportables;
    private QuesoButton alwaysQuickMove;
    private QuesoButton clearExcludedSlots;
    private QuesoButton tradeAll;
    private QuesoButton bulkCraft;
    private QuesoButton lockInventory;

    public QuesoButton transferContainer() {
        return this.transferContainer;
    }

    public void initTransferContainer(QuesoButton button) {
        this.transferContainer = button;
    }

    public QuesoButton transferInventory() {
        return this.transferInventory;
    }

    public void initTransferInventory(QuesoButton button) {
        this.transferInventory = button;
    }

    public QuesoButton includeHotbar() {
        return this.includeHotbar;
    }

    public void initIncludeHotbar(QuesoButton button) {
        this.includeHotbar = button;
    }

    public QuesoButton filtering() {
        return this.filtering;
    }

    public void initFiltering(QuesoButton button) {
        this.filtering = button;
    }

    public QuesoButton quickDrop() {
        return this.quickDrop;
    }

    public void initQuickDrop(QuesoButton button) {
        this.quickDrop = button;
    }

    public QuesoButton swap() {
        return this.swap;
    }

    public void initSwap(QuesoButton button) {
        this.swap = button;
    }

    public QuesoButton sort() {
        return this.sort;
    }

    public void initSort(QuesoButton button) {
        this.sort = button;
    }

    public QuesoButton searchTransportables() {
        return this.searchTransportables;
    }

    public void initSearchTransportables(QuesoButton button) {
        this.searchTransportables = button;
    }

    public QuesoButton alwaysQuickMove() {
        return this.alwaysQuickMove;
    }

    public void initAlwaysQuickMove(QuesoButton button) {
        this.alwaysQuickMove = button;
    }

    public QuesoButton clearExcludedSlots() {
        return this.clearExcludedSlots;
    }

    public void initClearExcludedSlots(QuesoButton button) {
        this.clearExcludedSlots = button;
    }

    public QuesoButton tradeAll() {
        return this.tradeAll;
    }

    public void initTradeAll(QuesoButton button) {
        this.tradeAll = button;
    }

    public QuesoButton bulkCraft() {
        return this.bulkCraft;
    }

    public void initBulkCraft(QuesoButton button) {
        this.bulkCraft = button;
    }

    public QuesoButton lockInventory() {
        return this.lockInventory;
    }

    public void initLockInventory(QuesoButton button) {
        this.lockInventory = button;
    }

    /**
     * @return this.all non-null management buttons.
     */
    public List<QuesoButton> all() {
        List<QuesoButton> buttons = new ArrayList<>();

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
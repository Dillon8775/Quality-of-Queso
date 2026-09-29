package net.dillon.qualityofqueso.event.context;

import net.dillon.qualityofqueso.widget.QuesoButton;

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
    private QuesoButton bulkTrade;
    private QuesoButton bulkCraft;
    private QuesoButton lockInventory;

    public QuesoButton getTransferContainer() {
        return transferContainer;
    }

    public QuesoButton getTransferInventory() {
        return transferInventory;
    }

    public QuesoButton getIncludeHotbar() {
        return includeHotbar;
    }

    public QuesoButton getFiltering() {
        return filtering;
    }

    public QuesoButton getQuickDrop() {
        return quickDrop;
    }

    public QuesoButton getSwap() {
        return swap;
    }

    public QuesoButton getSort() {
        return sort;
    }

    public QuesoButton getSearchTransportables() {
        return searchTransportables;
    }

    public QuesoButton getAlwaysQuickMove() {
        return alwaysQuickMove;
    }

    public QuesoButton getClearExcludedSlots() {
        return clearExcludedSlots;
    }

    public QuesoButton getBulkTrade() {
        return bulkTrade;
    }

    public QuesoButton getBulkCraft() {
        return bulkCraft;
    }

    public QuesoButton getLockInventory() {
        return lockInventory;
    }

    public void setTransferContainer(QuesoButton button) {
        transferContainer = button;
    }

    public void setTransferInventory(QuesoButton button) {
        transferInventory = button;
    }

    public void setIncludeHotbar(QuesoButton button) {
        includeHotbar = button;
    }

    public void setFiltering(QuesoButton button) {
        filtering = button;
    }

    public void setQuickDrop(QuesoButton button) {
        quickDrop = button;
    }

    public void setSwap(QuesoButton button) {
        swap = button;
    }

    public void setSort(QuesoButton button) {
        sort = button;
    }

    public void setSearchTransportables(QuesoButton button) {
        searchTransportables = button;
    }

    public void setAlwaysQuickMove(QuesoButton button) {
        alwaysQuickMove = button;
    }

    public void setClearExcludedSlots(QuesoButton button) {
        clearExcludedSlots = button;
    }

    public void setBulkTrade(QuesoButton button) {
        bulkTrade = button;
    }

    public void setBulkCraft(QuesoButton button) {
        bulkCraft = button;
    }

    public void setLockInventory(QuesoButton button) {
        lockInventory = button;
    }
}
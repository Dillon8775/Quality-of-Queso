package net.dillon.qualityofqueso.event.context;

import net.dillon.qualityofqueso.widget.management.ManagementButton;

/**
 * Holds all management buttons.
 */
public class ManagementButtons {
    private ManagementButton transferContainer;
    private ManagementButton transferInventory;
    private ManagementButton includeHotbar;
    private ManagementButton filtering;
    private ManagementButton quickDrop;
    private ManagementButton swap;
    private ManagementButton sort;
    private ManagementButton searchTransportables;
    private ManagementButton alwaysQuickMove;
    private ManagementButton clearExcludedSlots;
    private ManagementButton bulkTrade;
    private ManagementButton bulkCraft;
    private ManagementButton lockInventory;

    public ManagementButton getTransferContainer() {
        return transferContainer;
    }

    public ManagementButton getTransferInventory() {
        return transferInventory;
    }

    public ManagementButton getIncludeHotbar() {
        return includeHotbar;
    }

    public ManagementButton getFiltering() {
        return filtering;
    }

    public ManagementButton getQuickDrop() {
        return quickDrop;
    }

    public ManagementButton getSwap() {
        return swap;
    }

    public ManagementButton getSort() {
        return sort;
    }

    public ManagementButton getSearchTransportables() {
        return searchTransportables;
    }

    public ManagementButton getAlwaysQuickMove() {
        return alwaysQuickMove;
    }

    public ManagementButton getClearExcludedSlots() {
        return clearExcludedSlots;
    }

    public ManagementButton getBulkTrade() {
        return bulkTrade;
    }

    public ManagementButton getBulkCraft() {
        return bulkCraft;
    }

    public ManagementButton getLockInventory() {
        return lockInventory;
    }

    public void setTransferContainer(ManagementButton button) {
        transferContainer = button;
    }

    public void setTransferInventory(ManagementButton button) {
        transferInventory = button;
    }

    public void setIncludeHotbar(ManagementButton button) {
        includeHotbar = button;
    }

    public void setFiltering(ManagementButton button) {
        filtering = button;
    }

    public void setQuickDrop(ManagementButton button) {
        quickDrop = button;
    }

    public void setSwap(ManagementButton button) {
        swap = button;
    }

    public void setSort(ManagementButton button) {
        sort = button;
    }

    public void setSearchTransportables(ManagementButton button) {
        searchTransportables = button;
    }

    public void setAlwaysQuickMove(ManagementButton button) {
        alwaysQuickMove = button;
    }

    public void setClearExcludedSlots(ManagementButton button) {
        clearExcludedSlots = button;
    }

    public void setBulkTrade(ManagementButton button) {
        bulkTrade = button;
    }

    public void setBulkCraft(ManagementButton button) {
        bulkCraft = button;
    }

    public void setLockInventory(ManagementButton button) {
        lockInventory = button;
    }
}
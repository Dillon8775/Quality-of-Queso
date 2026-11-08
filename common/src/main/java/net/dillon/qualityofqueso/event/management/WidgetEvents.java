package net.dillon.qualityofqueso.event.management;

import net.dillon.dillonlib.mixin.accessor.ScreenInvoker;
import net.dillon.qualityofqueso.container.ContainerSyncer;
import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.option.eum.management.IncludeHotbar;
import net.dillon.qualityofqueso.widget.management.*;
import net.dillon.qualityofqueso.widget.management.toggleable.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.getTitleLabelY;
import static net.dillon.qualityofqueso.helper.MethodHelper.getTopPos;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.hasAllQuickDropModifiersDown;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles all widget events.
 */
public class WidgetEvents extends ManagementEvents {

    public WidgetEvents(QuesoScreenHolder screen) {
        super(screen);
    }

    /**
     * @param booleanSupplier if the button can be initialized or added
     * @param newButton       the desired button to create
     * @return a safely initialized {@link ManagementButton}.
     */
    private ManagementButton initButton(BooleanSupplier booleanSupplier, ManagementButton newButton) {
        if (!booleanSupplier.getAsBoolean()) {
            return null;
        }

        addWidget(newButton);
        return newButton;
    }

    public void initTransferContainer() {
        holder().managementButtons().setTransferContainer(
                initButton(this::canInitTransferButton, createTransferContainer())
        );
    }

    public void initTransferInventory() {
        holder().managementButtons().setTransferInventory(
                initButton(this::canInitTransferInventoryButton, createTransferInventory())
        );
    }

    public void initIncludeHotbar() {
        holder().managementButtons().setIncludeHotbar(
                initButton(this::canInitIncludeHotbarButton, createIncludeHotbar())
        );
    }

    public void initFiltering() {
        holder().managementButtons().setFiltering(
                initButton(this::canInitFilteringButton, createFiltering())
        );
    }

    public void initQuickDrop() {
        holder().managementButtons().setQuickDrop(
                initButton(this::canInitQuickDropButton, createQuickDrop())
        );
    }

    public void initSwap() {
        holder().managementButtons().setSwap(
                initButton(this::canInitSwapButton, createSwap())
        );
    }

    public void initSort() {
        holder().managementButtons().setSort(
                initButton(this::canInitSortButton, createSort())
        );
    }

    public void initSearchTransportables() {
        holder().managementButtons().setSearchTransportables(
                initButton(this::canInitSearchTransportablesButton, createSearchTransportables())
        );
    }

    public void initAlwaysQuickMove() {
        holder().managementButtons().setAlwaysQuickMove(
                initButton(this::canInitAlwaysQuickMoveButton, createAlwaysQuickMove())
        );
    }

    public void initClearExcludedSlots() {
        holder().managementButtons().setClearExcludedSlots(
                initButton(this::canInitClearExcludedSlotsButton, createClearExcludedSlots())
        );
    }

    public void initBulkTrade() {
        holder().managementButtons().setBulkTrade(
                initButton(this::canInitBulkTradeButton, createBulkTrade())
        );
    }

    public void initBulkCraft() {
        holder().managementButtons().setBulkCraft(
                initButton(this::canInitBulkCraftButton, createBulkCraft())
        );
    }

    public void initLockInventory() {
        holder().managementButtons().setLockInventory(
                initButton(this::canInitLockInventoryButton, createLockInventory())
        );
    }

    public void initLayout(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (!(isValidScreen() || isOtherValidScreen() || isMerchantScreen() || isCraftingScreen())) {
            return;
        }

        // Create the default, horizontal layout
        AbstractList<AbstractWidget> horizontalLayout = buttonLayoutFromButtonName(client().management().horizontalButtonLayout);

        // Create the vertical layout (up-down, box beside GUI)
        AbstractList<AbstractWidget> verticalLayout = buttonLayoutFromButtonName(client().management().verticalButtonLayout);

        // Construct the final layout
        AbstractList<AbstractWidget> finalLayout = client().management().layout.horizontal() ? horizontalLayout : verticalLayout;
        if (isMerchantScreen()) {
            finalLayout = NonNullList.of(null, holder().managementButtons().getBulkTrade());
        }

        // Set and initialize the widget layout
        holder().setWidgetLayout(WidgetLayout.initializeLayout(holder().screen(),
                holder().getCachedContainer(), getTopPos(holder().screen()), getTitleLabelY(holder().screen()), finalLayout
        ));
        holder().getWidgetLayout().extractRenderState(graphics, mouseX, mouseY, a);
    }

    /**
     * @return the actual button list to use in-game from the user-picked button layout.
     */
    private AbstractList<AbstractWidget> buttonLayoutFromButtonName(List<String> layoutList) {
        AbstractList<AbstractWidget> finalLayout = new ArrayList<>();

        for (String s : layoutList) {
            switch (s) {
                case TRANSFER_CONTAINER_BUTTON_SERIALIZED_NAME ->
                        finalLayout.add(holder().managementButtons().getTransferContainer());
                case TRANSFER_INVENTORY_BUTTON_SERIALIZED_NAME ->
                        finalLayout.add(holder().managementButtons().getTransferInventory());
                case LOCK_INVENTORY_BUTTON_SERIALIZED_NAME ->
                        finalLayout.add(holder().managementButtons().getLockInventory());
                case INCLUDE_HOTBAR_BUTTON_SERIALIZED_NAME ->
                        finalLayout.add(holder().managementButtons().getIncludeHotbar());
                case ALWAYS_QUICK_MOVE_BUTTON_SERIALIZED_NAME ->
                        finalLayout.add(holder().managementButtons().getAlwaysQuickMove());
                case FILTERING_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getFiltering());
                case BULK_CRAFT_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getBulkCraft());
                case QUICK_DROP_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getQuickDrop());
                case SWAP_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getSwap());
                case SEARCH_TRANSPORTABLES_BUTTON_SERIALIZED_NAME ->
                        finalLayout.add(holder().managementButtons().getSearchTransportables());
                case CLEAR_EXCLUDED_SLOTS_BUTTON_SERIALIZED_NAME ->
                        finalLayout.add(holder().managementButtons().getClearExcludedSlots());
                default -> finalLayout.add(holder().managementButtons().getSort());
            }
        }

        return finalLayout;
    }

    /**
     * @return new {@link ManagementParams}.
     */
    private ManagementParams defaultManagementParams() {
        return new ManagementParams(holder().menu(), this);
    }

    /**
     * Creates a new button using {@link ManagementParams}.
     */
    private ManagementButton createButton(Function<ManagementParams, ManagementButton> factory) {
        return factory.apply(defaultManagementParams());
    }

    /**
     * @return the {@code transfer container} button.
     */
    private ManagementButton createTransferContainer() {
        return createButton(TransferContainerButton::new);
    }

    /**
     * @return the {@code transfer inventory} button.
     */
    private ManagementButton createTransferInventory() {
        return createButton(TransferInventoryButton::new);
    }

    /**
     * @return the {@code include hotbar} button.
     */
    private ManagementButton createIncludeHotbar() {
        return createButton(IncludeHotbarButton::new);
    }

    /**
     * @return the move matching items button.
     */
    private ManagementButton createFiltering() {
        return createButton(FilteringButton::new);
    }

    /**
     * @return the {@code quick drop} button.
     */
    private ManagementButton createQuickDrop() {
        return createButton(QuickDropButton::new);
    }

    /**
     * @return the {@code swap} button.
     */
    private ManagementButton createSwap() {
        return createButton(SwapButton::new);
    }

    /**
     * @return the {@code sort} button.
     */
    private ManagementButton createSort() {
        return createButton(SortButton::new);
    }

    /**
     * @return the {@code search transportables} button.
     */
    private ManagementButton createSearchTransportables() {
        return createButton(SearchTransportablesButton::new);
    }

    /**
     * @return the {@code always quick move} button.
     */
    private ManagementButton createAlwaysQuickMove() {
        return createButton(AlwaysQuickMoveButton::new);
    }

    /**
     * @return the {@code clear excluded slots} button.
     */
    private ManagementButton createClearExcludedSlots() {
        return createButton(managementParams -> new ClearExcludedSlotsButton(managementParams, b -> {
            holder().excludedSlots().clear();
            holder().setExcludedAll(false);
        }));
    }

    /**
     * @return the {@code bulk trade} button.
     */
    private ManagementButton createBulkTrade() {
        return createButton(BulkTradeButton::new);
    }

    /**
     * @return the {@code bulk craft} button.
     */
    private ManagementButton createBulkCraft() {
        return createButton(BulkCraftButton::new);
    }

    /**
     * @return the {@code lock inventory} button.
     */
    private ManagementButton createLockInventory() {
        return createButton(LockInventoryButton::new);
    }

    /**
     * @return if the {@code Transfer Container button} can be {@code initialized}.
     */
    private boolean canInitTransferButton() {
        return client().management().transferring.buttonOrKey()
                && (isContainerScreen() || isDropperDispenserOrHopperScreen() || isBrewingOrFurnaceScreen());
    }

    /**
     * @return if the {@code Transfer inventory} button can be {@code initialized}.
     */
    private boolean canInitTransferInventoryButton() {
        return canInitTransferButton() && !isBrewingOrFurnaceScreen();
    }

    /**
     * @return if the {@link IncludeHotbarButton} can be {@code initialized}.
     */
    private boolean canInitIncludeHotbarButton() {
        if (!isValidScreen()) {
            return false;
        }

        if (isContainerScreen() && client().management().transferring.any() || client().management().quickDrop.any()) {
            return client().buttonDisplayOptions().displayIncludeHotbar != IncludeHotbar.OFF
                    && (!isInventoryScreen() || !client().buttonDisplayOptions().displayIncludeHotbar.containerScreensOnly());
        }

        return false;
    }

    /**
     * @return if the {@link FilteringButton} can be {@code initialized}.
     */
    private boolean canInitFilteringButton() {
        return !isInventoryScreen()
                && (
                ContainerSyncer.isTrackedFilteringActive() || !client().buttonDisplayOptions().displayFiltering.filteredContainersOnly()
                        && (
                        (isContainerScreen() || isDropperDispenserOrHopperScreen()) && client().management().transferring.any()
                )
        );
    }

    /**
     * @return if the {@link QuickDropButton} can be {@code initialized}.
     */
    private boolean canInitQuickDropButton() {
        return isValidScreen()
                && (client().management().quickDrop.buttonOrKey() || (client().management().quickDrop.any() && hasAllQuickDropModifiersDown()));
    }

    /**
     * @return if the {@link SwapButton} can be {@code initialized}.
     */
    private boolean canInitSwapButton() {
        return client().management().swapping.buttonOrKey() && isContainerScreen();
    }

    /**
     * @return if the {@link SortButton} can be {@code initialized}.
     */
    private boolean canInitSortButton() {
        return client().sorting().sorting.buttonOrKey()
                && (isValidScreen() || isDropperDispenserOrHopperScreen());
    }

    /**
     * @return if the {@link AlwaysQuickMoveButton} can be {@code initialized}.
     */
    private boolean canInitAlwaysQuickMoveButton() {
        return client().buttonDisplayOptions().displayAlwaysQuickMove && isValidScreenForSingularMoving(true);
    }

    /**
     * @return if the {@link SearchTransportablesButton} can be {@code initialized}.
     */
    private boolean canInitSearchTransportablesButton() {
        if (!isValidScreen()) {
            return false;
        }

        if (
                (
                        (holder().searchFields().inventory() != null && !holder().searchFields().inventory().getValue().isEmpty())
                                || (holder().searchFields().container() != null && !holder().searchFields().container().getValue().isEmpty())
                ) && client().buttonDisplayOptions().displaySearchTransportables
                        &&
                        (
                                (client().searching().containerSearching && isContainerScreen())
                                        || (client().searching().inventorySearching && isInventoryScreen())
                        )
        ) {
            for (int i = 0; i < searchEvents().getSearchSlotCount(); i++) {
                ItemStack stack = holder().menu().getSlot(i).getItem();
                if (stack.is(ItemTags.SHULKER_BOXES) || stack.is(ItemTags.BUNDLES)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return if the {@link ClearExcludedSlotsButton} can be {@code initialized}.
     */
    private boolean canInitClearExcludedSlotsButton() {
        return isValidScreen() && client().management().dragSorting && !holder().excludedSlots().isEmpty();
    }

    /**
     * @return if the {@link BulkTradeButton} can be {@code initialized}.
     */
    private boolean canInitBulkTradeButton() {
        return client().buttonDisplayOptions().displayBulkTrade && isMerchantScreen();
    }

    /**
     * @return if the {@link BulkCraftButton} can be {@code initialized}.
     */
    private boolean canInitBulkCraftButton() {
        return client().buttonDisplayOptions().displayBulkCraft && (isCraftingScreen() || isInventoryScreen());
    }

    /**
     * @return if the {@link LockInventoryButton} can be {@code initialized}.
     */
    private boolean canInitLockInventoryButton() {
        return client().buttonDisplayOptions().displayLockInventory && isInventoryScreen();
    }

    /**
     * Adds a widget to the screen (if supplier returns true), and adds it to the dynamic buttons for reference.
     */
    public <T extends GuiEventListener & NarratableEntry> void addWidget(T widget) {
        ((ScreenInvoker) holder().screen()).addModWidget(widget);
        holder().dynamicButtons().add(widget);
    }
}
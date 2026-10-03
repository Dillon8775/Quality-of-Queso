package net.dillon.qualityofqueso.event.management;

import net.dillon.dillonlib.mixin.accessor.ScreenInvoker;
import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.eum.management.IncludeHotbar;
import net.dillon.qualityofqueso.widget.*;
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

import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.getTitleLabelY;
import static net.dillon.qualityofqueso.helper.MethodHelper.getTopPos;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModHelper.sendClientPreferencesToServer;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.hasAllQuickDropModifiersDown;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;

/**
 * Handles all widget events.
 */
public class WidgetEvents extends ManagementEvents {

    public WidgetEvents(QuesoScreenHolder screen) {
        super(screen);
    }

    /**
     * @return a safely initialized {@link QuesoButton}.
     * @param booleanSupplier if the button can be initialized or added
     * @param newButton the desired button to create
     */
    private QuesoButton initButton(BooleanSupplier booleanSupplier, QuesoButton newButton) {
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
        if (!(isValidScreen(getScreen()) || isOtherValidScreen(holder().screen()) || isMerchantScreen(getScreen()) || isCraftingScreen(getScreen()))) {
            return;
        }

        // Create the default, horizontal layout
        AbstractList<AbstractWidget> horizontalLayout = buttonLayoutFromButtonName(client().management().horizontalButtonLayout);

        // Create the vertical layout (up-down, box beside GUI)
        AbstractList<AbstractWidget> verticalLayout = buttonLayoutFromButtonName(client().management().verticalButtonLayout);

        // Construct the final layout
        AbstractList<AbstractWidget> finalLayout = client().management().layout.horizontal() ? horizontalLayout : verticalLayout;
        if (isMerchantScreen(getScreen())) {
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
                case TRANSFER_CONTAINER_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getTransferContainer());
                case TRANSFER_INVENTORY_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getTransferInventory());
                case LOCK_INVENTORY_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getLockInventory());
                case INCLUDE_HOTBAR_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getIncludeHotbar());
                case ALWAYS_QUICK_MOVE_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getAlwaysQuickMove());
                case FILTERING_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getFiltering());
                case BULK_CRAFT_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getBulkCraft());
                case QUICK_DROP_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getQuickDrop());
                case SWAP_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getSwap());
                case SEARCH_TRANSPORTABLES_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getSearchTransportables());
                case CLEAR_EXCLUDED_SLOTS_BUTTON_SERIALIZED_NAME -> finalLayout.add(holder().managementButtons().getClearExcludedSlots());
                default -> finalLayout.add(holder().managementButtons().getSort());
            }
        }

        return finalLayout;
    }

    /**
     * @return the {@code transfer container} button.
     */
    private QuesoButton createTransferContainer() {
        return new QuesoButton(
                holder().menu(),
                holder().mc().font,
                searchEvents().getSearchFieldText(),
                TRANSFER_CONTAINER_BUTTON_PATH,
                TRANSFER_CONTAINER_BUTTON_NAME,
                true,
                b -> transferEvents().transferItems(true, true),
                () -> !isContainerFull(true) && shouldButtonBeActive(false, null, true, true));
    }

    /**
     * @return the {@code transfer inventory} button.
     */
    private QuesoButton createTransferInventory() {
        return new QuesoButton(
                holder().menu(),
                holder().mc().font,
                searchEvents().getSearchFieldText(),
                TRANSFER_INVENTORY_BUTTON_PATH,
                TRANSFER_INVENTORY_BUTTON_NAME,
                true,
                b -> transferEvents().transferItems(false, true),
                () -> !isContainerFull(false) && shouldButtonBeActive(true, holder().mc().player.getInventory(), true, true)
        );
    }

    /**
     * @return the {@code include hotbar} button.
     */
    private QuesoButton createIncludeHotbar() {
        return new IncludeHotbarButton(
                holder().menu(),
                holder().mc().font,
                searchEvents().getSearchFieldText(),
                "include_hotbar",
                b -> {
                    updateClient(client -> {
                        client.management().includingHotbar = !client.management().includingHotbar;
                    });
                    sendClientPreferencesToServer();
                });
    }

    /**
     * @return the move matching items button.
     */
    private QuesoButton createFiltering() {
        return new FilteringButton(
                holder().menu(),
                holder().mc().font,
                searchEvents().getSearchFieldText(),
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
     * @return the {@code quick drop} button.
     */
    private QuesoButton createQuickDrop() {
        boolean containerScreen = isContainerScreen(holder().screen());
        return new QuickDropButton(
                holder().menu(),
                holder().mc().font,
                searchEvents().getSearchFieldText(),
                "quick_drop/",
                "quick_drop",
                b -> transferEvents().dropItems(!containerScreen),
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
    private QuesoButton createSwap() {
        return new SwapButton(
                holder().menu(),
                holder().mc().font,
                searchEvents().getSearchFieldText(),
                "swap/",
                "swap",
                b -> transferEvents().trySwap(),
                transferEvents()::canSwap
        );
    }

    /**
     * @return the {@code sort} button.
     */
    private QuesoButton createSort() {
        return new SortButton(
                holder().menu(),
                holder().mc().font,
                searchEvents().getSearchFieldText(),
                "sort/",
                "sort",
                b -> sortingEvents().trySort(),
                sortingEvents()::canSort
        );
    }

    /**
     * @return the {@code search transportables} button.
     */
    private QuesoButton createSearchTransportables() {
        return new SearchTransportablesButton(
                holder().menu(),
                holder().mc().font,
                searchEvents().getSearchFieldText(),
                "search_transportables",
                b -> SEARCHING_TRANSPORTABLES = !SEARCHING_TRANSPORTABLES
        );
    }

    /**
     * @return the {@code always quick move} button.
     */
    private QuesoButton createAlwaysQuickMove() {
        return new AlwaysQuickMoveButton(
                holder().menu(),
                holder().mc().font,
                searchEvents().getSearchFieldText(),
                "always_quick_move",
                b -> updateClient(ModClientOptions::toggleAlwaysQuickMove)
        );
    }

    /**
     * @return the {@code clear excluded slots} button.
     */
    private QuesoButton createClearExcludedSlots() {
        return new ClearExcludedSlotsButton(
                holder().menu(),
                holder().mc().font,
                searchEvents().getSearchFieldText(),
                "clear_excluded_slots",
                b -> {
                    holder().excludedSlots().clear();
                    holder().setExcludedAll(false);
                }
        );
    }

    /**
     * @return the {@code bulk trade} button.
     */
    private QuesoButton createBulkTrade() {
        return new BulkTradeButton(
                holder().menu(),
                holder().mc().font,
                searchEvents().getSearchFieldText(),
                "trade_all",
                b -> updateClient(client -> {
                    client.management().bulkTrade = !client.management().bulkTrade;
                })
        );
    }

    /**
     * @return the {@code bulk craft} button.
     */
    private QuesoButton createBulkCraft() {
        return new BulkCraftButton(
                holder().menu(),
                holder().mc().font,
                searchEvents().getSearchFieldText(),
                "craft_all",
                b -> updateClient(client -> {
                    client.management().bulkCraft = !client.management().bulkCraft;
                })
        );
    }

    /**
     * @return the {@code lock inventory} button.
     */
    private QuesoButton createLockInventory() {
        return new LockInventoryButton(
                holder().menu(),
                holder().mc().font,
                searchEvents().getSearchFieldText(),
                "lock_inventory",
                b -> {
                    updateClient(ModClientOptions::cycleLockedInventoryMode);
                    sendClientPreferencesToServer();
                }
        );
    }

    /**
     * @return if the {@code Transfer Container button} can be {@code initialized}.
     */
    private boolean canInitTransferButton() {
        return client().management().transferring.buttonOrKey()
                && (isContainerScreen(getScreen()) || isDropperDispenserOrHopperScreen(getScreen()) || isBrewingOrFurnaceScreen(getScreen()));
    }

    /**
     * @return if the {@code Transfer inventory} button can be {@code initialized}.
     */
    private boolean canInitTransferInventoryButton() {
        return canInitTransferButton() && !isBrewingOrFurnaceScreen(getScreen());
    }

    /**
     * @return if the {@link IncludeHotbarButton} can be {@code initialized}.
     */
    private boolean canInitIncludeHotbarButton() {
        if (!isValidScreen(getScreen())) {
            return false;
        }

        if (isContainerScreen(getScreen()) && client().management().transferring.any() || client().management().quickDrop.any()) {
            return client().buttonDisplayOptions().displayIncludeHotbar != IncludeHotbar.OFF
                    && (!isInventoryScreen(getScreen()) || !client().buttonDisplayOptions().displayIncludeHotbar.containerScreensOnly());
        }

        return false;
    }

    /**
     * @return if the {@link FilteringButton} can be {@code initialized}.
     */
    private boolean canInitFilteringButton() {
        return !isInventoryScreen(getScreen())
                && (
                ContainerHelper.isTrackedFilteringActive() || !client().buttonDisplayOptions().displayFiltering.filteredContainersOnly()
                        && (
                        (isContainerScreen(getScreen()) || isDropperDispenserOrHopperScreen(getScreen())) && client().management().transferring.any()
                )
        );
    }

    /**
     * @return if the {@link QuickDropButton} can be {@code initialized}.
     */
    private boolean canInitQuickDropButton() {
        return isValidScreen(getScreen())
                && (client().management().quickDrop.buttonOrKey() || (client().management().quickDrop.any() && hasAllQuickDropModifiersDown()));
    }

    /**
     * @return if the {@link SwapButton} can be {@code initialized}.
     */
    private boolean canInitSwapButton() {
        return client().management().swapping.buttonOrKey() && isContainerScreen(getScreen());
    }

    /**
     * @return if the {@link SortButton} can be {@code initialized}.
     */
    private boolean canInitSortButton() {
        return client().sorting().sorting.buttonOrKey()
                && (isValidScreen(getScreen()) || isDropperDispenserOrHopperScreen(getScreen()));
    }

    /**
     * @return if the {@link AlwaysQuickMoveButton} can be {@code initialized}.
     */
    private boolean canInitAlwaysQuickMoveButton() {
        return client().buttonDisplayOptions().displayAlwaysQuickMove && isValidScreenForSingularMoving(getScreen(), true);
    }

    /**
     * @return if the {@link SearchTransportablesButton} can be {@code initialized}.
     */
    private boolean canInitSearchTransportablesButton() {
        if (
                (
                        (holder().searchFields().inventory() != null && !holder().searchFields().inventory().getValue().isEmpty())
                                || (holder().searchFields().container() != null && !holder().searchFields().container().getValue().isEmpty())
                ) && client().buttonDisplayOptions().displaySearchTransportables
                        && ((client().searching().containerSearching && isContainerScreen(getScreen())) || (client().searching().inventorySearching && isInventoryScreen(getScreen())))
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
        return client().management().dragSorting && !holder().excludedSlots().isEmpty();
    }

    /**
     * @return if the {@link BulkTradeButton} can be {@code initialized}.
     */
    private boolean canInitBulkTradeButton() {
        return client().buttonDisplayOptions().displayBulkTrade && isMerchantScreen(getScreen());
    }

    /**
     * @return if the {@link BulkCraftButton} can be {@code initialized}.
     */
    private boolean canInitBulkCraftButton() {
        return client().buttonDisplayOptions().displayBulkCraft && (isCraftingScreen(getScreen()) || isInventoryScreen(getScreen()));
    }

    /**
     * @return if the {@link LockInventoryButton} can be {@code initialized}.
     */
    private boolean canInitLockInventoryButton() {
        return client().buttonDisplayOptions().displayLockInventory && isInventoryScreen(getScreen());
    }

    /**
     * Adds a widget to the screen (if supplier returns true), and adds it to the dynamic buttons for reference.
     */
    public <T extends GuiEventListener & NarratableEntry> void addWidget(T widget) {
        ((ScreenInvoker) holder().screen()).addModWidget(widget);
        holder().dynamicButtons().add(widget);
    }
}
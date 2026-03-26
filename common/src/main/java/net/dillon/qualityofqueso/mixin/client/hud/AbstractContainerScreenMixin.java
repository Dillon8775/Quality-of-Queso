package net.dillon.qualityofqueso.mixin.client.hud;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.option.instance.ModClientOptions;
import net.dillon.qualityofqueso.screen.gui.search.SearchField;
import net.dillon.qualityofqueso.screen.gui.widget.WidgetLayout;
import net.dillon.qualityofqueso.screen.gui.widget.WidgetLayoutHolder;
import net.dillon.qualityofqueso.screen.gui.widget.button.*;
import net.dillon.qualityofqueso.util.ContainerTracker;
import net.dillon.qualityofqueso.util.EnchantingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static net.dillon.qualityofqueso.util.AccessorUtil.*;
import static net.dillon.qualityofqueso.util.ButtonUtil.*;
import static net.dillon.qualityofqueso.util.EnchantingHelper.isEnchantmentInGroup;
import static net.dillon.qualityofqueso.util.GuiUtil.ofItalicAndGray;
import static net.dillon.qualityofqueso.util.ModUtil.*;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T>, WidgetLayoutHolder {
    @Shadow
    protected int imageWidth;
    @Shadow
    protected int topPos;
    @Shadow
    protected int titleLabelY;

    @Shadow
    @Final
    protected T menu;

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @Shadow @Nullable
    protected abstract Slot getHoveredSlot(double mouseX, double mouseY);

    @Shadow
    @Final
    protected int imageHeight;
    @Unique
    private final AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
    @Unique
    private EditBox containerSearchField;
    @Unique
    private EditBox inventorySearchField;
    @Unique
    private TransferButton transferContainerButton;
    @Unique
    private TransferButton transferInventoryButton;
    @Unique
    private TransferButton includeHotbarButton;
    @Unique
    private TransferButton fillWhatsPresentButton;
    @Unique
    private TransferButton quickDropButton;
    @Unique
    private TransferButton swapButton;
    @Unique
    private TransferButton sortButton;
    @Unique
    private TransferButton searchTransportablesButton;
    @Unique
    private TransferButton clearExcludedSlotsButton;
    @Unique
    private TransferButton alwaysQuickMoveButton;
    @Unique
    private WidgetLayout widgetLayout;
    @Unique
    private Container container;
    @Unique
    private final Set<Integer> excludedSlots = new HashSet<>();
    @Unique
    private boolean excludedAll = false;
    @Unique
    private boolean disableFillWhatsPresentOnClose = false;

    public AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    /**
     * Creates and initializes the search field and container button.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        // If it's BrewingStandScreen, fromInventory is the brewing stand's inventory
        if (this.screen instanceof BrewingStandScreen brewingStandScreen) {
            this.container = brewingStand(brewingStandScreen);
        } else if (this.screen instanceof AbstractFurnaceScreen<?> abstractFurnaceScreen) {
            this.container = abstractFurnaceScreen.getMenu().getResultSlot().container;
        } else if (this.screen instanceof DispenserScreen dispenserScreen) {
            this.container = dispenser(dispenserScreen);
        } else if (this.screen instanceof HopperScreen hopperScreen) {
            this.container = hopper(hopperScreen);
        }
        if (isContainerScreen(this.screen)) {
            // Determine fromInventory variable; if instance ShulkerBoxScreen, fromInventory is the shulker box's fromInventory
            if (this.screen instanceof ShulkerBoxScreen shulkerBoxScreen) {
                this.container = shulkerBox(shulkerBoxScreen);
            }
            // If it's GenericContainerScreen, it's the generic container (or most likely chest/barrel)'s fromInventory
            else if (this.screen instanceof ContainerScreen genericContainerScreen) {
                this.container = genericContainerScreen.getMenu().getContainer();
            }
            // Otherwise, fromInventory is null
            else {
                this.container = null;
            }

            // Handle tracked containers
            if (ContainerTracker.RETURNING_FROM_PLACEHOLDER_SCREEN) {
                ContainerTracker.RETURNING_FROM_PLACEHOLDER_SCREEN = false;
                ContainerTracker.IS_TRACKED_CONTAINER = true;
            } else if (ContainerTracker.consumePendingOpenIsTracked()) {
                ContainerTracker.IS_TRACKED_CONTAINER = true;
                if (!options().management.fillWhatsPreset) {
                    options().management.fillWhatsPreset = true;
                    ModClientOptions.CLIENT.save();
                    this.disableFillWhatsPresentOnClose = true;
                }
            } else {
                ContainerTracker.IS_TRACKED_CONTAINER = false;
            }

            if (options().searching.containerSearching) {
                this.containerSearchField = this.initializeSearchField(false);
                this.addRenderableWidget(this.containerSearchField);
            }
        } else if (isInventoryScreen(this.screen)) {
            this.container = this.minecraft.player.getInventory();
            if (options().searching.inventorySearching) {
                this.inventorySearchField = this.initializeSearchField(true);
                this.addRenderableWidget(this.inventorySearchField);
            }
        }

        // Handle excluded slots
        if (isValidScreen(this.screen) && options().management.saveExcludedSlots && this.container != null) {
            for (int i : this.excludedSlots) {
                this.excludedSlots.remove(i);
            }
            if (SAVED_EXCLUDED_SLOTS.containsKey(getTotalSlots(this.menu))) {
                this.excludedSlots.addAll(SAVED_EXCLUDED_SLOTS.get(getTotalSlots(this.menu)));
            }
        }
    }

    /**
     * Initializes a search field widget.
     */
    @Unique
    private SearchField initializeSearchField(boolean inventory) {
        return new SearchField(this.font, this.width / 2 + getBarWidth(this.imageWidth) / 2 - (inventory ? 60 : 64), this.topPos + this.titleLabelY - 2);
    }

    /**
     * @return the {@code searchField namespace} text.
     */
    @Unique
    private String getSearchFieldText() {
        return this.inventorySearchField != null ? this.inventorySearchField.getValue() : this.containerSearchField != null ? this.containerSearchField.getValue() : "";
    }

    /**
     * Drops all highlighted items.
     */
    @Unique
    private void dropItems(boolean fromInventory) {
        this.moveItems(true, true, fromInventory);
    }

    /**
     * Transfers items the original way.
     */
    @Unique
    private void transferItems(boolean toInventory) {
        this.moveItems(toInventory, false, false);
    }

    /**
     * Transfers items from one container to another.
     */
    @Unique
    private void moveItems(boolean toInventory, boolean drop, boolean fromInventory) {
        int containerSize = getContainerSize(this.container);
        int totalSlots = getTotalSlots(this.menu);

        int fromStart = toInventory ? 0 : containerSize;
        int fromEnd = toInventory ? containerSize : totalSlots;
        int toStart = toInventory ? containerSize : 0;
        int toEnd = toInventory ? totalSlots : containerSize;

        if (drop && fromInventory) {
            fromStart = 9;
            fromEnd = 45;
        }

        if (isBrewingStandScreen(this.screen)) {
            fromStart = 0;
            fromEnd = 3;
        }

        if (isFurnaceScreen(this.screen)) {
            fromStart = 2;
            fromEnd = 3;
        }

        boolean movedItem = false;

        // Normal logic (dropping and quick move)
        for (int i = fromStart; i < fromEnd; i++) {
            Slot fromSlot = this.menu.getSlot(i);
            ItemStack fromStack = fromSlot.getItem();

            // Skip player-chosen excluded slots
            if (shouldSkipSlot(fromSlot.index, this.excludedSlots)) {
                continue;
            }

            if ((this.containerSearchField != null || this.inventorySearchField != null) && !this.getSearchFieldText().isEmpty() && !this.search(this.getSearchFieldText(), fromSlot, false)) {
                continue; // Then skip container slot if query not found via search
            } else if (!options().management.includeHotbar) {
                if (drop) {
                    if (isExcludedSlot(this.screen, fromSlot.index) || isInventoryHotbarSlot(isInventoryScreen(this.screen), fromSlot.index)) {
                        continue; // If dropping from InventoryScreen, and it's an excluded slot AND fromInventory hotbar slot, skip slot and continue
                    }
                } else if (!toInventory && isHotbarSlot(fromEnd, fromSlot.index)) {
                    continue; // Otherwise, check if it's a hotbar slot in normal container
                }
            }

            // Skip items that aren't already present/filtered
            if (!drop && options().management.fillWhatsPreset && !isPresent(toInventory, this.container, this.menu, fromStack)) {
                continue;
            }

            if (!fromStack.isEmpty()) {
                for (int j = toStart; j < toEnd; j++) {
                    // Only transfer items if the query matches whatever the cursor is holding
                    ContainerInput slotActionType = drop ? ContainerInput.THROW : ContainerInput.QUICK_MOVE;
                    ItemStack cursorStack = getCursorStack(this.screen);
                    if (!cursorStack.isEmpty()) {
                        if (canMoveCursorItem(this.menu, fromSlot, cursorStack, false, toInventory)) {
                            sendClickSlotPacket(i, slotActionType);
                            movedItem = true;
                            break;
                        }
                    }
                    // If cursor has nothing in it, move all items over
                    else {
                        sendClickSlotPacket(i, slotActionType);
                        movedItem = true;
                        break;
                    }
                }
            }
        }

        if (movedItem) {
            playButtonSound(this.minecraft, drop);
        } else {
            playButtonInactiveSound(this.minecraft);
        }
    }

    /**
     * @return if a container can swap with inventory.
     */
    @Unique
    private boolean canSwap() {
        Inventory playerInventory = this.minecraft.player.getInventory();
        return SwapButton.SWAP_COOLDOWN == 0 && (getContainerSize(this.container) != 27 || isAnySlotFilled(this.menu, false, 27, 54))
                && this.menu.getCarried().isEmpty() && this.getSearchFieldText().isEmpty()
                && this.shouldButtonBeActive(false, null)
                && this.shouldButtonBeActive(true, playerInventory);
    }

    /**
     * Attempts to swap items.
     */
    @Unique
    private void trySwap() {
        if (SwapButton.SWAP_COOLDOWN == 0 && this.canSwap()) {
            swapItems(this.minecraft, this.menu, this.container, this.excludedSlots);
            playButtonSound(this.minecraft, false);
            SwapButton.resetCooldown();
        } else {
            playButtonInactiveSound(this.minecraft);
        }
    }

    /**
     * @return if a container can be sorted.
     */
    @Unique
    private boolean canSort(boolean checkForButton) {
        boolean excludedContainerSlot = false;
        for (int id : this.excludedSlots) {
            if (id <= getTotalSlots(this.menu) - 37) {
                excludedContainerSlot = true;
                break;
            }
        }
        return isContainerScreen(this.screen)
                && isAnySlotFilled(this.menu, false, 0, getContainerSize(this.container))
                && this.getSearchFieldText().isEmpty()
                && !excludedContainerSlot
                && getCursorStack(this.screen).isEmpty()
                && (!checkForButton || this.shouldButtonBeActive(false, null, false));
    }

    /**
     * Attempts to sort items.
     */
    @Unique
    private void trySort(boolean checkForButton) {
        if (this.canSort(checkForButton)) {
            sortItems(this.minecraft);
            playButtonSound(this.minecraft, false);
        } else {
            playButtonInactiveSound(this.minecraft);
        }
    }

    /**
     * @return {@code true} if the {@code fromInventory} has a match with the search query.
     */
    @Unique
    private boolean shouldButtonBeActive(boolean isPlayerInventory, @Nullable Inventory playerInventory) {
        return this.shouldButtonBeActive(isPlayerInventory, playerInventory, true);
    }

    /**
     * @return if the button should be active.
     * @param isPlayerInventory means button status is directed towards the transfer inventory button
     */
    @Unique
    private boolean shouldButtonBeActive(boolean isPlayerInventory, @Nullable Inventory playerInventory, boolean applyFillWhatsPresentFilter) {
        boolean toInventory = !isPlayerInventory;
        // Determine fromInventory size to run through
        int size = isPlayerInventory ? playerInventory.getNonEquipmentItems().size() : this.container.getContainerSize();
        int filledSlots = 0;
        if (this.screen instanceof BrewingStandScreen brewingScreen) {
            size = brewingStand(brewingScreen).getContainerSize() - 2;
        } else if (this.screen instanceof AbstractFurnaceScreen<?> abstractFurnaceScreen
                && !abstractFurnaceScreen.getMenu().getResultSlot().hasItem()) {
            return false;
        } else if (this.screen instanceof DispenserScreen dispenserScreen) {
            size = dispenser(dispenserScreen).getContainerSize();
        } else if (this.screen instanceof HopperScreen hopperScreen) {
            size = hopper(hopperScreen).getContainerSize();
        }

        for (int i = 0; i < size; i++) {
            // If slot is not empty, the button should be active
            // Increment J and make button active
            Slot slot = this.menu.getSlot(i);
            ItemStack stack = isPlayerInventory ? playerInventory.getItem(i) : slot.getItem();
            ItemStack cursorStack = getCursorStack(this.screen);
            boolean isShulkerScreen = isShulkerBoxScreen(this.screen);
            boolean isCursorShulker = isShulkerScreen && this.menu.getCarried().is(ItemTags.SHULKER_BOXES);
            boolean isStackShulker = isShulkerScreen && stack.is(ItemTags.SHULKER_BOXES);
            if (applyFillWhatsPresentFilter && options().management.fillWhatsPreset && !isPresent(toInventory, this.container, this.menu, !cursorStack.isEmpty() ? cursorStack : stack)) {
                continue;
            }
            if (!cursorStack.isEmpty()) {
                // Check if we can move the cursor stack
                boolean canMoveCursorItem = false;
                for (int k = 0; k < getTotalSlots(this.menu); k++) {
                    // We must iterate through all slots to check for hotbar slot, but we skip container slots because we don't watch to check those slots
                    // Only check inventory slots
                    if (isPlayerInventory && this.menu.getSlot(k).index < this.container.getContainerSize()) {
                        continue;
                    } else if (!isPlayerInventory && this.menu.getSlot(k).index > this.container.getContainerSize()) {
                        continue;
                    }
                    // Then we check if the cursor item is actually applicable to move, ignoring components
                    if (canMoveCursorItem(this.menu, this.menu.getSlot(k), cursorStack, true, isPlayerInventory)) {
                        canMoveCursorItem = true;
                        break;
                    }
                }
                if (canMoveCursorItem && !isCursorShulker) {
                    filledSlots++;
                }
            } else {
                if (!stack.isEmpty() && !isStackShulker) {
                    filledSlots++;
                }
            }
        }
        return filledSlots != 0 && !this.areAllSlotsUnavailable(applyFillWhatsPresentFilter, isPlayerInventory, toInventory, isPlayerInventory ? playerInventory : null);
    }

    /**
     * @return {@code true} if all slots are grayed out, or {@code unavailable.}
     */
    @Unique
    private boolean areAllSlotsUnavailable(boolean applyFillWhatsPresentFilter, boolean isPlayerInventory, boolean toInventory, @Nullable Inventory playerInventory) {
        int foundQuerys = 0;
        List<Slot> playerSlots = new ArrayList<>();
        // If checking player fromInventory, loop through all player fromInventory slots to determine if slot is unavailable
        if (isPlayerInventory) {
            for (Slot s : this.menu.slots) {
                if (s.container == playerInventory) {
                    playerSlots.add(s);
                }
            }
            for (Slot slot : playerSlots) {
                // Skip checking player-chosen excluded slots
                if (shouldSkipSlot(slot.index, this.excludedSlots)) {
                    continue;
                }
                if (this.search(this.getSearchFieldText(), slot, false)) {
                    // prevent shulker boxes from counting as a found query when in a shulker box screen
                    if (!(isShulkerBoxScreen(this.screen) && slot.getItem().is(ItemTags.SHULKER_BOXES))) {
                        if (!applyFillWhatsPresentFilter || !options().management.fillWhatsPreset || isPresent(toInventory, this.container, this.menu, slot.getItem())) {
                            foundQuerys++; // foundQuerys++; // increment J if query found in slot
                        }
                    }
                }
            }
        } else {
            // Otherwise, loop through the container fromInventory and increment J if query found inside
            for (int i = 0; i < this.container.getContainerSize(); i++) {
                Slot slot = this.menu.getSlot(i);
                // Skip checking player-chosen excluded slots
                if (shouldSkipSlot(slot.index, this.excludedSlots)) {
                    continue;
                }
                if (this.search(this.getSearchFieldText(), slot, false)) {
                    foundQuerys++;
                }
            }
        }
        // If J == 0 then no query was found (OR if ALL slots are filled in fromInventory/container), returning true for all slots are unavailable
        return foundQuerys == 0;
    }

    /**
     * @return {@code true} if an query is found from {@code searchQuery}.
     */
    @Unique
    private boolean search(String searchQuery, Slot slot, boolean dropping) {
        ItemStack stack = slot.getItem();

        // Empty slots are never searchable.
        if (stack.isEmpty()) {
            return false;
        }

        // The "search inventory" option only gates container-screen player inventory scanning.
        // InventoryScreen should always keep its own hotbar/include behavior.
        if (!options().management.includeHotbar
                && isHotbarSlot(this.menu.slots.size(), dropping ? slot.index + 1 : slot.index)
                && (!dropping || !isInventoryScreen(this.screen) || slot.index != 45)) {
            boolean applyHotbarFilter = options().accessibility.searchInventory || isInventoryScreen(this.screen);
            if (applyHotbarFilter) {
                return false;
            }
        }

        if (this.matchesQuery(searchQuery, stack)) {
            return true;
        }

        if (!options().searching.searchTransportables) {
            return false;
        }

        ItemContainerContents containerContents = stack.get(DataComponents.CONTAINER);
        if (containerContents != null && containerContents.nonEmptyItemCopyStream().anyMatch(contained -> this.matchesQuery(searchQuery, contained))) {
            return true;
        }

        BundleContents bundleContents = stack.get(DataComponents.BUNDLE_CONTENTS);
        return bundleContents != null && bundleContents.itemCopyStream().anyMatch(contained -> this.matchesQuery(searchQuery, contained));
    }

    /**
     * @return {@code true} if {@code stack} matches the provided query syntax.
     */
    @Unique
    private boolean matchesQuery(String searchQuery, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        String itemName = stack.getHoverName().getString().toLowerCase();
        String customName = stack.getCustomName() != null ? stack.getCustomName().getString().toLowerCase() : "";

        String[] terms = searchQuery.split(",");
        // If slot contains a comma, for each query searched (separated by each comma), return true if search query'namespace find an query (make slot available)
        boolean hasPositiveTerm = false;
        for (String s : terms) {
            // Initialize term variable
            String term = s;
            term = term.trim().toLowerCase();

            // As long as slot doesn't contain whatever is searched (beginning after "!"), return true (slot is available)
            if (!term.startsWith("!")) {
                hasPositiveTerm = true;
            }
            if (term.startsWith("!")) {
                String forbidden = term.substring(1);
                if (itemName.contains(forbidden) || customName.contains(forbidden)) {
                    return false;
                }
                continue; // Continue searching for the rest
            }

            // If tag contains search query and stack is in returned tag, slot is available
            if (term.startsWith("#")) {
                String tagSearch = term.substring(1).toLowerCase();

                // Return false if tag list is empty
                if (stack.tags().toList().isEmpty()) {
                    return false;
                }

                // Then search through all item's tags
                for (TagKey<Item> tag : stack.tags().toList()) {
                    Identifier location = tag.location();

                    if (location.getPath().toLowerCase().contains(tagSearch)
                            || location.toString().toLowerCase().contains(tagSearch)) {
                        return true;
                    }
                }
            }

            // Enchanted book searching logic
            if (stack.isEnchanted() || stack.is(Items.ENCHANTED_BOOK)) {
                ItemEnchantments enchantments = net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentsForCrafting(stack);
                for (Holder<Enchantment> enchantment : enchantments.keySet()) {
                    String encName = enchantment.value().description().getString();
                    String fullName = encName + " " + enchantments.getLevel(enchantment);

                    // If slot contains enchantments searched, return true (slot is available)
                    if (fullName.toLowerCase().contains(term)) {
                        return true;
                    }
                }
            }

            if (term.startsWith(":")) {
                String query = term.substring(1);
                if (itemName.matches(query) || customName.matches(query)) {
                    return true;
                }
            } else {
                if (itemName.contains(term) || customName.contains(term)) {
                    return true;
                }
            }
        }

        // If stack contains whatever is searched, return true (stack is available)
        return !hasPositiveTerm;
    }

    /**
     * Selects slots.
     */
    @Unique
    private void selectSlot(MouseButtonEvent click, CallbackInfoReturnable<Boolean> cir) {
        Slot slot = this.getHoveredSlot(click.x(), click.y());
        boolean bl = Minecraft.getInstance().hasAltDown() && !Minecraft.getInstance().hasShiftDown();
        if (slot != null) {
            if (bl && !this.excludedAll) {
                for (Slot s : this.menu.slots) {
                    this.excludedSlots.add(s.index);
                }
                this.excludedAll = true;
            }
            if (click.button() == 1) {
                if (bl) {
                    this.excludedSlots.add(slot.index);
                } else {
                    this.excludedSlots.remove(slot.index);
                }
            } else {
                if (bl) {
                    this.excludedSlots.remove(slot.index);
                } else {
                    this.excludedSlots.add(slot.index);
                }
            }
            cir.setReturnValue(true);
        }
    }

    /**
     * Grays out any containerSlot which doesn't contain the query name being searched.
     */
    @Inject(method = "extractContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractSlotHighlightFront(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V", shift = At.Shift.AFTER))
    private void grayOutSlot(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!isValidScreen(this.screen)) {
            return;
        }

        boolean inventorySearchFieldPresent = this.inventorySearchField != null;
        for (int i = 0; i < this.getSearchSlotCount(); i++) {
            Slot slot = this.menu.getSlot(i);
            // Gray out hotbar slots if include hotbar is off and one of the transfer buttons are hovered
            boolean alreadyExcluded = false;
            if ((this.containerSearchField != null || inventorySearchFieldPresent)
                    && !this.getSearchFieldText().isEmpty()
                    && !this.search(this.getSearchFieldText(), slot, inventorySearchFieldPresent)) {
                renderSlotUnavailable(graphics, slot, false);
                alreadyExcluded = true;
            }
            // Otherwise, gray out slots that don't match the search
            else if (!options().management.includeHotbar
                    && (isInventoryScreen(this.screen) ? isInventoryHotbarSlot(true, slot.index) : isHotbarSlot(this.menu.slots.size(), slot.index))
                    && options().management.transferring.orKeyOnly()) {
                if (shouldGrayout(this.screen, this.transferInventoryButton, this.transferContainerButton, this.includeHotbarButton, this.quickDropButton, slot)) {
                    renderSlotUnavailable(graphics, slot, slot.hasItem());
                    alreadyExcluded = true;
                }
            }
            // Gray out player-chosen excluded slots
            if (!alreadyExcluded && options().management.dragSorting) {
                for (int id : this.excludedSlots) {
                    if (slot.index == id) {
                        if (isContainerScreen(this.screen)) {
                            // Don't grayout if CTRL is pressed and transfer keys are bounded
                            if (Minecraft.getInstance().hasControlDown()
                                    && key(ModKeybinds.MOVE_CONTAINER).getValue() != InputConstants.UNKNOWN.getValue()
                                    && key(ModKeybinds.MOVE_INVENTORY).getValue() != InputConstants.UNKNOWN.getValue()) {
                                break;
                            } else if (buttonHoveredActiveOrShiftHeld(this.screen, this.transferContainerButton, false)) {
                                if (slot.index <= getTotalSlots(this.menu) - 37) {
                                    renderSlotUnavailable(graphics, slot, false);
                                }
                            } else if (buttonHoveredActiveOrShiftHeld(this.screen, this.transferInventoryButton, true)) {
                                if (slot.index >= getTotalSlots(this.menu) - 36) {
                                    renderSlotUnavailable(graphics, slot, false);
                                }
                            } else {
                                renderSlotUnavailable(graphics, slot, false);
                            }
                        } else {
                            renderSlotUnavailable(graphics, slot, false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles rendering, such as the search field and transferring fromInventory button textures.
     */
    @Inject(method = "extractContents", at = @At("TAIL"))
    private void renderWidgets(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        // Render the search field
        if (this.containerSearchField != null) {
            this.containerSearchField.extractWidgetRenderState(graphics, mouseX, mouseY, deltaTicks);
        }
        // Render fromInventory search field
        if (this.inventorySearchField != null) {
            this.inventorySearchField.setX(this.width / 2 + getBarWidth(this.imageWidth) / 2 - (
                    this.screen instanceof AbstractRecipeBookScreen<?> recipeBookScreen && getRecipeBookComponent(recipeBookScreen).isVisible() ? -16 : 60
            ));
            this.inventorySearchField.extractWidgetRenderState(graphics, mouseX, mouseY, deltaTicks);
        }

        Inventory playerInventory = this.minecraft.player.getInventory();
        boolean containerScreen = isContainerScreen(this.screen);
        boolean inventoryScreen = isInventoryScreen(this.screen);
        boolean validScreen = containerScreen || inventoryScreen;
        if (options().management.transferring.shortcutOrButton()) {

            // TRANSFER CONTAINER BUTTON (container -> inventory)
            if (containerScreen || isBrewingStandScreen(this.screen) || isFurnaceScreen(this.screen) || isDispenserScreen(this.screen) || isHopperScreen(this.screen)) {
                this.transferContainerButton = this.addWidget(
                        new TransferButton(
                                this.menu,
                                this.font,
                                this.getSearchFieldText(),
                                "transfer/container/",
                                "transfer_container",
                                true,
                                b -> this.transferItems(true),
                                () -> !isContainerFull(this.menu, this.container, true) && this.shouldButtonBeActive(false, null)));
            }

            // TRANSFER INVENTORY BUTTON (inventory -> container)
            if (containerScreen) {
                this.transferInventoryButton = this.addWidget(
                        new TransferButton(
                                this.menu,
                                this.font,
                                this.getSearchFieldText(),
                                "transfer/inventory/",
                                "transfer_inventory",
                                true,
                                b -> this.transferItems(false),
                                () -> !isContainerFull(this.menu, this.container, false) && this.shouldButtonBeActive(true, playerInventory)
                        ));
            }
        }

        // ALWAYS QUICK MOVE BUTTON
        if (containerScreen) {
            this.alwaysQuickMoveButton = this.addWidget(
                    new AlwaysQuickMoveButton(
                            this.menu,
                            this.font,
                            this.getSearchFieldText(),
                            "always_quick_move",
                            b -> {
                                options().management.alwaysQuickMove = !options().management.alwaysQuickMove;
                                ModClientOptions.CLIENT.save();
                            }
                    ));
        }

        /* --- */

        if (validScreen) {

            // INCLUDE HOTBAR BUTTON
            if ((containerScreen && options().management.transferring.orKeyOnly()) || options().management.quickDrop.orKeyOnly()) {
                this.includeHotbarButton = this.addWidget(
                        new IncludeHotbarButton(
                                this.menu,
                                this.font,
                                this.getSearchFieldText(),
                                "include_hotbar",
                                b -> {
                                    options().management.includeHotbar = !options().management.includeHotbar;
                                    ModClientOptions.CLIENT.save();
                                    sendClientOptionsToServer();
                                }));
            }

            // MOVE MATCHING ITEMS BUTTON
            if (containerScreen && options().management.containerFiltering && options().management.transferring.orKeyOnly()) {
                this.fillWhatsPresentButton = this.addWidget(
                    new FillWhatsPresentButton(
                            this.menu,
                            this.font,
                            this.getSearchFieldText(),
                            "fill_whats_present",
                            b -> {
                                if (!ContainerTracker.IS_TRACKED_CONTAINER) {
                                    options().management.fillWhatsPreset = !options().management.fillWhatsPreset;
                                    ModClientOptions.CLIENT.save();
                                }
                            },
                            this.minecraft,
                            this.screen));
            }

            // SORT BUTTON
            if (options().management.containerSorting.shortcutOrButton() && containerScreen) {
                this.sortButton = this.addWidget(
                        new SortButton(
                                this.menu,
                                this.font,
                                this.getSearchFieldText(),
                                "sort/",
                                "sort",
                                b -> this.trySort(true),
                                () -> this.canSort(true)
                        ));
            }

            // SEARCH INSIDE TRANSPORTABLE CONTAINERS BUTTON
            if ((options().searching.containerSearching && containerScreen) || (options().searching.inventorySearching && inventoryScreen)) {
                boolean canRenderTransportablesButton = false;
                for (int i = 0; i < this.getSearchSlotCount(); i++) {
                    ItemStack stack = this.menu.getSlot(i).getItem();
                    if (stack.is(ItemTags.SHULKER_BOXES) || stack.is(ItemTags.BUNDLES)) {
                        canRenderTransportablesButton = true;
                        break;
                    }
                }

                if (canRenderTransportablesButton) {
                    this.searchTransportablesButton = this.addWidget(
                            new SearchTransportablesButton(
                                    this.menu,
                                    this.font,
                                    this.getSearchFieldText(),
                                    "search_transportables",
                                    b -> {
                                        options().searching.searchTransportables = !options().searching.searchTransportables;
                                        ModClientOptions.CLIENT.save();
                                    }
                            ));
                } else {
                    this.searchTransportablesButton = null;
                }
            }

            // QUICK DROP BUTTON
            if (options().management.quickDrop.shortcutOrButton() || (options().management.quickDrop.orKeyOnly() && Minecraft.getInstance().hasControlDown() && Minecraft.getInstance().hasAltDown())) {
                this.quickDropButton = this.addWidget(
                        new QuickDropButton(
                                this.menu,
                                this.font,
                                this.getSearchFieldText(),
                                "quick_drop/",
                                "quick_drop",
                                b -> this.dropItems(!containerScreen),
                                () -> (isInventoryScreen(this.screen) ?
                                        isAnySlotFilled(this.menu, true, 9, 36) :
                                        isAnySlotFilled(this.menu, false, 0, getContainerSize(this.container)))
                                        && this.menu.getCarried().isEmpty()
                                        && this.shouldButtonBeActive(!containerScreen, containerScreen ? null : playerInventory, false)
                        ));
            } else {
                this.quickDropButton = null;
            }

            // SWAP BUTTON
            if (options().management.swapping.shortcutOrButton() && containerScreen) {
                this.swapButton = this.addWidget(
                        new SwapButton(
                                this.menu,
                                this.font,
                                this.getSearchFieldText(),
                                "swap/",
                                "swap",
                                b -> this.trySwap(),
                                this::canSwap
                        ));
            }

            // CLEAR EXCLUDED SLOTS BUTTON
            if (options().management.dragSorting && !this.excludedSlots.isEmpty()) {
                this.clearExcludedSlotsButton = this.addWidget(
                        new ClearExcludedSlotsButton(
                                this.menu,
                                this.font,
                                this.getSearchFieldText(),
                                "clear_excluded_slots",
                                b -> {
                                    this.excludedSlots.clear();
                                    this.excludedAll = false;
                                }
                        )
                );
            } else {
                this.clearExcludedSlotsButton = null;
            }

            // Render Button Layout
            AbstractList<AbstractWidget> verticalLayout = NonNullList.of(
                    null,
                    this.transferInventoryButton,
                    this.transferContainerButton,

                    this.includeHotbarButton,
                    this.alwaysQuickMoveButton,

                    this.sortButton,
                    this.fillWhatsPresentButton,

                    this.quickDropButton,
                    this.swapButton,

                    this.searchTransportablesButton,
                    this.clearExcludedSlotsButton
            );

            AbstractList<AbstractWidget> horizontalLayout = NonNullList.of(
                    null,
                    this.transferContainerButton,
                    this.transferInventoryButton,
                    this.includeHotbarButton,
                    this.alwaysQuickMoveButton,
                    this.fillWhatsPresentButton,
                    this.sortButton,
                    this.searchTransportablesButton,
                    this.swapButton,
                    this.quickDropButton,
                    this.clearExcludedSlotsButton
            );

            this.setWidgetLayout(WidgetLayout.initializeLayout(this.screen,
                    inventoryScreen ? this.inventorySearchField : this.containerSearchField,
                    this.container, this.topPos, this.titleLabelY, options().management.horizontalLayout ? horizontalLayout : verticalLayout
            ));
            this.getWidgetLayout().extractRenderState(graphics, mouseX, mouseY, deltaTicks);
        }
    }

    /**
     * @return the widget layout.
     */
    @Override
    public WidgetLayout getWidgetLayout() {
        return this.widgetLayout;
    }

    /**
     * Sets the widget layout for other screens to reference.
     */
    @Override
    public void setWidgetLayout(WidgetLayout layout) {
        this.widgetLayout = layout;
    }

    /**
     * Allows clicking on the vertical box outside of the GUI screen.
     */
    @Inject(method = "hasClickedOutside", at = @At("HEAD"), cancellable = true)
    private void hasClickedOnBoxInContainer(double mx, double my, int xo, int yo, CallbackInfoReturnable<Boolean> cir) {
        WidgetLayout.hasClickedOnBox(mx, my, this.widgetLayout, cir);
    }

    /**
     * @return the slot count that should be considered for searching/highlighting on the current screen.
     */
    @Unique
    private int getSearchSlotCount() {
        if (isInventoryScreen(this.screen)) {
            return this.menu.slots.size();
        }
        return getInventorySize(this.menu, this.container);
    }

    /**
     * Renders tag tooltips to all slots if searching by tag {@code searchQuery.startsWith(#)}.
     */
    @Inject(method = "extractTooltip", at = @At("HEAD"), cancellable = true)
    private void modifyTooltips(GuiGraphicsExtractor graphics, int x, int y, CallbackInfo ci) {
        if (modEnabled(this.minecraft) && isExcludingSlots(this.screen)) {
            ci.cancel();
        }

        Slot hoveredSlot = this.hoveredSlot;
        if (hoveredSlot == null || !hoveredSlot.hasItem()) {
            return;
        }

        ItemStack stack = hoveredSlot.getItem();
        List<Component> originalTooltip = stack.getTooltipLines(Item.TooltipContext.EMPTY, this.minecraft.player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);

        if (options().misc.enchantingHelper && stack.is(Items.ENCHANTED_BOOK)) {
            ItemEnchantments enchantments = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
            Set<Component> enchantmentApplicables = new HashSet<>();

            for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : enchantments.entrySet()) {
                if (isEnchantmentInGroup(EnchantingHelper.ALL_PURPOSE_ENCHANTMENTS, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray("Any tool, weapon or armor piece"));
                }

                if (isEnchantmentInGroup(EnchantingHelper.ARMOR_ENCHANTMENTS, enchantment) || enchantment.getKey().is(EnchantmentTags.ARMOR_EXCLUSIVE)) {
                    enchantmentApplicables.add(ofItalicAndGray("Armor"));
                }

                if (isEnchantmentInGroup(EnchantingHelper.HELMET_EXCLUSIVE, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray("Helmets"));
                }

                if (isEnchantmentInGroup(EnchantingHelper.LEGS_EXCLUSIVE, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray("Leggings"));
                }

                if (isEnchantmentInGroup(EnchantingHelper.BOOTS_EXCLUSIVE, enchantment) || enchantment.getKey().is(EnchantmentTags.BOOTS_EXCLUSIVE)) {
                    enchantmentApplicables.add(ofItalicAndGray("Boots"));
                }

                if (isEnchantmentInGroup(EnchantingHelper.SWORDS, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray("Swords"));
                }

                if (isEnchantmentInGroup(EnchantingHelper.SPEARS, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray("Spears"));
                }

                if (isEnchantmentInGroup(EnchantingHelper.MACES, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray("Maces"));
                }

                if (isEnchantmentInGroup(EnchantingHelper.TRIDENTS, enchantment) || enchantment.getKey().is(EnchantmentTags.RIPTIDE_EXCLUSIVE)) {
                    enchantmentApplicables.add(ofItalicAndGray("Tridents"));
                }

                if (isEnchantmentInGroup(EnchantingHelper.BOWS, enchantment) || enchantment.getKey().is(EnchantmentTags.BOW_EXCLUSIVE)) {
                    enchantmentApplicables.add(ofItalicAndGray("Bows"));
                }

                if (isEnchantmentInGroup(EnchantingHelper.CROSSBOWS, enchantment) || enchantment.getKey().is(EnchantmentTags.CROSSBOW_EXCLUSIVE)) {
                    enchantmentApplicables.add(ofItalicAndGray("Crossbows"));
                }

                if (isEnchantmentInGroup(EnchantingHelper.TOOLS, enchantment) || enchantment.getKey().is(EnchantmentTags.MINING_EXCLUSIVE)) {
                    enchantmentApplicables.add(ofItalicAndGray("Tools"));
                }

                if (isEnchantmentInGroup(EnchantingHelper.FISHING_RODS, enchantment)) {
                    enchantmentApplicables.add(ofItalicAndGray("Fishing Rods"));
                }
            }

            int index = Minecraft.getInstance().options.advancedItemTooltips ? originalTooltip.size() - 2 : originalTooltip.size();
            for (Component c : enchantmentApplicables) {
                originalTooltip.add(index, c);
            }
            originalTooltip.add(index, Component.literal("Applicable on:"));

            graphics.setTooltipForNextFrame(this.font, originalTooltip, Optional.empty(), x, y);
            ci.cancel();
        }

        String searchQuery = this.getSearchFieldText();
        if (this.containerSearchField != null || this.inventorySearchField != null) {
            // Exit if search query doesn't start with #
            if (!searchQuery.startsWith("#")) {
                return;
            }

            // Loop through item's tags
            for (TagKey<Item> tag : stack.tags().toList()) {
                // Add each tag to the query hovered
                String location = tag.location().getNamespace().equals("c") ? "fabric:" + tag.location().getPath() : tag.location().toString();
                String tagString = "#" + location;
                originalTooltip.add(1, Component.literal(tagString).withStyle(ChatFormatting.LIGHT_PURPLE));
            }

            // If tags were found in the query add it to the tooltip and render
            // cancel out original method to prevent overlapping tooltips
            if (stack.tags().toList().isEmpty()) {
                originalTooltip.add(1, Component.translatable("qualityofqueso.gui.no_tags_found").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
            }
            graphics.setTooltipForNextFrame(this.font, originalTooltip, Optional.empty(), x, y);
            ci.cancel();
        }
    }

    /**
     * Closes the screen when clicking outside of menu.
     */
    @Inject(method = "slotClicked", at = @At("HEAD"))
    private void closeButtonOnClickOutOfBounds(Slot slot, int slotId, int button, ContainerInput
            actionType, CallbackInfo ci) {
        if (modEnabled(this.minecraft) && options().misc.quickGuiExit && this.menu.getCarried().isEmpty() && button == 0 && slot == null) {
            this.onClose();
        }
    }

    /**
     * Always quickly moves items if the option is enabled.
     */
    @Redirect(method = {"mouseClicked", "mouseReleased"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/MouseButtonEvent;hasShiftDown()Z"))
    private boolean alwaysQuickMove(MouseButtonEvent event) {
        return canQuickMove(this.screen, event);
    }

    /**
     * Implements functionality for the {@code Quick Equip right-click feature,} and handles fromInventory search field.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleMouseClicking(MouseButtonEvent event, boolean isDouble, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        if (isExcludingSlots((AbstractContainerScreen<?>)(Object)this)) {
            this.selectSlot(event, cir);
        }
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && this.hoveredSlot != null && isQuicklyEquippable(this.hoveredSlot.getItem())) {
            quickEquip(this.screen, this.hoveredSlot);
            cir.setReturnValue(true);
        }
        // Refocus fromInventory search field if clicked.
        if (this.inventorySearchField != null && this.inventorySearchField.mouseClicked(event, isDouble)) {
            this.inventorySearchField.setFocused(true);
        }

        if (buttonHoveredButInactive(this.transferContainerButton)
                || buttonHoveredButInactive(this.transferInventoryButton)
                || buttonHoveredButInactive(this.quickDropButton)
                || buttonHoveredButInactive(this.sortButton)
                || buttonHoveredButInactive(this.swapButton)
        ) {
            playButtonInactiveSound(this.minecraft);
        }
    }

    /**
     * Drag-sort feature, where you can exclude slots by clicking on them.
     */
    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void dragToExclude(MouseButtonEvent event, double offsetX, double offsetY, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        if (isExcludingSlots((AbstractContainerScreen<?>)(Object)this)) {
            this.selectSlot(event, cir);
        }
    }

    /**
     * Handles key pressing correctly and implements functionality for the {@link ModKeybinds#QUICK_EQUIP} keybind.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handleKeyPressing(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        // Prevent inventory key from closing automatically if search bar is focused
        if (input.key() == key(Minecraft.getInstance().options.keyInventory).getValue()) {
            if ((this.containerSearchField != null && !this.containerSearchField.isFocused()) || (this.inventorySearchField != null && !this.inventorySearchField.isFocused()) || (isInventoryScreen(this.screen) && this.inventorySearchField == null)) {
                this.onClose();
                cir.setReturnValue(true);
            }
        }

        if (Minecraft.getInstance().hasControlDown()) {
            if (this.screen instanceof AbstractRecipeBookScreen<?> recipeScreen
                    && input.key() == key(ModKeybinds.HIDE_RECIPE_BOOK).getValue()
                    && getRecipeBookComponent(recipeScreen).isVisible()) {
                String text = this.getSearchFieldText();
                getRecipeBookComponent(recipeScreen).toggleVisibility();
                this.repositionElements();
                if (this.inventorySearchField != null) {
                    this.inventorySearchField.setValue(text);
                }
                cir.setReturnValue(true);
            }
            if ((isValidScreen(this.screen) || isBrewingStandScreen(this.screen) || isFurnaceScreen(this.screen) || isDispenserScreen(this.screen) || isHopperScreen(this.screen)) && options().management.transferring.orKeyOnly()) {
                if (input.key() == key(ModKeybinds.MOVE_CONTAINER).getValue()) {
                    this.transferItems(true);
                }
                if (input.key() == key(ModKeybinds.MOVE_INVENTORY).getValue()) {
                    this.transferItems(false);
                }
            }
            if (isValidScreen(this.screen)) {
                if (options().management.containerSorting.orKeyOnly() && input.key() == key(ModKeybinds.SORT_CONTAINER).getValue()) {
                    this.trySort(false);
                }
                if (options().management.swapping.orKeyOnly() && input.key() == key(ModKeybinds.SWAP_ITEMS).getValue()) {
                    this.trySwap();
                }
                if (options().management.quickDrop.orKeyOnly() && Minecraft.getInstance().hasAltDown() && input.key() == GLFW.GLFW_KEY_Q) {
                    this.dropItems(!isContainerScreen(this.screen));
                }
            }
        }

        // Quick equip logic
        if (input.key() == key(ModKeybinds.QUICK_EQUIP).getValue()) {
            quickEquip(this.screen, this.hoveredSlot);
            if (hoveredSlotHasItem(this.hoveredSlot) && this.inventorySearchField != null && this.inventorySearchField.isFocused()) {
                this.inventorySearchField.setFocused(false);
            }
            if (isInventoryScreen(this.screen)) {
                return;
            }
        }

        // Prevent E from typing entirely in fromInventory screens
        boolean canCloseFromE = (this.containerSearchField != null && !this.containerSearchField.isFocused()) || (this.inventorySearchField != null && !this.inventorySearchField.isFocused());
        if ((input.key() == key(Minecraft.getInstance().options.keyInventory).getValue() && options().accessibility.preventEFromTyping && canCloseFromE)
                && (isContainerScreen(this.screen) || isInventoryScreen(this.screen) || isCreativeInventoryScreen(this.screen))) {
            this.onClose();
            cir.setReturnValue(true);
        }

        // Declare typing variables
        // Both of these variables apply to disallowed keys and hotbar switching
        boolean ignoreTyping = hoveredSlotHasItem(this.hoveredSlot); // Basic ignore typing variable; applies to recipe book screens only.
        boolean secondaryIgnoreTyping = false; // Secondary ignore typing variable; applies to "T" and "E" keys and chest searching screens only.

        // These variables only apply to the chest search bar
        boolean numberKeyPressed = false; // Determines if a number key was pressed
        boolean hotbarKeyPressed = false; // Determines if a hotbar key was pressed
        boolean dropKeyPressed = false; // Determines if the drop key was pressed
        boolean swapKeyPressed = false; // Determines if the swap item key was pressed

        // If a "disallowed key" is pressed, ignoreTyping and secondaryIgnoreTyping become true.
        for (int key : allDisallowedKeys) {
            if (input.key() == key) {
                ignoreTyping = true;
                secondaryIgnoreTyping = true;
                break;
            }
        }

        // handle switching items from hotbar to another containerSlot in fromInventory; cancel out typing if an query can be moved
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
            for (int i = 0; i < 9; i++) {
                if (this.minecraft.options.keyHotbarSlots[i].matches(input)) {
                    ignoreTyping = true;
                    secondaryIgnoreTyping = true;
                    hotbarKeyPressed = true;
                    break;
                }
            }
            if (this.screen instanceof InventoryScreen && input.key() == key(ModKeybinds.QUICK_EQUIP).getValue()) {
                ignoreTyping = true;
            }
        }

        // Handle 'T' and 'E' keys
        for (int key : popularKeys) {
            if (input.key() == key) {
                secondaryIgnoreTyping = false;
                cir.setReturnValue(true);
                break;
            }
        }

        // Check if number key was pressed
        List<Integer> numbers = List.of(
                GLFW.GLFW_KEY_1,
                GLFW.GLFW_KEY_2,
                GLFW.GLFW_KEY_3,
                GLFW.GLFW_KEY_4,
                GLFW.GLFW_KEY_5,
                GLFW.GLFW_KEY_6,
                GLFW.GLFW_KEY_7,
                GLFW.GLFW_KEY_8,
                GLFW.GLFW_KEY_9
        );
        for (int key : numbers) {
            if (input.key() == key) {
                numberKeyPressed = true;
                break;
            }
        }

        // Check if drop key or swap hands key was pressed
        if (input.key() == key(Minecraft.getInstance().options.keyDrop).getValue()) {
            secondaryIgnoreTyping = true;
            dropKeyPressed = true;
        } else if (input.key() == key(Minecraft.getInstance().options.keySwapOffhand).getValue()) {
            secondaryIgnoreTyping = true;
            swapKeyPressed = true;
        }

        // If any of these are true, the user cannot type in the search field
        boolean cannotType = (numberKeyPressed || hotbarKeyPressed || dropKeyPressed || swapKeyPressed) && hoveredSlotHasItem(this.hoveredSlot);

        // Recipe book search field logic
        if (options().searching.quickSearch && this.screen instanceof AbstractRecipeBookScreen<?> recipeScreen && !Minecraft.getInstance().hasControlDown()) {
            boolean swapKeyValid = swapKeyPressed && (hoveredSlotHasItem(this.hoveredSlot) || this.menu.getSlot(45).hasItem());
            if (input.key() != key(Minecraft.getInstance().options.keyInventory).getValue()) {
                if (!ignoreTyping && !swapKeyValid && !getRecipeBookComponent(recipeScreen).isVisible() && (this.inventorySearchField == null || (!this.inventorySearchField.isFocused() && options().accessibility.autoFocusIntoRecipeBook))) {
                    getRecipeBookComponent(recipeScreen).toggleVisibility();
                    this.repositionElements();
                }
                if (getSearchBoxInsideRecipeBook(recipeScreen) != null) {
                    boolean unfocus = false;
                    for (int i = 0; i < 9; i++) {
                        if (this.hoveredSlot != null && this.minecraft.options.keyHotbarSlots[i].matches(input)) {
                            unfocus = true;
                            break;
                        }
                    }
                    if (Minecraft.getInstance().hasShiftDown() || unfocus) {
                        getSearchBoxInsideRecipeBook(recipeScreen).setFocused(false);
                        return;
                    }
                    if (!cannotType && this.inventorySearchField == null) {
                        getSearchBoxInsideRecipeBook(recipeScreen).setFocused(true);
                    } else {
                        getRecipeBookComponent(recipeScreen).setFocused(!cannotType);
                    }

                    if (getSearchBoxInsideRecipeBook(recipeScreen).isFocused()) {
                        cir.setReturnValue(getRecipeBookComponent(recipeScreen).keyPressed(input) || super.keyPressed(input));
                    }
                }
            }
        }
        // Inventory search field logic
        if (options().searching.inventorySearching && this.inventorySearchField != null) {
            if (!Minecraft.getInstance().hasControlDown() && this.screen instanceof AbstractRecipeBookScreen<?> recipeScreen && getRecipeBookComponent(recipeScreen).isVisible() && !this.inventorySearchField.isFocused()) {
                getSearchBoxInsideRecipeBook(recipeScreen).setFocused(!cannotType);
            } else if (options().searching.quickSearch && !secondaryIgnoreTyping && (!Minecraft.getInstance().hasControlDown() || (Minecraft.getInstance().hasControlDown() && input.key() == GLFW.GLFW_KEY_A))) {
                this.inventorySearchField.setFocused(true);
                this.setFocused(this.inventorySearchField);
            } else if (this.inventorySearchField.isFocused() && cannotType) {
                this.inventorySearchField.setFocused(false);
            }

            // Unfocus recipe book search field when fromInventory search field is focused
            if (this.screen instanceof AbstractRecipeBookScreen<?> recipeScreen && getSearchBoxInsideRecipeBook(recipeScreen) != null) {
                if (this.inventorySearchField.isFocused()) {
                    getSearchBoxInsideRecipeBook(recipeScreen).setFocused(false);
                    if (input.key() == GLFW.GLFW_KEY_BACKSPACE && getRecipeBookComponent(recipeScreen).isVisible()) {
                        String text = this.getSearchFieldText();
                        getRecipeBookComponent(recipeScreen).toggleVisibility();
                        this.repositionElements();
                        this.inventorySearchField.setValue(text.substring(0, text.length() - 1));
                        this.inventorySearchField.setFocused(true);
                        this.setFocused(this.inventorySearchField);
                    }
                    return;
                }
                // Unfocus fromInventory search field when recipe book search field is focused
                else if (getSearchBoxInsideRecipeBook(recipeScreen).isFocused() && this.inventorySearchField != null) {
                    this.inventorySearchField.setFocused(false);
                }
            }

            if (this.inventorySearchField.isFocused() && this.inventorySearchField.keyPressed(input)) {
                cir.setReturnValue(true);
            }
        }

        // Chest search field logic
        if (options().searching.containerSearching && isContainerScreen(this.screen)) {
            if (options().searching.quickSearch && !secondaryIgnoreTyping && (!Minecraft.getInstance().hasControlDown() || (Minecraft.getInstance().hasControlDown() && input.key() == GLFW.GLFW_KEY_A))) {
                this.containerSearchField.setFocused(true);
            } else if (this.containerSearchField.isFocused() && cannotType) {
                this.containerSearchField.setFocused(false);
            }

            if (this.containerSearchField.isFocused() && this.containerSearchField.keyPressed(input)) {
                cir.setReturnValue(true);
            }
        }
    }

    /**
     * Allows correct functionality for typing into search field without clicking on it.
     */
    @Override
    public boolean charTyped(CharacterEvent input) {
        if (options().searching.containerSearching && this.containerSearchField != null && this.containerSearchField.isFocused()) {
            return this.containerSearchField.charTyped(input);
        }
        if (isInventoryScreen(this.screen)) {
            if (this.screen instanceof AbstractRecipeBookScreen<?> recipeScreen
                    && getRecipeBookComponent(recipeScreen).isVisible()
                    && this.inventorySearchField != null
                    && this.inventorySearchField.isFocused()) {
                String text = this.getSearchFieldText();
                getRecipeBookComponent(recipeScreen).toggleVisibility();
                this.repositionElements();
                this.inventorySearchField.setValue(text + input.codepointAsString());
                this.inventorySearchField.setFocused(true);
            }
        }
        return super.charTyped(input);
    }

    /**
     * Saves the current text in {@code searchField.getText()} to memory so it can be referenced when opening a container screen again.
     */
    @Inject(method = "onClose", at = @At("TAIL"))
    private void injectClose(CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        if (options().management.saveExcludedSlots && this.menu != null) {
            SAVED_EXCLUDED_SLOTS.put(getTotalSlots(this.menu), this.excludedSlots);
        }

        if (options().searching.saveSearchText) {
            if (isInventoryScreen(this.screen) && this.inventorySearchField != null) {
                SAVED_TEXT = this.inventorySearchField.getValue();
            } else if (isContainerScreen(this.screen) && this.containerSearchField != null) {
                SAVED_TEXT = this.containerSearchField.getValue();
            }
        }

        if (options().accessibility.autoCloseRecipeBook
                && this.screen instanceof AbstractRecipeBookScreen<?> recipeBookScreen
                && getRecipeBookComponent(recipeBookScreen).isVisible()) {
            getRecipeBookComponent(recipeBookScreen).toggleVisibility();
            this.repositionElements();
        }

        if (this.disableFillWhatsPresentOnClose) {
            options().management.fillWhatsPreset = false;
            ModClientOptions.CLIENT.save();
        }

        if (isContainerScreen(this.screen)) {
            if (ContainerTracker.OPENING_PLACEHOLDER_SCREEN) {
                ContainerTracker.OPENING_PLACEHOLDER_SCREEN = false;
                return;
            }
            ContainerTracker.clearActiveContainer();
            ContainerTracker.IS_TRACKED_CONTAINER = false;
        }
    }

    /**
     * Ensures variables and stored values aren't lost during resizing of window.
     */
    @Override
    public void resize(int width, int height) {
        if (this.containerSearchField != null) {
            // Get current text and focused status
            String text = this.getSearchFieldText();
            boolean refocus = this.containerSearchField.isFocused();
            // Gets current tracked container
            boolean trackedContainer = ContainerTracker.IS_TRACKED_CONTAINER;
            // Prevents ConcurrentModificationException
            Set<Integer> temp = new HashSet<>(this.excludedSlots);
            this.excludedSlots.clear();
            // Refresh screen (or resize)
            this.init(width, height);
            this.excludedSlots.addAll(temp);
            // Reset text and focused status
            this.containerSearchField.setValue(text);
            this.containerSearchField.setFocused(refocus);
            // Reset tracked container
            ContainerTracker.IS_TRACKED_CONTAINER = trackedContainer;
        }
    }
}
